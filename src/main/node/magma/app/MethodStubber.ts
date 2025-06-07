import ArrayList from "../../java/util/ArrayList";
import List from "../../java/util/List";
export default class MethodStubber {
    stubMethods(source: string): string {
        let lines: any = source.split("\\R");
        let out: any = new StringBuilder();
        let i: any = 0;
        while (i < lines.length) {
            // TODO
        }
        private static void copyRange(/* TODO */, /* TODO */, /* TODO */, /* TODO */);
        let j: any = start;
        // TODO
        // TODO
        out.append(lines[j]).append(System.lineSeparator());
        // TODO
        // TODO
        static String buildMethodStub(/* TODO */, /* TODO */, /* TODO */, /* TODO */, /* TODO */);
        let indent: any = line.substring(0, line.indexOf(trimmed));
        let beforeBrace: any = trimmed.substring(0, trimmed.length()).trim();
        let parenStart: any = beforeBrace.indexOf('(/* TODO */);
        let parenEnd: any = beforeBrace./* TODO */;
        if (/* TODO */) {
            // TODO
        }
        let signatureStart: any = beforeBrace.substring(0, parenStart).trim();
        let params: any = beforeBrace.substring(/* TODO */, parenEnd).trim();
        let sigTokens: any = signatureStart.split("\\s+");
        if (/* TODO */) {
            // TODO
        }
        let name: any = sigTokens[sigTokens.length - 1];
        let returnType: any = sigTokens.length > 1 ? sigTokens[sigTokens.length - 2] : "void";
        let tsParams: any = TypeMapper.toTsParams(params);
        let tsReturn: any = TypeMapper.toTsType(returnType);
        let stub: any = new StringBuilder();
        stub.append(indent).append(name).append("(/* TODO */).append(tsParams).append(/* TODO */));
        if (!tsReturn.isBlank()) {
            // TODO
        }
        stub.append(" {").append(System.lineSeparator());
        let wrote: any = /* TODO */;
        let i: any = start;
        // TODO
        // TODO
        let body: any = lines[i].trim();
        if (body.isEmpty()) {
            // TODO
        }
        // TODO
        if ((body.startsWith("if").startsWith("else if")).endsWith("{")) {
            // TODO
        }
        if (body.startsWith("else").endsWith("{")) {
            // TODO
        }
        if (body.startsWith("while").endsWith("{")) {
            // TODO
        }
        if (body.startsWith("return")) {
            // TODO
        }
        // TODO
        if (!wrote) {
            // TODO
        }
        stub.append(indent).append("}").append(System.lineSeparator());
        return stub.toString();
        // TODO
        private static void appendParts(/* TODO */, /* TODO */, /* TODO */);
        // TODO
        let trimmedPart: any = part.trim();
        // TODO
        if (trimmedPart.startsWith("return")) {
            // TODO
        }
        // TODO
        // TODO
        static int skipBody(/* TODO */, /* TODO */);
        let depth: any = 1;
        let i: any = /* TODO */;
        while (i < lines.length && depth > 0) {
            // TODO
        }
        return i;
        // TODO
        static void appendBlockStub(/* TODO */, /* TODO */, /* TODO */, /* TODO */);
        stub.append(indent).append("    ").append(keyword);
        if (/* TODO */) {
            // TODO
        }
        stub.append(" {").append(System.lineSeparator());
        stub.append(indent).append("        // TODO").append(System.lineSeparator());
        stub.append(indent).append("    }").append(System.lineSeparator());
        // TODO
        private static String parseCondition(/* TODO */);
        let open: any = stmt.indexOf('(/* TODO */);
        let close: any = stmt./* TODO */;
        if (/* TODO */) {
            // TODO
        }
        let inside: any = stmt.substring(/* TODO */, close).trim();
        return parseValue(inside);
        // TODO
        static String parseAssignment(/* TODO */, /* TODO */);
        let eq: any = stmt.indexOf(/* TODO */);
        if (/* TODO */) {
            // TODO
        }
        let dest: any = stmt.substring(0, eq).trim();
        let rhs: any = stmt.substring(/* TODO */).trim();
        let tokens: any = dest.split("\\s+");
        if (/* TODO */) {
            // TODO
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
        let open: any = stmt.indexOf('(/* TODO */);
        let close: any = stmt./* TODO */;
        if (/* TODO */) {
            // TODO
        }
        let head: any = stmt.substring(0, open).trim();
        return !head.startsWith("if").startsWith("while").startsWith("for");
        // TODO
        static String parseInvokable(/* TODO */, /* TODO */);
        return indent + "    " + stubInvokableExpr(stmt);
        // TODO
        static String parseValue(/* TODO */);
        let trimmed: any = value.trim();
        if (trimmed.startsWith("!")) {
            // TODO
        }
        if (trimmed.startsWith("new ").contains(".") && isInvokable(trimmed)) {
            // TODO
        }
        if (trimmed.contains(".") && !trimmed.contains("=")) {
            // TODO
        }
        if (isInvokable(trimmed)) {
            // TODO
        }
        if (trimmed.length() >= 2 && trimmed.startsWith("\"") && trimmed.endsWith("\"")) {
            // TODO
        }
        if (isMemberAccess(trimmed) || isNumeric(trimmed)) {
            // TODO
        }
        if (isIdentifier(trimmed)) {
            // TODO
        }
        if (isNumeric(trimmed)) {
            // TODO
        }
        return "/* TODO */";
        // TODO
        private static String parseValueArg(/* TODO */);
        let trimmed: any = value.trim();
        return parseValue(trimmed);
        // TODO
        private static String parseMemberChain(/* TODO */);
        let parts: List<string> = new ArrayList<>();
        let depth: any = 0;
        let part: any = new StringBuilder();
        let i: any = 0;
        i < expr.length();
        // TODO
        let c: any = expr.charAt(i);
        if (/* TODO */) {
            // TODO
        }
        let (c: any = = '(/* TODO */);
        let (c: any = /* TODO */;
        part.append(c);
        // TODO
        parts.add(part.toString());
        let out: any = new StringBuilder();
        let i: any = 0;
        i < parts.size();
        // TODO
        // TODO
        out.append(parseChainSegment(parts.get(i).trim()));
        // TODO
        return out.toString();
        // TODO
        private static String parseChainSegment(/* TODO */);
        if (isInvokable(seg)) {
            // TODO
        }
        return seg;
        // TODO
        private static boolean isNumeric(/* TODO */);
        // TODO
        let i: any = 0;
        if (s.charAt(0)) {
            // TODO
        }
        let dot: any = /* TODO */;
        // TODO
        i < s.length();
        // TODO
        let c: any = s.charAt(i);
        if (/* TODO */) {
            // TODO
        }
        // TODO
        // TODO
        return /* TODO */;
        // TODO
        private static boolean isIdentifier(/* TODO */);
        // TODO
        // TODO
        let first: any = s.charAt(0);
        if (!((first >= 'a' && first <= 'z') ||(/* TODO */))) {
            // TODO
        }
        let i: any = 1;
        i < s.length();
        // TODO
        let c: any = s.charAt(i);
        let letter: any = (c >= 'a' && c <= 'z') ||(/* TODO */);
        let digit: any = /* TODO */;
        if (!(/* TODO */)) {
            // TODO
        }
        // TODO
        return /* TODO */;
        // TODO
        static String stubInvokableExpr(/* TODO */);
        let close: any = stmt./* TODO */;
        if (/* TODO */) {
            // TODO
        }
        let open: any = -1;
        let depth: any = 0;
        let i: any = close;
        let >: any = 0;
        // TODO
        let c: any = stmt.charAt(i);
        if (/* TODO */) {
            // TODO
        }
        // TODO
        if (/* TODO */) {
            // TODO
        }
        let callee: any = stmt.substring(0, open).trim();
        let args: any = stmt.substring(/* TODO */, close).trim();
        let parts: any = splitArgs(args);
        parts.replaceAll(/* TODO */);
        let joined: any = String.join(/* TODO */, /* TODO */, parts);
        return callee + "(" + joined + ");
        // TODO
        private static List<String> splitArgs(/* TODO */);
        let out: List<string> = new ArrayList<>();
        // TODO
        let depth: any = 0;
        let part: any = new StringBuilder();
        let i: any = 0;
        i < args.length();
        // TODO
        let c: any = args.charAt(i);
        if (/* TODO */) {
            // TODO
        }
        let (c: any = = '(/* TODO */);
        let (c: any = /* TODO */;
        part.append(c);
        // TODO
        out.add(part.toString().trim());
        return out;
        // TODO
    }
