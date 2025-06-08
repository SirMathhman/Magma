package magma.app;

import magma.list.JdkList;
import magma.list.ListLike;

class ExpressionParser {
    static boolean isMemberAccess(String stmt) {
        return stmt.contains(".") && !stmt.contains("(") && !stmt.contains("=");
    }

    static String parseMemberAccess(String stmt, String indent) {
        return indent + "    " + stmt + ";";
    }

    static boolean isInvokable(String stmt) {
        var open = stmt.indexOf('(');
        var close = stmt.lastIndexOf(')');
        if (open == -1 || close == -1 || close <= open) {
            return false;
        }
        var arrow = stmt.indexOf("->");
        if (arrow != -1 && arrow > close) {
            return false;
        }
        var head = stmt.substring(0, open).trim();
        if (head.isEmpty()) return false;
        return !head.startsWith("if") && !head.startsWith("while") && !head.startsWith("for");
    }

    static String parseInvokable(String stmt, String indent) {
        return indent + "    " + stubInvokableExpr(stmt) + ";";
    }

    static String parseValue(String value, String expected) {
        var trimmed = value.trim();
        if (trimmed.startsWith("new ") && trimmed.contains("<>")) {
            trimmed = fillDiamond(trimmed, expected);
        }
        if (trimmed.contains("->")) return trimmed;
        if (trimmed.startsWith("!")) {
            var rest = trimmed.substring(1).trim();
            return "!" + parseValue(rest, expected);
        }
        if (trimmed.startsWith("new ") && trimmed.contains(".") && isInvokable(trimmed)) {
            return trimmed;
        }
        if (trimmed.contains(".") && !trimmed.contains("=")) {
            return parseMemberChain(trimmed);
        }
        if (isInvokable(trimmed)) {
            return stubInvokableExpr(trimmed);
        }
        if (trimmed.length() >= 2 && trimmed.startsWith("\"") && trimmed.endsWith("\"")) {
            return trimmed;
        }
        if (isMemberAccess(trimmed) || isNumeric(trimmed)) {
            return trimmed;
        }
        if (isIdentifier(trimmed)) {
            return trimmed;
        }
        if (isNumeric(trimmed)) {
            return trimmed;
        }
        return "/* TODO */";
    }

    static String parseValue(String value) {
        return parseValue(value, null);
    }

    static String stubInvokableExpr(String stmt) {
        var close = stmt.lastIndexOf(')');
        if (close == -1) return "/* TODO */";
        var open = findOpenParen(stmt, close);
        if (open == -1) return "/* TODO */";
        var callee = stmt.substring(0, open).trim();
        var args = stmt.substring(open + 1, close).trim();
        var parts = splitArgs(args);
        mapArgs(parts);
        var joined = joinArgs(parts);
        return callee + "(" + joined + ")";
    }

    private static String parseValueArg(String value) {
        var trimmed = value.trim();
        return parseValue(trimmed, null);
    }

    private static String fillDiamond(String expr, String expected) {
        if (expected == null) return expr;
        var start = expected.indexOf('<');
        var end = expected.lastIndexOf('>');
        if (start == -1 || end == -1 || end <= start) return expr;
        var generic = expected.substring(start + 1, end);
        return expr.replace("<>", "<" + generic + ">");
    }

    private static String parseMemberChain(String expr) {
        var parts = splitMemberParts(expr);
        return joinMemberParts(parts);
    }

    private static ListLike<String> splitMemberParts(String expr) {
        ListLike<String> parts = JdkList.create();
        var depth = 0;
        var part = new StringBuilder();
        for (var i = 0; i < expr.length(); i++) {
            var c = expr.charAt(i);
            if (c == '.' && depth == 0) {
                parts.add(part.toString());
                part.setLength(0);
                continue;
            }
            if (c == '(') depth++; if (c == ')') depth--;
            part.append(c);
        }
        parts.add(part.toString());
        return parts;
    }

    private static String joinMemberParts(ListLike<String> parts) {
        var out = new StringBuilder();
        for (var i = 0; i < parts.size(); i++) {
            if (i > 0) out.append('.');
            out.append(parseChainSegment(parts.get(i).trim()));
        }
        return out.toString();
    }

    private static String parseChainSegment(String seg) {
        if (isInvokable(seg)) {
            return stubInvokableExpr(seg);
        }
        return seg;
    }

    private static boolean isNumeric(String s) {
        if (s.isEmpty()) return false;
        var i = 0;
        if (s.charAt(0) == '-') {
            if (s.length() == 1) return false;
            i = 1;
        }
        var dot = false;
        for (; i < s.length(); i++) {
            var c = s.charAt(i);
            if (c == '.') {
                if (dot) return false;
                dot = true;
                continue;
            }
            if (c < '0' || c > '9') return false;
        }
        return true;
    }

    private static boolean isIdentifier(String s) {
        if (s.isEmpty()) return false;
        if (s.equals("true") || s.equals("false") || s.equals("null")) return false;
        var first = s.charAt(0);
        if (!((first >= 'a' && first <= 'z') || (first >= 'A' && first <= 'Z') || first == '_')) {
            return false;
        }
        for (var i = 1; i < s.length(); i++) {
            var c = s.charAt(i);
            var letter = (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
            var digit = c >= '0' && c <= '9';
            if (!(letter || digit || c == '_')) {
                return false;
            }
        }
        return true;
    }

    private static int findOpenParen(String stmt, int close) {
        var depth = 0;
        for (var i = close; i >= 0; i--) {
            var c = stmt.charAt(i);
            if (c == ')') depth++; else if (c == '(') {
                depth--;
                if (depth == 0) return i;
            }
        }
        return -1;
    }

    private static void mapArgs(ListLike<String> parts) {
        for (var i = 0; i < parts.size(); i++) {
            parts.set(i, parseValueArg(parts.get(i)));
        }
    }

    private static String joinArgs(ListLike<String> parts) {
        var out = new StringBuilder();
        for (var i = 0; i < parts.size(); i++) {
            if (i > 0) out.append(", ");
            out.append(parts.get(i));
        }
        return out.toString();
    }

    private static ListLike<String> splitArgs(String args) {
        ListLike<String> out = JdkList.create();
        if (args.isBlank()) return out;
        var depth = 0;
        var part = new StringBuilder();
        for (var i = 0; i < args.length(); i++) {
            var c = args.charAt(i);
            if (c == ',' && depth == 0) {
                out.add(part.toString().trim());
                part.setLength(0);
                continue;
            }
            if (c == '(') depth++; if (c == ')') depth--;
            part.append(c);
        }
        out.add(part.toString().trim());
        return out;
    }
}
