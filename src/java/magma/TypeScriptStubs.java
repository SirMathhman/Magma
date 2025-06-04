package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Utility for generating TypeScript stub files mirroring the Java sources.
 * Instances contain no state and all methods are static.
 */
public final class TypeScriptStubs {
    private TypeScriptStubs() {}

    public static Optional<IOException> write(Path javaRoot, Path tsRoot) {
        List<Path> files;
        try (Stream<Path> stream = Files.walk(javaRoot)) {
            files = stream.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".java"))
                    .toList();
        } catch (IOException e) {
            return Optional.of(e);
        }
        for (Path file : files) {
            Path relative = javaRoot.relativize(file);
            Path tsFile = tsRoot.resolve(relative.toString().replaceFirst("\\.java$", ".ts"));
            try {
                Files.createDirectories(tsFile.getParent());
                var imports = readImports(file);
                var declarations = readDeclarations(file);
                var methods = readMethods(file);
                String content = stubContent(relative, tsFile.getParent(), tsRoot,
                        imports, declarations, methods);
                Files.writeString(tsFile, content);
            } catch (IOException e) {
                return Optional.of(e);
            }
        }
        return Optional.empty();
    }

    private static List<String> readImports(Path file) throws IOException {
        String source = Files.readString(file);
        var pattern = java.util.regex.Pattern.compile("^import\\s+([\\w.]+);", java.util.regex.Pattern.MULTILINE);
        var matcher = pattern.matcher(source);
        List<String> imports = new java.util.ArrayList<>();
        while (matcher.find()) {
            String name = matcher.group(1);
            if (!name.startsWith("java.")) {
                imports.add(name);
            }
        }
        return imports;
    }

    private static List<String> readDeclarations(Path file) throws IOException {
        String source = Files.readString(file);
        var pattern = java.util.regex.Pattern.compile(
                "^(?:public\\s+|protected\\s+|private\\s+)?(?:static\\s+)?(?:final\\s+)?(?:sealed\\s+)?(class|interface|record)\\s+(\\w+(?:<[^>]+>)?)",
                java.util.regex.Pattern.MULTILINE);
        var matcher = pattern.matcher(source);
        List<String> declarations = new java.util.ArrayList<>();
        while (matcher.find()) {
            String kind = matcher.group(1);
            String name = matcher.group(2);
            if ("record".equals(kind)) {
                kind = "class";
            }
            declarations.add("export " + kind + " " + name + " {}");
        }
        return declarations;
    }

    private static Map<String, List<String>> readMethods(Path file) throws IOException {
        String source = Files.readString(file);
        source = source.replaceAll("(?s)/\\*.*?\\*/", "");
        source = source.replaceAll("//.*", "");
        Map<String, List<String>> map = new java.util.LinkedHashMap<>();
        var classPat = java.util.regex.Pattern.compile("(?:class|interface|record)\\s+(\\w+)[^{]*\\{");
        var cMatcher = classPat.matcher(source);
        while (cMatcher.find()) {
            String name = cMatcher.group(1);
            int start = cMatcher.end();
            int level = 1;
            int i = start;
            while (i < source.length() && level > 0) {
                char ch = source.charAt(i);
                if (ch == '{') level++; else if (ch == '}') level--;
                i++;
            }
            String body = source.substring(start, i - 1);
            var methodPat = java.util.regex.Pattern.compile(
                    "(?:public\\s+|protected\\s+|private\\s+)?(static\\s+)?(?:final\\s+)?([\\w.]+(?:<[^>]+>)?(?:\\[\\])*)\\s+(\\w+)\\s*\\(([^)]*)\\)\\s*\\{");
            var mMatcher = methodPat.matcher(body);
            List<String> list = new java.util.ArrayList<>();
            while (mMatcher.find()) {
                String staticKw = mMatcher.group(1);
                String returnType = mMatcher.group(2);
                String mName = mMatcher.group(3);
                String params = mMatcher.group(4);
                if (!mName.equals(name)) {
                    String prefix = staticKw == null ? "" : "static ";
                    String paramList = tsParams(params);
                    list.add("\t" + prefix + mName + "(" + paramList + "): " + tsType(returnType) + " {");
                    list.add("\t}");
                }
            }
            map.put(name, list);
        }
        return map;
    }

    private static String stubContent(Path relative, Path from, Path root,
                                      List<String> imports,
                                      List<String> declarations,
                                      Map<String, List<String>> methods) {
        StringBuilder builder = new StringBuilder();
        builder.append("// Auto-generated from ").append(relative).append(System.lineSeparator());

        Map<String, List<String>> byPath = new java.util.LinkedHashMap<>();
        for (String imp : imports) {
            String className = imp.substring(imp.lastIndexOf('.') + 1);
            Path target = root.resolve(imp.replace('.', '/') + ".ts");
            Path rel = from.relativize(target);
            String path = rel.toString().replace('\\', '/');
            path = path.replaceFirst("\\.ts$", "");
            if (!path.startsWith(".")) {
                path = "./" + path;
            }
            byPath.computeIfAbsent(path, k -> new java.util.ArrayList<>()).add(className);
        }

        for (var entry : byPath.entrySet()) {
            builder.append("import { ");
            builder.append(String.join(", ", entry.getValue()));
            builder.append(" } from \"").append(entry.getKey()).append("\"");
            builder.append(";").append(System.lineSeparator());
        }

        if (declarations.isEmpty()) {
            builder.append("export {};").append(System.lineSeparator());
            return builder.toString();
        }

        var namePattern = java.util.regex.Pattern.compile("export \\w+ (\\w+)(?:<[^>]+>)? \\{\\}");
        for (String decl : declarations) {
            var m = namePattern.matcher(decl);
            if (!m.matches()) {
                builder.append(decl).append(System.lineSeparator());
                continue;
            }
            String name = m.group(1);
            List<String> mList = methods.getOrDefault(name, java.util.Collections.emptyList());
            if (mList.isEmpty()) {
                builder.append(decl).append(System.lineSeparator());
                continue;
            }
            builder.append(decl.substring(0, decl.length() - 1)).append(System.lineSeparator());
            for (String method : mList) {
                builder.append(method).append(System.lineSeparator());
            }
            builder.append("}").append(System.lineSeparator());
        }
        return builder.toString();
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
            } else if (javaParams.charAt(i) == '<') {
                depth++;
            } else if (javaParams.charAt(i) == '>') {
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
            java.util.List<String> parts = new java.util.ArrayList<>();
            int depth = 0;
            int start = 0;
            for (int i = 0; i < args.length(); i++) {
                char ch = args.charAt(i);
                if (ch == '<') depth++; else if (ch == '>') depth--; else if (ch == ',' && depth == 0) {
                    parts.add(args.substring(start, i).trim());
                    start = i + 1;
                }
            }
            parts.add(args.substring(start).trim());
            java.util.List<String> converted = new java.util.ArrayList<>();
            for (String part : parts) {
                converted.add(tsType(part));
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
}
