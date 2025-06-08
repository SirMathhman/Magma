import TypeMapper from "./TypeMapper";
import StatementParser from "./StatementParser";
import PathLike from "./PathLike";
export default class MethodStubber {
    static final java.util.Map<String, String> KNOWN_RETURNS = buildKnownReturns();

    buildKnownReturns(): String> {
        let map : String> = new java.util.HashMap<>();
        map.put("PathLike.walk", "Result<Set<PathLike>>");
        map.put("PathLike.readString", "Result<string>");
        map.put("PathLike.createDirectories", "Option<string>");
        map.put("PathLike.writeString", "Option<string>");
        map.put("PathLike.resolve", "PathLike");
        map.put("PathLike.relativize", "PathLike");
        map.put("PathLike.getParent", "PathLike");
        map.put("PathLike.deleteIfExists", "Option<string>");
        return map;
    }

    stubMethods(source: string): string {
        let lines : unknown = source.split("\\R");
        let returns : String> = collectReturnTypes(lines);
        let out : StringBuilder = new StringBuilder();
        let i : (var = 0;
        i lines.length: <;
        // TODO
        let line : unknown = /* TODO */;
        let trimmed : unknown = line.trim();
        if (isInterfaceMethod(trimmed)) {
            out.append(convertInterfaceMethod(line)).append(System.lineSeparator());
            // TODO
            // TODO
        }
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
        // TODO
        java.util.Map<String, String> {: returns);
        let end : unknown = StatementParser.skipBody(lines, index);
        let stub : unknown = buildMethodStub(line, trimmed, lines, /* TODO */, /* TODO */, returns);
        if (/* TODO */) {
            copyRange(lines, index, end, out);
            // TODO
            out.append(stub);
        }
        return end;
        // TODO
        private static void copyRange(/* TODO */, /* TODO */, /* TODO */, /* TODO */);
        let j : (var = start;
        // TODO
        // TODO
        out.append(lines[j]).append(System.lineSeparator());
        // TODO
        // TODO
        private static java.util.Map<String, String> collectReturnTypes(/* TODO */);
        let map : String> = new java.util.HashMap<>();
        // TODO
        let trimmed : unknown = line.trim();
        // TODO
        let beforeBrace : unknown = trimmed.substring(0, trimmed.length()).trim();
        let open : unknown = beforeBrace.indexOf('(/* TODO */);
        let close : unknown = beforeBrace./* TODO */;
        let (open : if = /* TODO */;
        let signature : unknown = beforeBrace.substring(0, open).trim();
        let tokens : unknown = signature.split("\\s+");
        // TODO
        let name : unknown = tokens[tokens.length - 1];
        let type : unknown = tokens[tokens.length - 2];
        map.put(name, TypeMapper.toTsType(type));
        // TODO
        return map;
        // TODO
        // TODO
        java.util.Map<String, String> {: returns);
        let indent : unknown = line.substring(0, line.indexOf(trimmed));
        let beforeBrace : unknown = trimmed.substring(0, trimmed.length()).trim();
        let parenStart : unknown = beforeBrace.indexOf('(/* TODO */);
        let parenEnd : unknown = beforeBrace./* TODO */;
        if (/* TODO */) {
            return /* TODO */;
        }
        let signatureStart : unknown = beforeBrace.substring(0, parenStart).trim();
        let params : unknown = beforeBrace.substring(/* TODO */, parenEnd).trim();
        let sigTokens : unknown = signatureStart.split("\\s+");
        if (/* TODO */) {
            return /* TODO */;
        }
        let name : unknown = sigTokens[sigTokens.length - 1];
        let returnType : unknown = sigTokens.length > 1 ? sigTokens[sigTokens.length - 2] : "void";
        return s.put(name, TypeMapper.toTsType(returnType));
        let tsParams : unknown = TypeMapper.toTsParams(params);
        let tsReturn : unknown = TypeMapper.toTsType(returnType);
        let stub : StringBuilder = new StringBuilder();
        stub.append(indent).append(name).append("(/* TODO */).append(tsParams).append(/* TODO */));
        if (!tsReturn.isBlank()) {
            stub.append(": ").append(tsReturn);
        }
        stub.append(" {").append(System.lineSeparator());
        let paramVars : unknown = StatementParser.paramVars(tsParams);
        StatementParser.parseStatements(lines, start, end, indent, stub, tsReturn, returns, paramVars);
        stub.append(indent).append("}").append(System.lineSeparator());
        return stub.toString();
        // TODO
        private static boolean isInterfaceMethod(/* TODO */);
        return trimmed.endsWith(";");
        trimmed.contains("(/* TODO */);
        /* */: TODO;
        !trimmed.startsWith("import");
        // TODO
        !trimmed.contains("=>");
        // TODO
        private static String convertInterfaceMethod(/* TODO */);
        let trimmed : unknown = line.trim();
        let indent : unknown = line.substring(0, line.indexOf(trimmed));
        let withoutSemi : unknown = trimmed.substring(0, trimmed.length()).trim();
        let open : unknown = withoutSemi.indexOf('(/* TODO */);
        let close : unknown = withoutSemi./* TODO */;
        let (open : if = /* TODO */;
        let before : unknown = withoutSemi.substring(0, open).trim();
        let params : unknown = withoutSemi.substring(/* TODO */, close).trim();
        let sigTokens : unknown = before.split("\\s+");
        // TODO
        let name : unknown = sigTokens[sigTokens.length - 1];
        let returnType : unknown = sigTokens[sigTokens.length - 2];
        let tsParams : unknown = TypeMapper.toTsParams(params).replace(":", " :");
        let tsReturn : unknown = TypeMapper.toTsType(returnType);
        let sb : StringBuilder = new StringBuilder();
        sb.append(indent).append(name).append("(/* TODO */).append(tsParams).append(/* TODO */));
        // TODO
        // TODO
        // TODO
        return sb.toString();
        // TODO
    }
