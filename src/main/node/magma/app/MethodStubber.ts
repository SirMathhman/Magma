import JdkList from "../list/JdkList";
import ListLike from "../list/ListLike";
export default class MethodStubber {
    stubMethods(source: string): string {
        let lines: var = source.split("\\R");
        let out: var = new StringBuilder();
        let i: (var = 0;
        i lines.length: <;
        // TODO
        let line: var = /* TODO */;
        let trimmed: var = line.trim();
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
        let end: var = skipBody(lines, index);
        let stub: var = buildMethodStub(line, trimmed, lines, /* TODO */, /* TODO */);
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
        let indent: var = line.substring(0, line.indexOf(trimmed));
        let beforeBrace: var = trimmed.substring(0, trimmed.length()).trim();
        let parenStart: var = beforeBrace.indexOf('(/* TODO */);
        let parenEnd: var = beforeBrace./* TODO */;
        if (/* TODO */) {
            return /* TODO */;
        }
        let signatureStart: var = beforeBrace.substring(0, parenStart).trim();
        let params: var = beforeBrace.substring(/* TODO */, parenEnd).trim();
        let sigTokens: var = signatureStart.split("\\s+");
        if (/* TODO */) {
            return /* TODO */;
        }
        let name: var = sigTokens[sigTokens.length - 1];
        let returnType: var = sigTokens.length > 1 ? sigTokens[sigTokens.length - 2] : "void";
        let tsParams: var = TypeMapper.toTsParams(params);
        let tsReturn: var = TypeMapper.toTsType(returnType);
        let stub: var = new StringBuilder();
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
        let trimmedPart: var = part.trim();
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
        let expr: var = stmt.substring(6).trim();
        // TODO
        let expr: ")) = expr.substring(0, expr.length()).trim();
        let value: var = expr.isBlank() ? "" : " " + parseValue(expr);
        stub.append(indent);
        .append("    return");
        .append(value);
        // TODO
        // TODO
        .append(System.lineSeparator());
        // TODO
        private static void parseStatements(/* TODO */, /* TODO */, /* TODO */, /* TODO */, /* TODO */);
        let wrote: var = /* TODO */;
        let i: (var = start;
        // TODO
        // TODO
        let body: var = lines[i].trim();
        // TODO
        // TODO
        let next: var = handleControlBlock(body, lines, i, indent, stub);
        if (/* TODO */) {
            // TODO
            // TODO
        }
        if (body.startsWith("return")) {
            appendReturn(body, indent, stub);
            // TODO
        }
        // TODO
        // TODO
        // TODO
        // TODO
        // TODO
        private static int handleControlBlock(/* TODO */, /* TODO */, /* TODO */, /* TODO */, /* TODO */);
        if ((body.startsWith("if").startsWith("else if")).endsWith("{")) {
            let keyword: var = body.startsWith("else if");
            let cond: var = parseCondition(body);
            let blockEnd: var = skipBody(lines, index);
            appendParsedBlock(stub, indent, keyword, cond, lines, /* TODO */, /* TODO */);
            return blockEnd;
        }
        if (body.startsWith("else").endsWith("{")) {
            let blockEnd: var = skipBody(lines, index);
            appendParsedBlock(stub, indent, "else", /* TODO */, lines, /* TODO */, /* TODO */);
            return blockEnd;
        }
        if (body.startsWith("while").endsWith("{")) {
            let cond: var = parseCondition(body);
            let blockEnd: var = skipBody(lines, index);
            appendParsedBlock(stub, indent, "while", cond, lines, /* TODO */, /* TODO */);
            return blockEnd;
        }
        return index;
        // TODO
        static int skipBody(/* TODO */, /* TODO */);
        let depth: var = 1;
        let i: var = /* TODO */;
        while (i < lines.length && depth > 0) {
            let body: var = /* TODO */;
            let +: depth = body.length().replace("{", "").length();
            let -: depth = body.length().replace("}", "").length();
            // TODO
        }
        return i;
        // TODO
        // TODO
        // TODO
        stub.append(indent).append("    ").append(keyword);
        if (/* TODO */) {
            stub.append("(/* TODO */).append(condition).append(/* TODO */));
        }
        stub.append(" {").append(System.lineSeparator());
        parseStatements(lines, start, end, /* TODO */, stub);
        stub.append(indent).append("    }").append(System.lineSeparator());
        // TODO
        private static String parseCondition(/* TODO */);
        let open: var = stmt.indexOf('(/* TODO */);
        let close: var = stmt./* TODO */;
        if (/* TODO */) {
            return "/* TODO */";
        }
        let inside: var = stmt.substring(/* TODO */, close).trim();
        return parseValue(inside);
        // TODO
        static String parseAssignment(/* TODO */, /* TODO */);
        let eq: var = stmt.indexOf(/* TODO */);
        if (/* TODO */) {
            return /* TODO */;
        }
        let dest: var = stmt.substring(0, eq).trim();
        let rhs: var = stmt.substring(/* TODO */).trim();
        let tokens: var = dest.split("\\s+");
        if (/* TODO */) {
            let name: var = tokens[tokens.length - 1];
            let type: var = tokens[tokens.length - 2];
            let value: var = parseValue(rhs);
            return indent + "    let " + name + ": " + TypeMapper.toTsType(type);
        }
        return /* TODO */;
        // TODO
        static boolean isMemberAccess(/* TODO */);
        return stmt.contains(".") && !stmt.contains("(") && !stmt.contains("=");
        // TODO
        static String parseMemberAccess(/* TODO */, /* TODO */);
        return /* TODO */;
        // TODO
        static boolean isInvokable(/* TODO */);
        let open: var = stmt.indexOf('(/* TODO */);
        let close: var = stmt./* TODO */;
        if (/* TODO */) {
            return /* TODO */;
        }
        let head: var = stmt.substring(0, open).trim();
        return !head.startsWith("if").startsWith("while").startsWith("for");
        // TODO
        static String parseInvokable(/* TODO */, /* TODO */);
        return indent + "    " + stubInvokableExpr(stmt);
        // TODO
        static String parseValue(/* TODO */);
        let trimmed: var = value.trim();
        if (trimmed.startsWith("!")) {
            let rest: var = trimmed.substring(1).trim();
            return "!" + parseValue(rest);
        }
        if (trimmed.startsWith("new ").contains(".") && isInvokable(trimmed)) {
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
        // TODO
        private static String parseValueArg(/* TODO */);
        let trimmed: var = value.trim();
        return parseValue(trimmed);
        // TODO
        private static String parseMemberChain(/* TODO */);
        let parts: var = splitMemberParts(expr);
        return joinMemberParts(parts);
        // TODO
        private static ListLike<String> splitMemberParts(/* TODO */);
        let parts: ListLike<string> = JdkList.create();
        let depth: var = 0;
        let part: var = new StringBuilder();
        let i: (var = 0;
        i < expr.length();
        // TODO
        let c: var = expr.charAt(i);
        if (/* TODO */) {
            parts.add(part.toString());
            part.setLength(0);
            // TODO
        }
        let (c: if = = '(/* TODO */);
        let (c: if = /* TODO */;
        part.append(c);
        // TODO
        parts.add(part.toString());
        return parts;
        // TODO
        private static String joinMemberParts(/* TODO */);
        let out: var = new StringBuilder();
        let i: (var = 0;
        i < parts.size();
        // TODO
        // TODO
        out.append(parseChainSegment(parts.get(i).trim()));
        // TODO
        return out.toString();
        // TODO
        private static String parseChainSegment(/* TODO */);
        if (isInvokable(seg)) {
            return stubInvokableExpr(seg);
        }
        return seg;
        // TODO
        private static boolean isNumeric(/* TODO */);
        // TODO
        let i: var = 0;
        if (s.charAt(0)) {
            let (s.length(): if = /* TODO */;
            // TODO
        }
        let dot: var = /* TODO */;
        // TODO
        i < s.length();
        // TODO
        let c: var = s.charAt(i);
        if (/* TODO */) {
            // TODO
            // TODO
            // TODO
        }
        // TODO
        // TODO
        return /* TODO */;
        // TODO
        private static boolean isIdentifier(/* TODO */);
        // TODO
        // TODO
        let first: var = s.charAt(0);
        if (!((first >= 'a' && first <= 'z') ||(/* TODO */))) {
            return /* TODO */;
        }
        let i: (var = 1;
        i < s.length();
        // TODO
        let c: var = s.charAt(i);
        let letter: var = (c >= 'a' && c <= 'z') ||(/* TODO */);
        let digit: var = /* TODO */;
        if (!(/* TODO */)) {
            return /* TODO */;
        }
        // TODO
        return /* TODO */;
        // TODO
        static String stubInvokableExpr(/* TODO */);
        let close: var = stmt./* TODO */;
        let (close: if = /* TODO */;
        let open: var = findOpenParen(stmt, close);
        let (open: if = /* TODO */;
        let callee: var = stmt.substring(0, open).trim();
        let args: var = stmt.substring(/* TODO */, close).trim();
        let parts: var = splitArgs(args);
        mapArgs(parts);
        let joined: var = joinArgs(parts);
        return callee + "(" + joined + ");
        // TODO
        private static int findOpenParen(/* TODO */, /* TODO */);
        let depth: var = 0;
        let i: (var = close;
        let >: i = 0;
        // TODO
        let c: var = stmt.charAt(i);
        if (/* TODO */) {
            // TODO
            let (depth: if = /* TODO */;
        }
        // TODO
        return -1;
        // TODO
        private static void mapArgs(/* TODO */);
        let i: (var = 0;
        i < parts.size();
        // TODO
        parts.set(i, parseValueArg(parts.get(i)));
        // TODO
        // TODO
        private static String joinArgs(/* TODO */);
        let out: var = new StringBuilder();
        let i: (var = 0;
        i < parts.size();
        // TODO
        // TODO
        out.append(parts.get(i));
        // TODO
        return out.toString();
        // TODO
        private static ListLike<String> splitArgs(/* TODO */);
        let out: ListLike<string> = JdkList.create();
        // TODO
        let depth: var = 0;
        let part: var = new StringBuilder();
        let i: (var = 0;
        i < args.length();
        // TODO
        let c: var = args.charAt(i);
        if (/* TODO */) {
            out.add(part.toString().trim());
            part.setLength(0);
            // TODO
        }
        let (c: if = = '(/* TODO */);
        let (c: if = /* TODO */;
        part.append(c);
        // TODO
        out.add(part.toString().trim());
        return out;
        // TODO
    }
