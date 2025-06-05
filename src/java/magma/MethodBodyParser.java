package magma;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.io.IOException;

import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

import java.util.Set;

final class MethodBodyParser {
    private MethodBodyParser() {
    }

    static Result<List<String>, IOException> parseSegments(String body, Set<String> defined) {
        List<String> segments = new ArrayList<>();
        int indent = 0;
        for (String token : tokens(body)) {
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
            var segRes = matchSegment(stripped, defined);
            if (segRes.isErr()) {
                return new Err<>(((Err<String, IOException>) segRes).error());
            }
            String seg = ((Ok<String, IOException>) segRes).value();
            if (seg != null) {
                segments.add("\t".repeat(indent) + seg);
            }
            if (stripped.endsWith("{")) {
                indent++;
            }
        }
        return new Ok<>(segments);
    }

    private static List<String> tokens(String body) {
        List<String> tokens = new ArrayList<>();
        for (String line : body.split("\\R")) {
            tokens.addAll(tokenizeLine(line));
        }
        return tokens;
    }

    private static List<String> tokenizeLine(String line) {
        List<String> parts = new ArrayList<>();
        for (String part : line.split("(?<=[;{}])")) {
            parts.add(part);
        }
        return parts;
    }

    private static Result<String, IOException> matchSegment(String stripped, Set<String> defined) {
        for (Pattern pat : SEGMENT_PATTERNS) {
            if (pat.matcher(stripped).find()) {
                return processSegment(stripped, defined);
            }
        }
        return new Ok<>(null);
    }

    private static Result<String, IOException> processSegment(String segment, Set<String> defined) {
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
                var declPat = Pattern.compile("^(?:final\\s+)?([\\w.<>\\[\\]]+)\\s+(\\w+)$");
                var declMatch = declPat.matcher(before);
                if (declMatch.find()) {
                    String type = JavaFile.tsType(declMatch.group(1));
                    String name = declMatch.group(2);
                    boolean isConst = before.startsWith("final ");
                    String kw = isConst ? "const" : "let";
                    defined.add(name);
                    var valRes = parseValue(after, defined);
                    if (valRes.isErr()) {
                        return new Err<>(((Err<String, IOException>) valRes).error());
                    }
                    String value = ((Ok<String, IOException>) valRes).value();
                    result = kw + " " + name + ": " + type + " = " + value + (semi ? ";" : "");
                } else {
                    var valRes = parseValue(after, defined);
                    if (valRes.isErr()) {
                        return new Err<>(((Err<String, IOException>) valRes).error());
                    }
                    String value = ((Ok<String, IOException>) valRes).value();
                    result = before + " = " + value + (semi ? ";" : "");
                }
                return new Ok<>(result.replace("->", "=>"));
            }
        }
        if (RETURN_PATTERN.matcher(segment).find()) {
            String rest = segment.substring(segment.indexOf("return") + 6).trim();
            boolean semi = rest.endsWith(";");
            if (semi) {
                rest = rest.substring(0, rest.length() - 1).trim();
            }
            if (rest.isEmpty()) {
                return new Ok<>(segment.replace("->", "=>"));
            }
            var valRes = parseValue(rest, defined);
            if (valRes.isErr()) {
                return new Err<>(((Err<String, IOException>) valRes).error());
            }
            String value = ((Ok<String, IOException>) valRes).value();
            result = "return " + value + (semi ? ";" : "");
            return new Ok<>(result.replace("->", "=>"));
        }
        if (IF_PATTERN.matcher(segment).find() || WHILE_PATTERN.matcher(segment).find() || FOR_PATTERN.matcher(segment).find()) {
            int open = segment.indexOf('(');
            int close = segment.lastIndexOf(')');
            if (open != -1 && close != -1 && close > open) {
                String prefix = segment.substring(0, open + 1);
                String inner = segment.substring(open + 1, close);
                String suffix = segment.substring(close);
                var valRes = parseValue(inner, defined);
                if (valRes.isErr()) {
                    return new Err<>(((Err<String, IOException>) valRes).error());
                }
                String value = ((Ok<String, IOException>) valRes).value();
                result = prefix + value + suffix;
                return new Ok<>(result.replace("->", "=>"));
            }
        }
        return new Ok<>(result.replace("->", "=>"));
    }

    private static Result<String, IOException> parseValue(String value, Set<String> defined) {
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
                var idxRes = scanIdentifier(value, i, out, defined);
                if (idxRes.isErr()) {
                    return new Err<>(((Err<Integer, IOException>) idxRes).error());
                }
                i = ((Ok<Integer, IOException>) idxRes).value();
                continue;
            }
            out.append(ch);
            i++;
        }
        return new Ok<>(out.toString().trim());
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

    private static Result<Integer, IOException> scanIdentifier(String value, int start, StringBuilder out, Set<String> defined) {
        int i = start + 1;
        while (i < value.length() && Character.isJavaIdentifierPart(value.charAt(i))) {
            i++;
        }
        String id = value.substring(start, i);
        if ("instanceof".equals(id)) {
            out.append("/* FIXME: instanceof */ instanceof");
            return new Ok<>(i);
        }
        out.append(id);
        if (!KEYWORDS.contains(id) && !defined.contains(id)) {
            int prev = start - 1;
            while (prev >= 0 && Character.isWhitespace(value.charAt(prev))) {
                prev--;
            }
            char prevCh = prev >= 0 ? value.charAt(prev) : '\0';
            char nextCh = i < value.length() ? value.charAt(i) : '\0';
            if (prevCh != '.' && nextCh != '(') {
                return new Err<>(new ParseException("undefined symbol: " + id));
            }
        }
        return new Ok<>(i);
    }

    private static final Set<String> KEYWORDS = Set.of(
            "return", "if", "else", "for", "while", "do", "switch", "case",
            "break", "continue", "new", "null", "true", "false", "this", "super"
    );

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
}

