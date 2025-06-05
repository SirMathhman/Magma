package magma;

import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.regex.Pattern;

/**
 * Wraps a single Java source file and provides parsing helpers used by
 * {@link TypeScriptStubs}. Each instance reads its underlying file and
 * returns parsed data wrapped in {@link Result} values instead of throwing
 * exceptions.
 */
public record JavaFile(PathLike file) {

    /** Simple container for a record component. */
    private static record Param(String name, String tsType) {}

    public Result<List<String>, IOException> imports() {
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

    public Result<String, IOException> packageName() {
        return file.readString().mapValue(source -> {
            var pattern = Pattern.compile("^package\\s+([\\w.]+);", Pattern.MULTILINE);
            var matcher = pattern.matcher(source);
            if (matcher.find()) {
                return matcher.group(1);
            }
            return "";
        });
    }

    public Result<List<String>, IOException> localDependencies() {
        return file.readString().flatMapValue(source -> {
            source = source.replaceAll("(?s)/\\*.*?\\*/", "");
            source = source.replaceAll("//.*", "");

            var classPat = Pattern.compile(
                    "^(?:public\\s+|protected\\s+|private\\s+)?(?:static\\s+)?(?:final\\s+)?(?:sealed\\s+)?(class|interface|record)\\s+(\\w+)",
                    Pattern.MULTILINE);

            var matcher = classPat.matcher(source);
            List<String> deps = new ArrayList<>();
            List<String> defined = new ArrayList<>();
            while (matcher.find()) {
                String name = matcher.group(2);
                defined.add(name);
                int start = matcher.end();
                int brace = source.indexOf('{', start);
                if (brace == -1) {
                    continue;
                }
                String rest = source.substring(start, brace);
                rest = rest.replaceAll("\\s+", " ").trim();

                int extIdx = rest.indexOf("extends ");
                int implIdx = rest.indexOf("implements ");
                String extendsPart = null;
                if (extIdx != -1 && implIdx != -1) {
                    extendsPart = rest.substring(extIdx + 8, implIdx).trim();
                }
                else if (extIdx != -1) {
                    extendsPart = rest.substring(extIdx + 8).trim();
                }
                String implementsPart = implIdx != -1 ? rest.substring(implIdx + 11).trim() : null;

                addParts(extendsPart, deps, defined);
                addParts(implementsPart, deps, defined);
            }
            return new Ok<>(deps);
        });
    }

    private static void addParts(String clause, List<String> deps, List<String> defined) {
        if (clause == null || clause.isEmpty()) {
            return;
        }
        clause = clause.replaceAll("<.*?>", "");
        for (String part : clause.split(",")) {
            String base = part.trim();
            if (!base.isEmpty() && !defined.contains(base)) {
                deps.add(base);
            }
        }
    }

    public Result<List<String>, IOException> declarations() {
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

    public Result<Map<String, List<String>>, IOException> methods() {
        var sourceRes = file.readString();
        if (sourceRes.isErr()) {
            return new Err<>(((Err<String, IOException>) sourceRes).error());
        }
        String source = ((Ok<String, IOException>) sourceRes).value();

        source = source.replaceAll("(?s)/\\*.*?\\*/", "");
        source = source.replaceAll("//.*", "");
        Map<String, List<String>> map = new LinkedHashMap<>();
        var classPat = Pattern.compile("(class|interface|record)\\s+(\\w+)[^{]*\\{");
        var cMatcher = classPat.matcher(source);
        while (cMatcher.find()) {
            String kind = cMatcher.group(1);
            String name = cMatcher.group(2);
            int start = cMatcher.end();
            var bodyRes = extractClassBody(source, start);
            if (bodyRes.isErr()) {
                return new Err<>(((Err<String, IOException>) bodyRes).error());
            }
            String body = ((Ok<String, IOException>) bodyRes).value();
            boolean isInterface = "interface".equals(kind);
            var methodsRes = parseMethods(body, name, isInterface);
            if (methodsRes.isErr()) {
                return new Err<>(((Err<List<String>, IOException>) methodsRes).error());
            }
            List<String> list = ((Ok<List<String>, IOException>) methodsRes).value();

            if ("record".equals(kind)) {
                String header = source.substring(cMatcher.start(), start - 1);
                int lp = header.indexOf('(');
                int rp = header.indexOf(')', lp);
                if (lp != -1 && rp != -1 && rp > lp + 1) {
                    String params = header.substring(lp + 1, rp).trim();
                    if (!params.isEmpty()) {
                        List<Param> fields = parseRecordParams(params);
                        List<String> all = new ArrayList<>();
                        for (Param p : fields) {
                            all.add("\t" + p.name + ": " + p.tsType + ";");
                        }
                        all.add("\tconstructor(" + tsParams(params) + ") {");
                        for (Param p : fields) {
                            all.add("\t\tthis." + p.name + " = " + p.name + ";");
                        }
                        all.add("\t}");
                        all.addAll(list);
                        list = all;
                    }
                }
            }

            map.put(name, list);
        }
        return new Ok<>(map);
    }

    private static Result<String, IOException> extractClassBody(String source, int start) {
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
        if (level != 0) {
            return new Err<>(new ParseException("missing '}'"));
        }
        return new Ok<>(source.substring(start, i - 1));
    }

    private static Result<List<String>, IOException> parseMethods(String body, String className, boolean isInterface) {
        var methodPat = Pattern.compile(
                "(?:public\\s+|protected\\s+|private\\s+)?(static\\s+)?(?:final\\s+)?(<[^>]+>\\s+)?([\\w.]+(?:<[^>]+>)?(?:\\[\\])*)\\s+(\\w+)\\s*\\(([^)]*)\\)\\s*(\\{|;)");
        var mMatcher = methodPat.matcher(body);
        List<String> list = new ArrayList<>();
        while (mMatcher.find()) {
            String mName = mMatcher.group(4);
            if (mName.equals(className)) {
                continue;
            }
            String delim = mMatcher.group(6);
            String methodBody = "";
            if ("{".equals(delim)) {
                int start = mMatcher.end();
                var blockRes = extractBlock(body, start);
                if (blockRes.isErr()) {
                    return new Err<>(((Err<String, IOException>) blockRes).error());
                }
                methodBody = ((Ok<String, IOException>) blockRes).value();
            }
            var addRes = addMethod(list,
                    mMatcher.group(1),
                    mMatcher.group(2),
                    mMatcher.group(3),
                    mName,
                    mMatcher.group(5),
                    delim,
                    isInterface,
                    methodBody);
            if (addRes.isErr()) {
                return new Err<>(((Err<Void, IOException>) addRes).error());
            }
        }
        return new Ok<>(list);
    }

    private static Result<Void, IOException> addMethod(List<String> list, String staticKw, String generics,
                                  String returnType, String name, String params,
                                  String delim, boolean isInterface,
                                  String body) {
        String prefix = staticKw == null ? "" : "static ";
        String typeParams = generics == null ? "" : generics.trim();
        String paramList = tsParams(params);
        if (isInterface || ";".equals(delim)) {
            list.add("\t" + prefix + name + typeParams + "(" + paramList + "): " + tsType(returnType) + ";");
            return new Ok<>(null);
        }
        list.add("\t" + prefix + name + typeParams + "(" + paramList + "): " + tsType(returnType) + " {");
        Set<String> defined = new HashSet<>(paramNames(params));
        defined.add("this");
        var segRes = MethodBodyParser.parseSegments(body, defined);
        if (segRes.isErr()) {
            return new Err<>(((Err<List<String>, IOException>) segRes).error());
        }
        List<String> segs = ((Ok<List<String>, IOException>) segRes).value();
        if (segs.isEmpty()) {
            list.add("\t\treturn 0;");
        } else {
            for (String seg : segs) {
                list.add("\t\t" + seg);
            }
        }
        list.add("\t}");
        return new Ok<>(null);
    }

    private static String tsParams(String javaParams) {
        javaParams = javaParams.trim();
        StringBuilder out = new StringBuilder();
        int depth = 0;
        int start = 0;
        boolean first = true;
        for (int i = 0; i <= javaParams.length(); i++) {
            boolean atEnd = i == javaParams.length();
            boolean atComma = !atEnd && javaParams.charAt(i) == ',' && depth == 0;
            if (atEnd || atComma) {
                String part = javaParams.substring(start, i).trim();
                first = appendParam(part, out, first);
                start = i + 1;
                continue;
            }
            if (javaParams.charAt(i) == '<') {
                depth++;
            }
            else if (javaParams.charAt(i) == '>') {
                depth--;
            }
        }
        return out.toString();
    }

    private static boolean appendParam(String part, StringBuilder out, boolean first) {
        if (part.isEmpty()) {
            return first;
        }
        int last = part.lastIndexOf(' ');
        if (last == -1) {
            return first;
        }
        String type = part.substring(0, last).trim();
        String name = part.substring(last + 1).trim();
        if (!first) {
            out.append(", ");
        }
        out.append(name).append(": ").append(tsType(type));
        return false;
    }

    private static List<String> paramNames(String javaParams) {
        javaParams = javaParams.trim();
        List<String> names = new ArrayList<>();
        int depth = 0;
        int start = 0;
        for (int i = 0; i <= javaParams.length(); i++) {
            boolean atEnd = i == javaParams.length();
            boolean atComma = !atEnd && javaParams.charAt(i) == ',' && depth == 0;
            if (atEnd || atComma) {
                String part = javaParams.substring(start, i).trim();
                if (!part.isEmpty()) {
                    int last = part.lastIndexOf(' ');
                    if (last != -1) {
                        String name = part.substring(last + 1).trim();
                        names.add(name);
                    }
                }
                start = i + 1;
                continue;
            }
            if (javaParams.charAt(i) == '<') {
                depth++;
            }
            else if (javaParams.charAt(i) == '>') {
                depth--;
            }
        }
        return names;
    }

    private static List<Param> parseRecordParams(String javaParams) {
        javaParams = javaParams.trim();
        List<Param> list = new ArrayList<>();
        int depth = 0;
        int start = 0;
        for (int i = 0; i <= javaParams.length(); i++) {
            boolean atEnd = i == javaParams.length();
            boolean atComma = !atEnd && javaParams.charAt(i) == ',' && depth == 0;
            if (atEnd || atComma) {
                String part = javaParams.substring(start, i).trim();
                if (!part.isEmpty()) {
                    int last = part.lastIndexOf(' ');
                    if (last != -1) {
                        String type = part.substring(0, last).trim();
                        String name = part.substring(last + 1).trim();
                        list.add(new Param(name, tsType(type)));
                    }
                }
                start = i + 1;
                continue;
            }
            if (javaParams.charAt(i) == '<') {
                depth++;
            } else if (javaParams.charAt(i) == '>') {
                depth--;
            }
        }
        return list;
    }

    static String tsType(String javaType) {
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
            converted.replaceAll(JavaFile::sanitizeWildcard);
            String simple = base.replace("java.util.function.", "");
            if ("Function".equals(simple) && converted.size() >= 2) {
                return "(arg0: " + converted.get(0) + ") => " + converted.get(1);
            }
            if ("BiFunction".equals(simple) && converted.size() >= 3) {
                return "(arg0: " + converted.get(0) + ", arg1: " + converted.get(1)
                        + ") => " + converted.get(2);
            }
            if ("Supplier".equals(simple) && !converted.isEmpty()) {
                return "() => " + converted.getFirst();
            }
            if ("Consumer".equals(simple) && !converted.isEmpty()) {
                return "(arg0: " + converted.getFirst() + ") => void";
            }
            if ("BiConsumer".equals(simple) && converted.size() >= 2) {
                return "(arg0: " + converted.get(0) + ", arg1: " + converted.get(1)
                        + ") => void";
            }
            if ("Predicate".equals(simple) && !converted.isEmpty()) {
                return "(arg0: " + converted.getFirst() + ") => boolean";
            }
            return base + "<" + String.join(", ", converted) + ">";
        }
        return switch (javaType) {
            case "byte", "short", "int", "long", "float", "double",
                    "Byte", "Short", "Integer", "Long", "Float", "Double",
                    "java.lang.Byte", "java.lang.Short", "java.lang.Integer",
                    "java.lang.Long", "java.lang.Float", "java.lang.Double" -> "number";
            case "boolean", "Boolean", "java.lang.Boolean" -> "boolean";
            case "char", "Character", "java.lang.Character",
                    "String", "java.lang.String" -> "string";
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


    private static Result<String, IOException> extractBlock(String source, int start) {
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
        if (level != 0) {
            return new Err<>(new ParseException("missing '}'"));
        }
        return new Ok<>(source.substring(start, i - 1));
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
