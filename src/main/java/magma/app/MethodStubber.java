package magma.app;

class MethodStubber {
    static final java.util.Map<String, String> KNOWN_RETURNS = buildKnownReturns();

    private static java.util.Map<String, String> buildKnownReturns() {
        java.util.Map<String, String> map = new java.util.HashMap<>();
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

    static String stubMethods(String source) {
        var lines = source.split("\\R");
        var returns = collectReturnTypes(lines);
        var out = new StringBuilder();
        for (var i = 0; i < lines.length; ) {
            var line = lines[i];
            var trimmed = line.trim();
            if (isInterfaceMethod(trimmed)) {
                out.append(convertInterfaceMethod(line)).append(System.lineSeparator());
                i++;
                continue;
            }
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
        var end = StatementParser.skipBody(lines, index);
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
        var paramVars = StatementParser.paramVars(tsParams);
        StatementParser.parseStatements(lines, start, end, indent, stub, tsReturn, returns, paramVars);
        stub.append(indent).append("}").append(System.lineSeparator());
        return stub.toString();
    }

    private static boolean isInterfaceMethod(String trimmed) {
        return trimmed.endsWith(";") &&
                trimmed.contains("(") &&
                trimmed.contains(")") &&
                !trimmed.startsWith("import") &&
                !trimmed.contains("=") &&
                !trimmed.contains("->");
    }

    private static String convertInterfaceMethod(String line) {
        var trimmed = line.trim();
        var indent = line.substring(0, line.indexOf(trimmed));
        var withoutSemi = trimmed.substring(0, trimmed.length() - 1).trim();
        var open = withoutSemi.indexOf('(');
        var close = withoutSemi.lastIndexOf(')');
        if (open == -1 || close == -1 || close <= open) return line;
        var before = withoutSemi.substring(0, open).trim();
        var params = withoutSemi.substring(open + 1, close).trim();
        var sigTokens = before.split("\\s+");
        if (sigTokens.length < 2) return line;
        var name = sigTokens[sigTokens.length - 1];
        var returnType = sigTokens[sigTokens.length - 2];
        var tsParams = TypeMapper.toTsParams(params).replace(":", " :");
        var tsReturn = TypeMapper.toTsType(returnType);
        var sb = new StringBuilder();
        sb.append(indent).append(name).append("(").append(tsParams).append(")");
        if (!tsReturn.isBlank()) sb.append(": ").append(tsReturn);
        sb.append(";");
        return sb.toString();
    }
}
