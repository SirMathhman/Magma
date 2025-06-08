package magma.app;

import magma.list.JdkList;
import magma.list.ListLike;

class MethodStubber {
    static String stubMethods(String source) {
        var lines = source.split("\\R");
        var returns = collectReturnTypes(lines);
        var out = new StringBuilder();
        for (var i = 0; i < lines.length; ) {
            var line = lines[i];
            var trimmed = line.trim();
            if (shouldCopyLine(trimmed)) {
                out.append(line).append(System.lineSeparator());
                i++;
                continue;
            }
            i = appendStub(lines, i, line, trimmed, out, returns);
        }
        return out.toString().trim();
    }

    private static boolean shouldCopyLine(String trimmed) {
        return !trimmed.endsWith("{") || !trimmed.contains("(") || trimmed.startsWith("export");
    }

    private static int appendStub(String[] lines, int index, String line, String trimmed, StringBuilder out,
                                  java.util.Map<String, String> returns) {
        var end = skipBody(lines, index);
        var stub = buildMethodStub(line, trimmed, lines, index + 1, end - 1, returns);
        if (stub == null) {
            copyRange(lines, index, end, out);
        } else {
            out.append(stub);
        }
        return end;
    }

    private static void copyRange(String[] lines, int start, int end, StringBuilder out) {
        for (var j = start; j < end; j++) {
            out.append(lines[j]).append(System.lineSeparator());
        }
    }

    private static java.util.Map<String, String> collectReturnTypes(String[] lines) {
        java.util.Map<String, String> map = new java.util.HashMap<>();
        for (var line : lines) {
            var trimmed = line.trim();
            if (!trimmed.endsWith("{") || !trimmed.contains("(")) continue;
            var beforeBrace = trimmed.substring(0, trimmed.length() - 1).trim();
            var open = beforeBrace.indexOf('(');
            var close = beforeBrace.lastIndexOf(')');
            if (open == -1 || close == -1) continue;
            var signature = beforeBrace.substring(0, open).trim();
            var tokens = signature.split("\\s+");
            if (tokens.length < 2) continue;
            var name = tokens[tokens.length - 1];
            var type = tokens[tokens.length - 2];
            map.put(name, TypeMapper.toTsType(type));
        }
        return map;
    }

    static String buildMethodStub(String line, String trimmed, String[] lines, int start, int end,
                                  java.util.Map<String, String> returns) {
        var indent = line.substring(0, line.indexOf(trimmed));
        var beforeBrace = trimmed.substring(0, trimmed.length() - 1).trim();
        var parenStart = beforeBrace.indexOf('(');
        var parenEnd = beforeBrace.lastIndexOf(')');
        if (parenStart == -1 || parenEnd == -1) {
            return null;
        }
        var signatureStart = beforeBrace.substring(0, parenStart).trim();
        var params = beforeBrace.substring(parenStart + 1, parenEnd).trim();
        var sigTokens = signatureStart.split("\\s+");
        if (sigTokens.length == 0) {
            return null;
        }
        var name = sigTokens[sigTokens.length - 1];
        var returnType = sigTokens.length > 1 ? sigTokens[sigTokens.length - 2] : "void";
        returns.put(name, TypeMapper.toTsType(returnType));
        var tsParams = TypeMapper.toTsParams(params);
        var tsReturn = TypeMapper.toTsType(returnType);
        var stub = new StringBuilder();
        stub.append(indent).append(name).append("(").append(tsParams).append(")");
        if (!tsReturn.isBlank()) {
            stub.append(": ").append(tsReturn);
        }
        stub.append(" {").append(System.lineSeparator());
        parseStatements(lines, start, end, indent, stub, returns);
        stub.append(indent).append("}").append(System.lineSeparator());
        return stub.toString();
    }

    private static void appendParts(String[] parts, String indent, StringBuilder stub,
                                    java.util.Map<String, String> vars,
                                    java.util.Map<String, String> returns) {
        for (var part : parts) {
            var trimmedPart = part.trim();
            if (trimmedPart.isEmpty()) continue;
            if (trimmedPart.startsWith("return")) {
                appendReturn(trimmedPart, indent, stub);
                continue;
            }
            if (trimmedPart.contains("=")) {
                stub.append(parseAssignment(trimmedPart, indent, vars, returns)).append(System.lineSeparator());
                continue;
            }
            if (isInvokable(trimmedPart)) {
                stub.append(parseInvokable(trimmedPart, indent)).append(System.lineSeparator());
                continue;
            }
            if (isMemberAccess(trimmedPart)) {
                stub.append(parseMemberAccess(trimmedPart, indent)).append(System.lineSeparator());
                continue;
            }
            stub.append(indent).append("    // TODO").append(System.lineSeparator());
        }
    }

    private static void appendReturn(String stmt, String indent, StringBuilder stub) {
        var expr = stmt.substring(6).trim();
        if (expr.endsWith(";")) expr = expr.substring(0, expr.length() - 1).trim();
        var value = expr.isBlank() ? "" : " " + parseValue(expr);
        stub.append(indent)
            .append("    return")
            .append(value)
            .append(";")
            .append(System.lineSeparator());
    }

    private static void parseStatements(String[] lines, int start, int end, String indent,
                                        StringBuilder stub,
                                        java.util.Map<String, String> returns) {
        var wrote = false;
        java.util.Map<String, String> vars = new java.util.HashMap<>();
        for (var i = start; i < end; i++) {
            var body = lines[i].trim();
            if (body.isEmpty()) continue;
            wrote = true;

            if (body.contains("->") && body.endsWith("{")) {
                i = copyArrowBlock(lines, i, stub) - 1;
                continue;
            }

            var next = handleControlBlock(body, lines, i, indent, stub, returns);
            if (next != i) {
                i = next - 1;
                continue;
            }

            if (body.startsWith("return")) {
                appendReturn(body, indent, stub);
                continue;
            }

            appendParts(body.split(";"), indent, stub, vars, returns);
        }
        if (!wrote) stub.append(indent).append("    // TODO").append(System.lineSeparator());
    }

    private static int handleControlBlock(String body, String[] lines, int index, String indent,
                                          StringBuilder stub, java.util.Map<String, String> returns) {
        if ((body.startsWith("if") || body.startsWith("else if")) && body.endsWith("{")) {
            var keyword = body.startsWith("else if") ? "else if" : "if";
            var cond = parseCondition(body);
            var blockEnd = skipBody(lines, index);
            appendParsedBlock(stub, indent, keyword, cond, lines, index + 1, blockEnd - 1, returns);
            return blockEnd;
        }
        if (body.startsWith("else") && body.endsWith("{")) {
            var blockEnd = skipBody(lines, index);
            appendParsedBlock(stub, indent, "else", null, lines, index + 1, blockEnd - 1, returns);
            return blockEnd;
        }
        if (body.startsWith("while") && body.endsWith("{")) {
            var cond = parseCondition(body);
            var blockEnd = skipBody(lines, index);
            appendParsedBlock(stub, indent, "while", cond, lines, index + 1, blockEnd - 1, returns);
            return blockEnd;
        }
        return index;
    }

    static int skipBody(String[] lines, int index) {
        var depth = 1;
        var i = index + 1;
        while (i < lines.length && depth > 0) {
            var body = lines[i];
            depth += body.length() - body.replace("{", "").length();
            depth -= body.length() - body.replace("}", "").length();
            i++;
        }
        return i;
    }

    private static int copyArrowBlock(String[] lines, int start, StringBuilder stub) {
        var i = start;
        while (i < lines.length) {
            stub.append(lines[i]).append(System.lineSeparator());
            var trimmed = lines[i].trim();
            if (trimmed.equals("});") || trimmed.equals("};")) {
                return i + 1;
            }
            i++;
        }
        return i;
    }

    static void appendParsedBlock(StringBuilder stub, String indent, String keyword,
                                  String condition, String[] lines, int start, int end,
                                  java.util.Map<String, String> returns) {
        stub.append(indent).append("    ").append(keyword);
        if (condition != null) {
            stub.append(" (").append(condition).append(")");
        }
        stub.append(" {").append(System.lineSeparator());
        parseStatements(lines, start, end, indent + "    ", stub, returns);
        stub.append(indent).append("    }").append(System.lineSeparator());
    }

    private static String parseCondition(String stmt) {
        var open = stmt.indexOf('(');
        var close = stmt.lastIndexOf(')');
        if (open == -1 || close == -1 || close <= open) {
            return "/* TODO */";
        }
        var inside = stmt.substring(open + 1, close).trim();
        return parseValue(inside);
    }

    static String parseAssignment(String stmt, String indent,
                                  java.util.Map<String, String> vars,
                                  java.util.Map<String, String> returns) {
        var eq = stmt.indexOf('=');
        if (eq == -1) {
            return indent + "    // TODO";
        }
        var dest = stmt.substring(0, eq).trim();
        var rhs = stmt.substring(eq + 1).trim();
        var tokens = dest.split("\\s+");
        if (tokens.length >= 2) {
            var name = tokens[tokens.length - 1];
            var type = tokens[tokens.length - 2];
            var value = parseValue(rhs);
            var tsType = type.equals("var") ? inferVarType(rhs, vars, returns) : TypeMapper.toTsType(type);
            vars.put(name, tsType);
            return indent + "    let " + name + " : " + tsType + " = " + value + ";";
        }
        return indent + "    // TODO";
    }

    private static String inferVarType(String value, java.util.Map<String, String> vars,
                                       java.util.Map<String, String> returns) {
        var trimmed = value.trim();
        if (vars.containsKey(trimmed)) return vars.get(trimmed);

        if (isInvokable(trimmed)) {
            var open = trimmed.lastIndexOf('(');
            var callee = trimmed.substring(0, open).trim();
            var dot = callee.lastIndexOf('.');
            if (dot != -1) callee = callee.substring(dot + 1).trim();
            if (returns.containsKey(callee)) return returns.get(callee);
        }

        if (trimmed.startsWith("new ")) {
            var rest = trimmed.substring(4).trim();
            var dot = rest.indexOf('.');
            if (dot != -1) rest = rest.substring(0, dot).trim();
            var open = rest.indexOf('(');
            if (open != -1) {
                rest = rest.substring(0, open).trim();
            }
            var generic = rest.indexOf('<');
            if (generic != -1) {
                rest = rest.substring(0, generic).trim();
            }
            return rest.isEmpty() ? "unknown" : rest;
        }

        if (isNumeric(trimmed)) return "number";
        if (trimmed.equals("true") || trimmed.equals("false")) return "boolean";
        if (trimmed.length() >= 2 && trimmed.startsWith("\"") && trimmed.endsWith("\"")) {
            return "string";
        }

        return "unknown";
    }

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

    static String parseValue(String value) {
        var trimmed = value.trim();
        if (trimmed.contains("->")) return trimmed;
        if (trimmed.startsWith("!")) {
            var rest = trimmed.substring(1).trim();
            return "!" + parseValue(rest);
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

    private static String parseValueArg(String value) {
        var trimmed = value.trim();
        return parseValue(trimmed);
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
            if (c == '(') depth++;
            if (c == ')') depth--;
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
            if (c == '(') depth++;
            if (c == ')') depth--;
            part.append(c);
        }
        out.add(part.toString().trim());
        return out;
    }
}
