package magma;

import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Wraps a single Java source file and provides parsing helpers used by
 * {@link TypeScriptStubs}. Each instance reads its underlying file and
 * returns parsed data wrapped in {@link Result} values instead of throwing
 * exceptions.
 */
public record JavaFile(PathLike file) {

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
            String body = extractClassBody(source, start);
            boolean isInterface = "interface".equals(kind);
            List<String> list = parseMethods(body, name, isInterface);
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

    private static List<String> parseMethods(String body, String className, boolean isInterface) {
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
                methodBody = extractBlock(body, start);
            }
            addMethod(list,
                    mMatcher.group(1),
                    mMatcher.group(2),
                    mMatcher.group(3),
                    mName,
                    mMatcher.group(5),
                    delim,
                    isInterface,
                    methodBody);
        }
        return list;
    }

    private static void addMethod(List<String> list, String staticKw, String generics,
                                  String returnType, String name, String params,
                                  String delim, boolean isInterface,
                                  String body) {
        String prefix = staticKw == null ? "" : "static ";
        String typeParams = generics == null ? "" : generics.trim();
        String paramList = tsParams(params);
        if (isInterface || ";".equals(delim)) {
            list.add("\t" + prefix + name + typeParams + "(" + paramList + "): " + tsType(returnType) + ";");
            return;
        }
        list.add("\t" + prefix + name + typeParams + "(" + paramList + "): " + tsType(returnType) + " {");
        List<String> segs = parseSegments(body);
        if (segs.isEmpty()) {
            list.add("\t\treturn 0;");
        } else {
            for (String seg : segs) {
                list.add("\t\t" + seg);
            }
        }
        list.add("\t}");
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

    /**
     * Performs a light-weight lexical conversion of a Java expression to
     * TypeScript. The method does not understand operator precedence or
     * complex constructs. It merely tokenizes the input and converts obvious
     * literals. Unsupported tokens such as {@code instanceof} are copied with
     * a FIXME comment so future revisions can handle them properly.
     */
    private static String parseValue(String value) {
        value = value.replace("->", "=>");
        StringBuilder out = new StringBuilder();
        int i = 0;
        while (i < value.length()) {
            char ch = value.charAt(i);
            if (Character.isWhitespace(ch)) {
                out.append(ch);
                i++;
                continue;
            }

            if (ch == '-' && i + 1 < value.length() && value.charAt(i + 1) == '>') {
                out.append("=>");
                i += 2;
                continue;
            }

            if (ch == '"' || ch == '\'') {
                i = scanStringLiteral(value, i, out);
                continue;
            }

            if (Character.isDigit(ch)) {
                i = scanNumberLiteral(value, i, out);
                continue;
            }

            if (Character.isJavaIdentifierStart(ch)) {
                i = scanIdentifier(value, i, out);
                continue;
            }

            out.append(ch);
            i++;
        }
        return out.toString().trim();
    }

    private static int scanStringLiteral(String value, int start, StringBuilder out) {
        char quote = value.charAt(start);
        int i = start + 1;
        boolean esc = false;
        while (i < value.length()) {
            char c = value.charAt(i);
            if (esc) {
                esc = false;
            } else if (c == '\\') {
                esc = true;
            } else if (c == quote) {
                i++;
                break;
            }
            i++;
        }
        out.append(value, start, i);
        return i;
    }

    private static int scanNumberLiteral(String value, int start, StringBuilder out) {
        int i = start;
        boolean exp = false;
        int endDigits = start;
        while (i < value.length()) {
            char c = value.charAt(i);
            if (c == '_' || Character.isDigit(c) || c == '.' || c == 'x' || c == 'X'
                    || c == 'b' || c == 'B') {
                i++;
                endDigits = i;
                continue;
            }
            if ((c == 'e' || c == 'E') && !exp) {
                exp = true;
                i++;
                endDigits = i;
                continue;
            }
            if ((c == '+' || c == '-') && exp
                    && (value.charAt(i - 1) == 'e' || value.charAt(i - 1) == 'E')) {
                i++;
                endDigits = i;
                continue;
            }
            if ("lLfFdD".indexOf(c) != -1) {
                i++;
                continue;
            }
            break;
        }
        String num = value.substring(start, endDigits).replace("_", "");
        out.append(num);
        return i;
    }

    private static int scanIdentifier(String value, int start, StringBuilder out) {
        int i = start + 1;
        while (i < value.length() && Character.isJavaIdentifierPart(value.charAt(i))) {
            i++;
        }
        String id = value.substring(start, i);
        if ("instanceof".equals(id)) {
            out.append("/* FIXME: instanceof */ instanceof");
        } else {
            out.append(id);
        }
        return i;
    }

    private static final Pattern[] SEGMENT_PATTERNS = new Pattern[]{
            Pattern.compile("\\b(class|interface|record)\\b"),
            Pattern.compile("\\b(?:public|protected|private)?\\s*(?:static\\s+)?(?:final\\s+)?(?:<[^>]+>\\s+)?[\\w.<>]+\\s+\\w+\\s*\\([^)]*\\)\\s*\\{"),
            Pattern.compile("[^=!<>]=[^=]"),
            Pattern.compile("\\b\\w+\\s*\\([^;]*\\);"),
            Pattern.compile("\\breturn\\b"),
            Pattern.compile("\\bif\\s*\\("),
            Pattern.compile("\\bwhile\\s*\\("),
            Pattern.compile("\\bfor\\s*\\("),
            Pattern.compile("\\belse\\b")
    };

    private static final Pattern ASSIGNMENT_PATTERN = Pattern.compile("[^=!<>]=[^=]");
    private static final Pattern RETURN_PATTERN = Pattern.compile("\\breturn\\b");
    private static final Pattern IF_PATTERN = Pattern.compile("\\bif\\s*\\(");
    private static final Pattern WHILE_PATTERN = Pattern.compile("\\bwhile\\s*\\(");
    private static final Pattern FOR_PATTERN = Pattern.compile("\\bfor\\s*\\(");

    private static List<String> parseSegments(String body) {
        List<String> segments = new ArrayList<>();
        int indent = 0;
        for (String line : body.split("\\R")) {
            for (String token : line.split("(?<=[;{}])")) {
                String stripped = token.trim();
                if (stripped.isEmpty()) {
                    continue;
                }

                if (stripped.startsWith("}")) {
                    indent = Math.max(0, indent - 1);
                    segments.add("\t".repeat(indent) + "}");
                    stripped = stripped.substring(1).trim();
                    if (stripped.isEmpty()) {
                        continue;
                    }
                }

                String seg = matchSegment(stripped);
                if (seg != null) {
                    segments.add("\t".repeat(indent) + seg);
                }

                if (stripped.endsWith("{")) {
                    indent++;
                }
            }
        }
        return segments;
    }

    private static String matchSegment(String stripped) {
        for (Pattern pat : SEGMENT_PATTERNS) {
            if (pat.matcher(stripped).find()) {
                return processSegment(stripped);
            }
        }
        return null;
    }

    private static String processSegment(String segment) {
        String result = segment;
        if (ASSIGNMENT_PATTERN.matcher(segment).find()) {
            int eq = segment.indexOf('=');
            if (eq != -1) {
                String before = segment.substring(0, eq).trim();
                String after = segment.substring(eq + 1).trim();
                boolean semi = after.endsWith(";");
                if (semi) {
                    after = after.substring(0, after.length() - 1).trim();
                }
                String value = parseValue(after);

                var declPat = Pattern.compile("^(?:final\\s+)?([\\w.<>\\[\\]]+)\\s+(\\w+)$");
                var declMatch = declPat.matcher(before);
                if (declMatch.find()) {
                    String type = tsType(declMatch.group(1));
                    String name = declMatch.group(2);
                    boolean isConst = before.startsWith("final ");
                    String kw = isConst ? "const" : "let";
                    result = kw + " " + name + ": " + type + " = " + value + (semi ? ";" : "");
                } else {
                    result = before + " = " + value + (semi ? ";" : "");
                }
                return result.replace("->", "=>");
            }
        }
        if (RETURN_PATTERN.matcher(segment).find()) {
            String rest = segment.substring(segment.indexOf("return") + 6).trim();
            boolean semi = rest.endsWith(";");
            if (semi) {
                rest = rest.substring(0, rest.length() - 1).trim();
            }
            if (rest.isEmpty()) {
                return segment.replace("->", "=>");
            }
            String value = parseValue(rest);
            result = "return " + value + (semi ? ";" : "");
            return result.replace("->", "=>");
        }
        if (IF_PATTERN.matcher(segment).find() || WHILE_PATTERN.matcher(segment).find() || FOR_PATTERN.matcher(segment).find()) {
            int open = segment.indexOf('(');
            int close = segment.lastIndexOf(')');
            if (open != -1 && close != -1 && close > open) {
                String prefix = segment.substring(0, open + 1);
                String inner = segment.substring(open + 1, close);
                String suffix = segment.substring(close);
                String value = parseValue(inner);
                result = prefix + value + suffix;
                return result.replace("->", "=>");
            }
        }
        return result.replace("->", "=>");
    }

    private static String extractBlock(String source, int start) {
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
