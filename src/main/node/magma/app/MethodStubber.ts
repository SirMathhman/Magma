import JdkList from "../list/JdkList";
import ListLike from "../list/ListLike";
export default class MethodStubber {
    stubMethods(source: string): string {
        let lines: unknown = source.split("\\R");
        let out : StringBuilder = new StringBuilder();
        let i: (var = 0;
        i lines.length: <;
        // TODO
        let line: unknown = /* TODO */;
        let trimmed: unknown = line.trim();
        if (shouldCopyLine(trimmed)) {
            out.append(line).append(System.lineSeparator());
            // TODO
            // TODO
        }
        // TODO
        // TODO
        return out.toString().trim();
    }

    shouldCopyLine(trimmed: string): boolean {
        return !trimmed.endsWith("{").contains("(") || trimmed.startsWith("export");
        // TODO
        private static int appendStub(/* TODO */, /* TODO */, /* TODO */, /* TODO */, /* TODO */);
        let end: unknown = skipBody(lines, index);
        let stub: unknown = buildMethodStub(line, trimmed, lines, /* TODO */, /* TODO */);
        if (/* TODO */) {
            copyRange(lines, index, end, out);
            // TODO
            out.append(stub);
        }
        return end;
        // TODO
        private static void copyRange(/* TODO */, /* TODO */, /* TODO */, /* TODO */);
        let j: (var = start;
        // TODO
        // TODO
        out.append(lines[j]).append(System.lineSeparator());
        // TODO
        // TODO
        static String buildMethodStub(/* TODO */, /* TODO */, /* TODO */, /* TODO */, /* TODO */);
        let indent: unknown = line.substring(0, line.indexOf(trimmed));
        let beforeBrace: unknown = trimmed.substring(0, trimmed.length()).trim();
        let parenStart: unknown = beforeBrace.indexOf('(/* TODO */);
        let parenEnd: unknown = beforeBrace./* TODO */;
        if (/* TODO */) {
            return /* TODO */;
        }
        let signatureStart: unknown = beforeBrace.substring(0, parenStart).trim();
        let params: unknown = beforeBrace.substring(/* TODO */, parenEnd).trim();
        let sigTokens: unknown = signatureStart.split("\\s+");
        if (/* TODO */) {
            return /* TODO */;
        }
        let name: unknown = sigTokens[sigTokens.length - 1];
        let returnType: unknown = sigTokens.length > 1 ? sigTokens[sigTokens.length - 2] : "void";
        let tsParams: unknown = TypeMapper.toTsParams(params);
        let tsReturn: unknown = TypeMapper.toTsType(returnType);
        let stub : StringBuilder = new StringBuilder();
        stub.append(indent).append(name).append("(/* TODO */).append(tsParams).append(/* TODO */));
        if (!tsReturn.isBlank()) {
            stub.append(": ").append(tsReturn);
        }
        stub.append(" {").append(System.lineSeparator());
        parseStatements(lines, start, end, indent, stub);
        stub.append(indent).append("}").append(System.lineSeparator());
        return stub.toString();
        // TODO
        private static void appendParts(/* TODO */, /* TODO */, /* TODO */);
        // TODO
        let trimmedPart: unknown = part.trim();
        // TODO
        if (trimmedPart.startsWith("return")) {
            appendReturn(trimmedPart, indent, stub);
            // TODO
        }
        if (trimmedPart.contains("=")) {
            stub.append(parseAssignment(trimmedPart, indent)).append(System.lineSeparator());
            // TODO
        }
        if (isInvokable(trimmedPart)) {
            stub.append(parseInvokable(trimmedPart, indent)).append(System.lineSeparator());
            // TODO
        }
        if (isMemberAccess(trimmedPart)) {
            stub.append(parseMemberAccess(trimmedPart, indent)).append(System.lineSeparator());
            // TODO
        }
        stub.append(indent).append("    // TODO").append(System.lineSeparator());
        // TODO
        // TODO
        private static void appendReturn(/* TODO */, /* TODO */, /* TODO */);
        let expr: unknown = stmt.substring(6).trim();
        // TODO
        let expr: ")) = expr.substring(0, expr.length()).trim();
        let value: unknown = expr.isBlank() ? "" : " " + parseValue(expr);
        stub.append(indent);
        .append("    return");
        .append(value);
        // TODO
        // TODO
        .append(System.lineSeparator());
        // TODO
        private static void parseStatements(/* TODO */, /* TODO */, /* TODO */, /* TODO */, /* TODO */);
        let wrote: boolean = /* TODO */;
        let i: (var = start;
        // TODO
        // TODO
        let body: unknown = lines[i].trim();
        // TODO
        // TODO
            if (body.contains("=>") && body.endsWith("{")) {
                i = copyArrowBlock(lines, i, stub) - 1;
                continue;
            }

            var next = handleControlBlock(body, lines, i, indent, stub);
            if (next != i) {
                i = next - 1;
                continue;
            }

            if (body.startsWith("return")) {
                appendReturn(body, indent, stub);
                continue;
            }

            appendParts(body.split(";"), indent, stub);
        }
        if (!wrote) stub.append(indent).append("    // TODO").append(System.lineSeparator());
    }

    private static int handleControlBlock(String body, String[] lines, int index, String indent, StringBuilder stub) {
        if ((body.startsWith("if") || body.startsWith("else if")) && body.endsWith("{")) {
            var keyword = body.startsWith("else if") ? "else if" : "if";
            var cond = parseCondition(body);
            var blockEnd = skipBody(lines, index);
            appendParsedBlock(stub, indent, keyword, cond, lines, index + 1, blockEnd - 1);
            return blockEnd;
        }
        if (body.startsWith("else") && body.endsWith("{")) {
            var blockEnd = skipBody(lines, index);
            appendParsedBlock(stub, indent, "else", null, lines, index + 1, blockEnd - 1);
            return blockEnd;
        }
        if (body.startsWith("while") && body.endsWith("{")) {
            var cond = parseCondition(body);
            var blockEnd = skipBody(lines, index);
            appendParsedBlock(stub, indent, "while", cond, lines, index + 1, blockEnd - 1);
            return blockEnd;
        }
        return index;
    }

    static int skipBody(String[] lines, int index) {
        depth: var;
        i: var;
        while (i < lines.length && depth > 0) {
            body: var;
            depth += body.length() - body.replace("{", "").length();
            depth -= body.length() - body.replace("}", "").length();
            i++;
        }
        return i;
    }

    private static int copyArrowBlock(String[] lines, int start, StringBuilder stub) {
        i: var;
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
                                  String condition, String[] lines, int start, int end) {
        stub.append(indent).append("    ").append(keyword);
        if (condition != null) {
            stub.append(" (").append(condition).append(")");
        }
        stub.append(" {").append(System.lineSeparator());
        parseStatements(lines, start, end, indent + "    ", stub);
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

    static String parseAssignment(String stmt, String indent) {
        var eq = stmt.indexOf('=');
        if (eq == -1) {
            return indent + "    // TODO";
        }
        var dest = stmt.substring(0, eq).trim();
        var rhs = stmt.substring(eq + 1).trim();
        var tokens = dest.split("\\s+");
        if (tokens.length >= 2) {
            name: var;
            type: var;
            var value = parseValue(rhs);
            var tsType = type.equals("var") ? inferVarType(rhs) : TypeMapper.toTsType(type);
            var spacing = type.equals("var") && needsSpace(tsType) ? " " : "";
            return indent + "    let " + name + spacing + ": " + tsType + " = " + value + ";";
        }
        return indent + "    // TODO";
    }

    private static boolean needsSpace(String tsType) {
        return !(tsType.equals("number") || tsType.equals("string") || tsType.equals("boolean") || tsType.equals("unknown"));
    }

    private static String inferVarType(String value) {
        var trimmed = value.trim();
        if (trimmed.startsWith("new ")) {
            var rest = trimmed.substring(4).trim();
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
        var arrow = stmt.indexOf("=>");
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
        if (trimmed.contains("=>")) return trimmed;
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
        depth: var;
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
        i: var;
        if (s.charAt(0) == '-') {
            if (s.length() == 1) return false;
            i = 1;
        }
        dot: var;
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
            digit: var;
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
        depth: var;
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
        depth: var;
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
    }
