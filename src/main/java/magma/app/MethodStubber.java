package magma.app;

class MethodStubber {
    static String stubMethods(String source) {
        String[] lines = source.split("\\R");
        StringBuilder out = new StringBuilder();
        int i = 0;
        while (i < lines.length) {
            String line = lines[i];
            String trimmed = line.trim();
            if (trimmed.endsWith("{") && trimmed.contains("(") && !trimmed.startsWith("export")) {
                int end = skipBody(lines, i);
                String stub = buildMethodStub(line, trimmed, lines, i + 1, end - 1);
                if (stub == null) {
                    copyRange(lines, i, end, out);
                } else {
                    out.append(stub);
                }
                i = end;
                continue;
            }
            out.append(line).append(System.lineSeparator());
            i++;
        }
        return out.toString().trim();
    }

    private static void copyRange(String[] lines, int start, int end, StringBuilder out) {
        for (int j = start; j < end; j++) {
            out.append(lines[j]).append(System.lineSeparator());
        }
    }

    static String buildMethodStub(String line, String trimmed, String[] lines, int start, int end) {
        String indent = line.substring(0, line.indexOf(trimmed));
        String beforeBrace = trimmed.substring(0, trimmed.length() - 1).trim();
        int parenStart = beforeBrace.indexOf('(');
        int parenEnd = beforeBrace.lastIndexOf(')');
        if (parenStart == -1 || parenEnd == -1) {
            return null;
        }
        String signatureStart = beforeBrace.substring(0, parenStart).trim();
        String params = beforeBrace.substring(parenStart + 1, parenEnd).trim();
        String[] sigTokens = signatureStart.split("\\s+");
        if (sigTokens.length == 0) {
            return null;
        }
        String name = sigTokens[sigTokens.length - 1];
        String returnType = sigTokens.length > 1 ? sigTokens[sigTokens.length - 2] : "void";
        String tsParams = TypeMapper.toTsParams(params);
        String tsReturn = TypeMapper.toTsType(returnType);
        StringBuilder stub = new StringBuilder();
        stub.append(indent).append(name).append("(").append(tsParams).append(")");
        if (!tsReturn.isBlank()) {
            stub.append(": ").append(tsReturn);
        }
        stub.append(" {").append(System.lineSeparator());
        boolean wrote = false;
        for (int i = start; i < end; i++) {
            String body = lines[i].trim();
            if (body.isEmpty()) {
                continue;
            }
            wrote = true;
            if ((body.startsWith("if") || body.startsWith("else if")) && body.endsWith("{")) {
                appendBlockStub(stub, indent, "if", true);
                i = skipBody(lines, i) - 1;
                continue;
            }
            if (body.startsWith("else") && body.endsWith("{")) {
                appendBlockStub(stub, indent, "else", false);
                i = skipBody(lines, i) - 1;
                continue;
            }
            if (body.startsWith("while") && body.endsWith("{")) {
                appendBlockStub(stub, indent, "while", true);
                i = skipBody(lines, i) - 1;
                continue;
            }
            if (body.startsWith("return")) {
                String expr = body.substring(6).trim();
                if (expr.endsWith(";")) {
                    expr = expr.substring(0, expr.length() - 1).trim();
                }
                if (expr.isBlank()) {
                    stub.append(indent).append("    return;").append(System.lineSeparator());
                } else if (isMemberAccess(expr)) {
                    stub.append(indent).append("    return ").append(expr).append(";")
                       .append(System.lineSeparator());
                } else {
                    stub.append(indent).append("    return /* TODO */;")
                       .append(System.lineSeparator());
                }
            } else {
                appendParts(body.split(";"), indent, stub);
            }
        }
        if (!wrote) {
            stub.append(indent).append("    // TODO").append(System.lineSeparator());
        }
        stub.append(indent).append("}").append(System.lineSeparator());
        return stub.toString();
    }

    private static void appendParts(String[] parts, String indent, StringBuilder stub) {
        for (String part : parts) {
            String trimmedPart = part.trim();
            if (trimmedPart.isEmpty()) continue;
            if (trimmedPart.startsWith("return")) {
                String expr = trimmedPart.substring(6).trim();
                if (expr.endsWith(";")) {
                    expr = expr.substring(0, expr.length() - 1).trim();
                }
                if (expr.isBlank()) {
                    stub.append(indent).append("    return;").append(System.lineSeparator());
                } else if (isMemberAccess(expr)) {
                    stub.append(indent).append("    return ").append(expr).append(";")
                        .append(System.lineSeparator());
                } else {
                    stub.append(indent).append("    return /* TODO */;").append(System.lineSeparator());
                }
            } else if (trimmedPart.contains("=")) {
                stub.append(parseAssignment(trimmedPart, indent)).append(System.lineSeparator());
            } else if (isInvokable(trimmedPart)) {
                stub.append(parseInvokable(trimmedPart, indent)).append(System.lineSeparator());
            } else if (isMemberAccess(trimmedPart)) {
                stub.append(parseMemberAccess(trimmedPart, indent)).append(System.lineSeparator());
            } else {
                stub.append(indent).append("    // TODO").append(System.lineSeparator());
            }
        }
    }

    static int skipBody(String[] lines, int index) {
        int depth = 1;
        int i = index + 1;
        while (i < lines.length && depth > 0) {
            String body = lines[i];
            depth += body.length() - body.replace("{", "").length();
            depth -= body.length() - body.replace("}", "").length();
            i++;
        }
        return i;
    }

    static void appendBlockStub(StringBuilder stub, String indent, String keyword, boolean withCondition) {
        stub.append(indent).append("    ").append(keyword);
        if (withCondition) {
            stub.append(" (/* TODO */)");
        }
        stub.append(" {").append(System.lineSeparator());
        stub.append(indent).append("        // TODO").append(System.lineSeparator());
        stub.append(indent).append("    }").append(System.lineSeparator());
    }

    static String parseAssignment(String stmt, String indent) {
        int eq = stmt.indexOf('=');
        if (eq == -1) {
            return indent + "    // TODO";
        }
        String dest = stmt.substring(0, eq).trim();
        String rhs = stmt.substring(eq + 1).trim();
        String[] tokens = dest.split("\\s+");
        if (tokens.length >= 2) {
            String name = tokens[tokens.length - 1];
            String type = tokens[tokens.length - 2];
            String value = parseValue(rhs);
            return indent + "    let " + name + ": " + TypeMapper.toTsType(type) + " = " + value + ";";
        }
        return indent + "    // TODO";
    }

    static boolean isMemberAccess(String stmt) {
        return stmt.contains(".") && !stmt.contains("(") && !stmt.contains("=");
    }

    static String parseMemberAccess(String stmt, String indent) {
        return indent + "    " + stmt + ";";
    }

    static boolean isInvokable(String stmt) {
        int open = stmt.indexOf('(');
        int close = stmt.lastIndexOf(')');
        if (open == -1 || close == -1 || close <= open) {
            return false;
        }
        String head = stmt.substring(0, open).trim();
        return !head.startsWith("if") && !head.startsWith("while") && !head.startsWith("for");
    }

    static String parseInvokable(String stmt, String indent) {
        return indent + "    " + stubInvokableExpr(stmt) + ";";
    }

    static String parseValue(String value) {
        String trimmed = value.trim();
        if (isInvokable(trimmed)) {
            return stubInvokableExpr(trimmed);
        }
        if ((trimmed.length() >= 2 && trimmed.startsWith("\"") && trimmed.endsWith("\"")) || isMemberAccess(trimmed)) {
            return trimmed;
        }
        return "/* TODO */";
    }

    private static String parseValueArg(String value) {
        String trimmed = value.trim();
        if (isInvokable(trimmed)) {
            return "/* TODO */";
        }
        if ((trimmed.length() >= 2 && trimmed.startsWith("\"") && trimmed.endsWith("\"")) || isMemberAccess(trimmed)) {
            return trimmed;
        }
        return "/* TODO */";
    }

    static String stubInvokableExpr(String stmt) {
        int close = stmt.lastIndexOf(')');
        if (close == -1) {
            return "/* TODO */";
        }
        int open = -1;
        int depth = 0;
        for (int i = close; i >= 0; i--) {
            char c = stmt.charAt(i);
            if (c == ')') {
                depth++;
            } else if (c == '(') {
                depth--;
                if (depth == 0) {
                    open = i;
                    break;
                }
            }
        }
        if (open == -1) {
            return "/* TODO */";
        }
        String head = stmt.substring(0, open).trim();
        if (head.startsWith("new ") && head.contains(".")) {
            return stmt;
        }
        boolean isNew = head.startsWith("new ") && !head.contains(".");
        String callee = "/* TODO */";
        if (isNew) {
            String afterNew = head.substring(4).trim();
            if (!afterNew.isBlank()) {
                callee = "new " + afterNew;
            } else {
                callee = "new /* TODO */";
            }
        }
        String args = stmt.substring(open + 1, close).trim();
        java.util.List<String> parts = splitArgs(args);
        for (int i = 0; i < parts.size(); i++) {
            parts.set(i, parseValueArg(parts.get(i)));
        }
        String joined = String.join(", ", parts);
        if (!isNew) {
            callee = "/* TODO */";
        }
        return callee + "(" + joined + ")";
    }

    private static java.util.List<String> splitArgs(String args) {
        java.util.List<String> out = new java.util.ArrayList<>();
        if (args.isBlank()) return out;
        int depth = 0;
        StringBuilder part = new StringBuilder();
        for (int i = 0; i < args.length(); i++) {
            char c = args.charAt(i);
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
