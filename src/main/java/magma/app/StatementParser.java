package magma.app;

class StatementParser {
    static void parseStatements(String[] lines, int start, int end, String indent,
                                StringBuilder stub, String returnType,
                                java.util.Map<String, String> returns,
                                java.util.Map<String, String> vars) {
        var wrote = false;
        if (vars == null) vars = new java.util.HashMap<>();
        for (var i = start; i < end; i++) {
            var body = lines[i].trim();
            if (body.isEmpty()) continue;
            wrote = true;
            if (body.contains("->") && body.endsWith("{")) {
                i = parseArrowBlock(lines, i, stub, returns, vars) - 1;
                continue;
            }
            var next = handleControlBlock(body, lines, i, indent, stub, returns, returnType, vars);
            if (next != i) {
                i = next - 1;
                continue;
            }
            if (body.startsWith("return")) {
                appendReturn(body, indent, stub, returnType);
                continue;
            }
            appendParts(body.split(";"), indent, stub, vars, returns, returnType);
        }
        if (!wrote) stub.append(indent).append("    // TODO").append(System.lineSeparator());
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

    private static int parseArrowBlock(String[] lines, int start, StringBuilder stub,
                                       java.util.Map<String, String> returns,
                                       java.util.Map<String, String> vars) {
        var indent = lines[start].substring(0, lines[start].indexOf(lines[start].trim()));
        stub.append(lines[start]).append(System.lineSeparator());
        var end = skipBody(lines, start);
        if (end - start > 2) {
            parseStatements(lines, start + 1, end - 1, indent, stub, null, returns, vars);
        }
        stub.append(lines[end - 1]).append(System.lineSeparator());
        return end;
    }

    static void appendParsedBlock(StringBuilder stub, String indent, String keyword,
                                  String condition, String[] lines, int start, int end,
                                  java.util.Map<String, String> returns, String returnType,
                                  java.util.Map<String, String> vars) {
        stub.append(indent).append("    ").append(keyword);
        if (condition != null) {
            stub.append(" (").append(condition).append(")");
        }
        stub.append(" {").append(System.lineSeparator());
        parseStatements(lines, start, end, indent + "    ", stub, returnType, returns, vars);
        stub.append(indent).append("    }").append(System.lineSeparator());
    }

    private static int handleControlBlock(String body, String[] lines, int index, String indent,
                                          StringBuilder stub, java.util.Map<String, String> returns,
                                          String returnType, java.util.Map<String, String> vars) {
        if ((body.startsWith("if") || body.startsWith("else if")) && body.endsWith("{")) {
            var keyword = body.startsWith("else if") ? "else if" : "if";
            var cond = parseCondition(body);
            var blockEnd = skipBody(lines, index);
            appendParsedBlock(stub, indent, keyword, cond, lines, index + 1, blockEnd - 1, returns, returnType, vars);
            return blockEnd;
        }
        if (body.startsWith("else") && body.endsWith("{")) {
            var blockEnd = skipBody(lines, index);
            appendParsedBlock(stub, indent, "else", null, lines, index + 1, blockEnd - 1, returns, returnType, vars);
            return blockEnd;
        }
        if (body.startsWith("while") && body.endsWith("{")) {
            var cond = parseCondition(body);
            var blockEnd = skipBody(lines, index);
            appendParsedBlock(stub, indent, "while", cond, lines, index + 1, blockEnd - 1, returns, returnType, vars);
            return blockEnd;
        }
        return index;
    }

    private static void appendParts(String[] parts, String indent, StringBuilder stub,
                                    java.util.Map<String, String> vars,
                                    java.util.Map<String, String> returns,
                                    String returnType) {
        for (var part : parts) {
            var trimmedPart = part.trim();
            if (trimmedPart.isEmpty()) continue;
            if (trimmedPart.startsWith("return")) {
                appendReturn(trimmedPart, indent, stub, returnType);
                continue;
            }
            if (trimmedPart.contains("=")) {
                if (trimmedPart.endsWith("(")) {
                    var eq = trimmedPart.indexOf('=');
                    var dest = trimmedPart.substring(0, eq).trim();
                    var rhs = trimmedPart.substring(eq + 1).trim();
                    var tokens = dest.split("\\s+");
                    if (tokens.length >= 2) {
                        var name = tokens[tokens.length - 1];
                        var type = tokens[tokens.length - 2];
                        var tsType = type.equals("var") ? inferVarType(rhs, vars, returns)
                                                       : TypeMapper.toTsType(type);
                        vars.put(name, tsType);
                        stub.append(indent).append("    let ")
                            .append(name).append(" : ")
                            .append(tsType).append(" = ")
                            .append(rhs).append(System.lineSeparator());
                    } else {
                        stub.append(indent).append("    ")
                            .append(trimmedPart).append(System.lineSeparator());
                    }
                } else {
                    stub.append(parseAssignment(trimmedPart, indent, vars, returns))
                        .append(System.lineSeparator());
                }
                continue;
            }
            if (ExpressionParser.isInvokable(trimmedPart)) {
                stub.append(ExpressionParser.parseInvokable(trimmedPart, indent)).append(System.lineSeparator());
                continue;
            }
            if (ExpressionParser.isMemberAccess(trimmedPart)) {
                stub.append(ExpressionParser.parseMemberAccess(trimmedPart, indent)).append(System.lineSeparator());
                continue;
            }
            stub.append(indent).append("    // TODO").append(System.lineSeparator());
        }
    }

    private static void appendReturn(String stmt, String indent, StringBuilder stub, String returnType) {
        var expr = stmt.substring(6).trim();
        if (expr.endsWith(";")) expr = expr.substring(0, expr.length() - 1).trim();
        var value = expr.isBlank() ? "" : " " + ExpressionParser.parseValue(expr, returnType);
        stub.append(indent)
            .append("    return")
            .append(value)
            .append(";")
            .append(System.lineSeparator());
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
            var tsType = type.equals("var") ? inferVarType(rhs, vars, returns) : TypeMapper.toTsType(type);
            var value = ExpressionParser.parseValue(rhs, tsType);
            vars.put(name, tsType);
            return indent + "    let " + name + " : " + tsType + " = " + value + ";";
        }
        return indent + "    // TODO";
    }

    private static String parseCondition(String stmt) {
        var open = stmt.indexOf('(');
        var close = stmt.lastIndexOf(')');
        if (open == -1 || close == -1 || close <= open) {
            return "/* TODO */";
        }
        var inside = stmt.substring(open + 1, close).trim();
        return ExpressionParser.parseValue(inside);
    }

    private static String inferVarType(String value, java.util.Map<String, String> vars,
                                       java.util.Map<String, String> returns) {
        var trimmed = value.trim();
        if (vars.containsKey(trimmed)) return vars.get(trimmed);

        if (ExpressionParser.isInvokable(trimmed)) {
            var open = trimmed.lastIndexOf('(');
            var callee = trimmed.substring(0, open).trim();
            var dot = callee.lastIndexOf('.');
            if (dot != -1) {
                var receiver = callee.substring(0, dot).trim();
                var name = callee.substring(dot + 1).trim();
                var type = vars.get(receiver);
                if (type != null) {
                    var key = type + "." + name;
                    if (MethodStubber.KNOWN_RETURNS.containsKey(key)) {
                        return MethodStubber.KNOWN_RETURNS.get(key);
                    }
                }
                callee = name;
            }
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

    static java.util.Map<String, String> paramVars(String tsParams) {
        java.util.Map<String, String> map = new java.util.HashMap<>();
        if (tsParams.isBlank()) return map;
        var parts = tsParams.split(",");
        for (var i = 0; i < parts.length; i++) {
            var p = parts[i].trim();
            var colon = p.indexOf(':');
            if (colon == -1) continue;
            var name = p.substring(0, colon).trim();
            var type = p.substring(colon + 1).trim();
            map.put(name, type);
        }
        return map;
    }
}
