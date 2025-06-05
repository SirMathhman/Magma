package magma;

import magma.option.None;
import magma.option.Option;
import magma.option.Some;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;
import magma.result.Results;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Utility for generating TypeScript stub files mirroring the Java sources.
 * Instances contain no state and all methods are static.
 */
public final class TypeScriptStubs {
    private TypeScriptStubs() {
    }

    public static Option<IOException> write(PathLike javaRoot, PathLike tsRoot) {
        List<PathLike> files;
        var walkRes = javaRoot.walk();
        if (walkRes.isErr()) {
            return new Some<>(((Err<Stream<PathLike>, IOException>) walkRes).error());
        }
        try (Stream<PathLike> stream = ((Ok<Stream<PathLike>, IOException>) walkRes).value()) {
            files = stream.filter(PathLike::isRegularFile)
                    .filter(p -> p.toString().endsWith(".java"))
                    .toList();
        }

        for (PathLike file : files) {
            PathLike relative = javaRoot.relativize(file);
            PathLike tsFile = tsRoot.resolve(relative.toString().replaceFirst("\\.java$", ".ts"));
            var dirResult = tsFile.getParent().createDirectories();
            if (dirResult.isPresent()) {
                return dirResult;
            }

            var importsRes = readImports(file);
            if (importsRes.isErr()) {
                return new Some<>(((Err<List<String>, IOException>) importsRes).error());
            }

            var declarationsRes = readDeclarations(file);
            if (declarationsRes.isErr()) {
                return new Some<>(((Err<List<String>, IOException>) declarationsRes).error());
            }

            var methodsRes = readMethods(file);
            if (methodsRes.isErr()) {
                return new Some<>(((Err<Map<String, List<String>>, IOException>) methodsRes).error());
            }

            List<String> imports = Results.unwrap(importsRes);
            List<String> declarations = Results.unwrap(declarationsRes);
            Map<String, List<String>> methods = Results.unwrap(methodsRes);

            String content = stubContent(relative, tsFile.getParent(), tsRoot,
                    imports, declarations, methods);
            var writeRes = tsFile.writeString(content);
            if (writeRes.isPresent()) {
                return writeRes;
            }
        }
        return new None<>();
    }

    private static Result<List<String>, IOException> readImports(PathLike file) {
        var sourceRes = file.readString();
        if (sourceRes.isErr()) {
            return new Err<>(((Err<String, IOException>) sourceRes).error());
        }
        String source = ((Ok<String, IOException>) sourceRes).value();

        var pattern = Pattern.compile("^import\\s+([\\w.]+);", Pattern.MULTILINE);
        var matcher = pattern.matcher(source);
        List<String> imports = new ArrayList<>();
        while (matcher.find()) {
            String name = matcher.group(1);
            if (!name.startsWith("java.")) {
                imports.add(name);
            }
        }
        return new Ok<>(imports);
    }

    private static Result<List<String>, IOException> readDeclarations(PathLike file) {
        var sourceRes = file.readString();
        if (sourceRes.isErr()) {
            return new Err<>(((Err<String, IOException>) sourceRes).error());
        }
        String source = ((Ok<String, IOException>) sourceRes).value();

        source = source.replaceAll("(?s)/\\*.*?\\*/", "");
        source = source.replaceAll("//.*", "");

        var classPat = Pattern.compile(
                "^(?:public\\s+|protected\\s+|private\\s+)?(?:static\\s+)?(?:final\\s+)?(?:sealed\\s+)?(class|interface|record)\\s+(\\w+(?:<[^>{}]+>)?)",
                Pattern.MULTILINE);

        var matcher = classPat.matcher(source);
        List<String> declarations = new ArrayList<>();
        while (matcher.find()) {
            String kind = matcher.group(1);
            String name = matcher.group(2);
            int start = matcher.end();
            int brace = source.indexOf('{', start);
            if (brace == -1) {
                continue;
            }
            String rest = source.substring(start, brace);
            rest = rest.replaceAll("\\s+", " ").trim();

            String extendsPart = null;
            String implementsPart = null;
            int extIdx = rest.indexOf("extends ");
            int implIdx = rest.indexOf("implements ");
            if (extIdx != -1) {
                if (implIdx != -1) {
                    extendsPart = rest.substring(extIdx + 8, implIdx).trim();
                }
                else {
                    extendsPart = rest.substring(extIdx + 8).trim();
                }
            }
            if (implIdx != -1) {
                implementsPart = rest.substring(implIdx + 11).trim();
            }

            if ("record".equals(kind)) {
                kind = "class";
            }
            StringBuilder decl = new StringBuilder("export " + kind + " " + name);
            if (extendsPart != null && !extendsPart.isEmpty()) {
                decl.append(" extends ").append(extendsPart);
            }
            if (implementsPart != null && !implementsPart.isEmpty()) {
                decl.append(" implements ").append(implementsPart);
            }
            decl.append(" {}");
            declarations.add(decl.toString());
        }
        return new Ok<>(declarations);
    }

    private static Result<Map<String, List<String>>, IOException> readMethods(PathLike file) {
        var sourceRes = file.readString();
        if (sourceRes.isErr()) {
            return new Err<>(((Err<String, IOException>) sourceRes).error());
        }
        String source = ((Ok<String, IOException>) sourceRes).value();

        source = source.replaceAll("(?s)/\\*.*?\\*/", "");
        source = source.replaceAll("//.*", "");
        Map<String, List<String>> map = new LinkedHashMap<>();
        var classPat = Pattern.compile("(?:class|interface|record)\\s+(\\w+)[^{]*\\{");
        var cMatcher = classPat.matcher(source);
        while (cMatcher.find()) {
            String name = cMatcher.group(1);
            int start = cMatcher.end();
            String body = extractClassBody(source, start);
            List<String> list = parseMethods(body, name);
            map.put(name, list);
        }
        return new Ok<>(map);
    }

    private static String extractClassBody(String source, int start) {
        int level = 1;
        int i = start;
        while (i < source.length() && level > 0) {
            char ch = source.charAt(i);
            if (ch == '{') {
                level++;
            }
            else if (ch == '}') {
                level--;
            }
            i++;
        }
        return source.substring(start, i - 1);
    }

    private static List<String> parseMethods(String body, String className) {
        var methodPat = Pattern.compile(
                "(?:public\\s+|protected\\s+|private\\s+)?(static\\s+)?(?:final\\s+)?(<[^>]+>\\s+)?([\\w.]+(?:<[^>]+>)?(?:\\[\\])*)\\s+(\\w+)\\s*\\(([^)]*)\\)\\s*\\{");
        var mMatcher = methodPat.matcher(body);
        List<String> list = new ArrayList<>();
        while (mMatcher.find()) {
            String staticKw = mMatcher.group(1);
            String generics = mMatcher.group(2);
            String returnType = mMatcher.group(3);
            String mName = mMatcher.group(4);
            String params = mMatcher.group(5);
            if (!mName.equals(className)) {
                String prefix = staticKw == null ? "" : "static ";
                String typeParams = generics == null ? "" : generics.trim();
                String paramList = tsParams(params);
                list.add("\t" + prefix + mName + typeParams + "(" + paramList + "): " + tsType(returnType) + " {");
                list.add("\t}");
            }
        }
        return list;
    }

    private static String stubContent(PathLike relative, PathLike from, PathLike root,
                                      List<String> imports,
                                      List<String> declarations,
                                      Map<String, List<String>> methods) {
        StringBuilder builder = new StringBuilder();
        builder.append("// Auto-generated from ").append(relative).append(System.lineSeparator());

        Map<String, List<String>> byPath = groupImports(imports, from, root);
        appendImportLines(builder, byPath);

        if (declarations.isEmpty()) {
            builder.append("export {};").append(System.lineSeparator());
            return builder.toString();
        }

        appendDeclarations(builder, relative, declarations, methods);
        return builder.toString();
    }

    private static Map<String, List<String>> groupImports(List<String> imports, PathLike from, PathLike root) {
        Map<String, List<String>> byPath = new LinkedHashMap<>();
        for (String imp : imports) {
            String className = imp.substring(imp.lastIndexOf('.') + 1);
            PathLike target = root.resolve(imp.replace('.', '/') + ".ts");
            PathLike rel = from.relativize(target);
            String path = rel.toString().replace('\\', '/');
            path = path.replaceFirst("\\.ts$", "");
            if (!path.startsWith(".")) {
                path = "./" + path;
            }
            byPath.computeIfAbsent(path, k -> new ArrayList<>()).add(className);
        }
        return byPath;
    }

    private static void appendImportLines(StringBuilder builder, Map<String, List<String>> byPath) {
        for (var entry : byPath.entrySet()) {
            builder.append("import { ");
            builder.append(String.join(", ", entry.getValue()));
            builder.append(" } from \"").append(entry.getKey()).append("\"");
            builder.append(";").append(System.lineSeparator());
        }
    }

    private static void appendDeclarations(StringBuilder builder, PathLike relative,
                                           List<String> declarations,
                                           Map<String, List<String>> methods) {
        var namePattern = Pattern.compile("export \\w+ (\\w+)(?:<[^>]+>)?");
        boolean isMain = relative.toString().replace('\\', '/').equals("magma/Main.java");
        for (String decl : declarations) {
            appendDeclaration(builder, decl, namePattern, methods);
        }
        if (isMain) {
            builder.append("Main.main([]);").append(System.lineSeparator());
        }
    }

    private static void appendDeclaration(StringBuilder builder, String decl,
                                          Pattern namePattern,
                                          Map<String, List<String>> methods) {
        var m = namePattern.matcher(decl);
        if (!m.find()) {
            builder.append(decl).append(System.lineSeparator());
            return;
        }
        String name = m.group(1);
        List<String> mList = methods.getOrDefault(name, Collections.emptyList());
        if (mList.isEmpty()) {
            builder.append(decl).append(System.lineSeparator());
            return;
        }
        builder.append(decl, 0, decl.length() - 1).append(System.lineSeparator());
        for (String method : mList) {
            builder.append(method).append(System.lineSeparator());
        }
        builder.append("}").append(System.lineSeparator());
    }

    private static String tsParams(String javaParams) {
        javaParams = javaParams.trim();
        StringBuilder out = new StringBuilder();
        int depth = 0;
        int start = 0;
        boolean first = true;
        for (int i = 0; i <= javaParams.length(); i++) {
            if (i == javaParams.length() || (javaParams.charAt(i) == ',' && depth == 0)) {
                String part = javaParams.substring(start, i).trim();
                if (!part.isEmpty()) {
                    int last = part.lastIndexOf(' ');
                    if (last != -1) {
                        String type = part.substring(0, last).trim();
                        String name = part.substring(last + 1).trim();
                        if (!first) {
                            out.append(", ");
                        }
                        out.append(name).append(": ").append(tsType(type));
                        first = false;
                    }
                }
                start = i + 1;
            }
            else if (javaParams.charAt(i) == '<') {
                depth++;
            }
            else if (javaParams.charAt(i) == '>') {
                depth--;
            }
        }
        return out.toString();
    }

    private static String tsType(String javaType) {
        javaType = javaType.trim();
        if (javaType.endsWith("[]")) {
            String inner = javaType.substring(0, javaType.length() - 2);
            return tsType(inner) + "[]";
        }
        int lt = javaType.indexOf('<');
        if (lt != -1 && javaType.endsWith(">")) {
            String base = javaType.substring(0, lt);
            String args = javaType.substring(lt + 1, javaType.length() - 1);
            List<String> converted = convertTypes(splitGenericArgs(args));
            for (int i = 0; i < converted.size(); i++) {
                converted.set(i, sanitizeWildcard(converted.get(i)));
            }
            String simple = base.replace("java.util.function.", "");
            if ("Function".equals(simple) && converted.size() >= 2) {
                return "(arg0: " + converted.get(0) + ") => " + converted.get(1);
            }
            if ("BiFunction".equals(simple) && converted.size() >= 3) {
                return "(arg0: " + converted.get(0) + ", arg1: " + converted.get(1)
                        + ") => " + converted.get(2);
            }
            if ("Supplier".equals(simple) && converted.size() >= 1) {
                return "() => " + converted.get(0);
            }
            if ("Consumer".equals(simple) && converted.size() >= 1) {
                return "(arg0: " + converted.get(0) + ") => void";
            }
            if ("BiConsumer".equals(simple) && converted.size() >= 2) {
                return "(arg0: " + converted.get(0) + ", arg1: " + converted.get(1)
                        + ") => void";
            }
            if ("Predicate".equals(simple) && converted.size() >= 1) {
                return "(arg0: " + converted.get(0) + ") => boolean";
            }
            return base + "<" + String.join(", ", converted) + ">";
        }
        return switch (javaType) {
            case "byte", "short", "int", "long", "float", "double" -> "number";
            case "boolean" -> "boolean";
            case "char", "String" -> "string";
            default -> javaType;
        };
    }

    private static List<String> splitGenericArgs(String args) {
        List<String> parts = new ArrayList<>();
        int depth = 0;
        int start = 0;
        for (int i = 0; i < args.length(); i++) {
            char ch = args.charAt(i);
            if (ch == '<') {
                depth++;
            }
            else if (ch == '>') {
                depth--;
            }
            else if (ch == ',' && depth == 0) {
                parts.add(args.substring(start, i).trim());
                start = i + 1;
            }
        }
        parts.add(args.substring(start).trim());
        return parts;
    }

    private static List<String> convertTypes(List<String> parts) {
        List<String> converted = new ArrayList<>();
        for (String part : parts) {
            converted.add(tsType(part));
        }
        return converted;
    }

    private static String sanitizeWildcard(String type) {
        type = type.trim();
        if (type.startsWith("? extends ")) {
            return type.substring(10).trim();
        }
        if (type.startsWith("? super ")) {
            return type.substring(8).trim();
        }
        if ("?".equals(type)) {
            return "any";
        }
        return type;
    }
}
