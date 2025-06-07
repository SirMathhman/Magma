package com.example;

/**
 * Very small prototype that converts a fragment of Java to TypeScript.
 */
public class Transpiler {

    /**
     * Transpiles the given Java source code to TypeScript.
     * Currently removes the package declaration and rewrites simple class
     * definitions. Modifiers before the {@code class} keyword are replaced with
     * {@code export default}.
     *
     * @param javaSource the Java source text
     * @return transpiled TypeScript
     */
    public String toTypeScript(String javaSource) {
        String withoutPackage = removePackage(javaSource);

        String[] lines = withoutPackage.split("\\R");
        StringBuilder ts = new StringBuilder();
        for (String line : lines) {
            int classIdx = line.indexOf("class");
            int enumIdx = line.indexOf("enum");
            int ifaceIdx = line.indexOf("interface");
            int brace = line.indexOf('{');
            if (classIdx != -1 && brace != -1 && classIdx < brace) {
                String afterClass = line.substring(classIdx);
                ts.append("export default ").append(afterClass).append(System.lineSeparator());
            } else if (ifaceIdx != -1 && brace != -1 && ifaceIdx < brace) {
                String afterIface = line.substring(ifaceIdx);
                ts.append("export ").append(afterIface).append(System.lineSeparator());
            } else if (enumIdx != -1 && brace != -1 && enumIdx < brace) {
                String afterEnum = line.substring(enumIdx);
                ts.append("export ").append(afterEnum).append(System.lineSeparator());
            } else {
                ts.append(line).append(System.lineSeparator());
            }
        }

        String withMethods = stubMethods(ts.toString().trim());
        String withFields = transpileFields(withMethods);
        String withArrows = convertArrowFunctions(withFields);
        return stubArrowAssignments(withArrows);
    }

    private String stubMethods(String source) {
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
                    for (int j = i; j < end; j++) {
                        out.append(lines[j]).append(System.lineSeparator());
                    }
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

    private String buildMethodStub(String line, String trimmed, String[] lines, int start, int end) {
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
        String tsParams = toTsParams(params);
        String tsReturn = toTsType(returnType);
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
                stub.append(indent).append("    return /* TODO */;").append(System.lineSeparator());
            } else {
                String[] parts = body.split(";");
                for (String part : parts) {
                    if (part.trim().isEmpty()) continue;
                    if (part.trim().startsWith("return")) {
                        stub.append(indent).append("    return /* TODO */;").append(System.lineSeparator());
                    } else {
                        stub.append(indent).append("    // TODO").append(System.lineSeparator());
                    }
                }
            }
        }
        if (!wrote) {
            stub.append(indent).append("    // TODO").append(System.lineSeparator());
        }
        stub.append(indent).append("}").append(System.lineSeparator());
        return stub.toString();
    }

    private int skipBody(String[] lines, int index) {
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

    private void appendBlockStub(StringBuilder stub, String indent, String keyword, boolean withCondition) {
        stub.append(indent).append("    ").append(keyword);
        if (withCondition) {
            stub.append(" (/* TODO */)");
        }
        stub.append(" {").append(System.lineSeparator());
        stub.append(indent).append("        // TODO").append(System.lineSeparator());
        stub.append(indent).append("    }").append(System.lineSeparator());
    }

    private String transpileFields(String source) {
        String[] lines = source.split("\\R");
        StringBuilder out = new StringBuilder();
        for (String line : lines) {
            String trimmed = line.trim();
            if (!trimmed.endsWith(";") || trimmed.contains("(") || trimmed.startsWith("import") || trimmed.startsWith("return")) {
                out.append(line).append(System.lineSeparator());
                continue;
            }

            String indent = line.substring(0, line.indexOf(trimmed));
            String withoutSemi = trimmed.substring(0, trimmed.length() - 1).trim();
            int eq = withoutSemi.indexOf('=');
            if (eq != -1) {
                withoutSemi = withoutSemi.substring(0, eq).trim();
            }
            String[] tokens = withoutSemi.split("\\s+");
            if (tokens.length < 2) {
                out.append(line).append(System.lineSeparator());
                continue;
            }

            String name = tokens[tokens.length - 1];
            String type = tokens[tokens.length - 2];
            String[] modArray = java.util.Arrays.copyOf(tokens, tokens.length - 2);
            String modifiers = replaceFinalWithReadonly(modArray);
            String tsType = toTsType(type);
            out.append(indent);
            if (!modifiers.isBlank()) {
                out.append(modifiers).append(" ");
            }
            out.append(name).append(": ").append(tsType).append(";").append(System.lineSeparator());
        }
        return out.toString().trim();
    }

    private String replaceFinalWithReadonly(String[] mods) {
        for (int i = 0; i < mods.length; i++) {
            if (mods[i].equals("final")) {
                mods[i] = "readonly";
            }
        }
        return String.join(" ", mods).trim();
    }

    private String convertArrowFunctions(String source) {
        String[] lines = source.split("\\R");
        StringBuilder out = new StringBuilder();
        for (String line : lines) {
            if (line.contains("->")) {
                out.append(line.replace("->", "=>")).append(System.lineSeparator());
            } else {
                out.append(line).append(System.lineSeparator());
            }
        }
        return out.toString().trim();
    }

    private String stubArrowAssignments(String source) {
        String[] lines = source.split("\\R");
        StringBuilder out = new StringBuilder();
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.contains("=>") && trimmed.contains("{") && trimmed.contains("}")) {
                int open = line.indexOf('{');
                int close = line.lastIndexOf('}');
                String indent = line.substring(0, line.indexOf(trimmed));
                String header = line.substring(0, open + 1);
                String body = line.substring(open + 1, close).trim();
                if (body.contains("=") && body.contains(";")) {
                    out.append(header).append(System.lineSeparator());
                    for (String part : body.split(";")) {
                        if (part.trim().isEmpty()) continue;
                        out.append(indent).append("    // TODO").append(System.lineSeparator());
                    }
                    out.append(indent).append("};").append(System.lineSeparator());
                    continue;
                }
            }
            out.append(line).append(System.lineSeparator());
        }
        return out.toString().trim();
    }

    private String toTsParams(String params) {
        if (params.isBlank()) {
            return "";
        }
        java.util.List<String> out = new java.util.ArrayList<>();
        for (String p : params.split(",")) {
            String[] parts = p.trim().split("\\s+");
            if (parts.length == 0) {
                continue;
            }
            String name = parts[parts.length - 1];
            String type = parts.length > 1 ? parts[parts.length - 2] : "any";
            out.add(name + ": " + toTsType(type));
        }
        return String.join(", ", out);
    }

    private String toTsType(String javaType) {
        int genericStart = javaType.indexOf('<');
        int genericEnd = javaType.lastIndexOf('>');
        if (genericStart != -1 && genericEnd != -1 && genericEnd > genericStart) {
            return mapGeneric(javaType, genericStart, genericEnd);
        }

        if (javaType.endsWith("[]")) {
            String element = javaType.substring(0, javaType.length() - 2);
            return toTsType(element) + "[]";
        }

        return switch (javaType) {
            case "int", "long", "float", "double" -> "number";
            case "boolean", "Boolean" -> "boolean";
            case "char", "Character", "String" -> "string";
            case "void" -> "void";
            default -> "any";
        };
    }

    private String mapGeneric(String javaType, int start, int end) {
        String base = javaType.substring(0, start).trim();
        String params = javaType.substring(start + 1, end);
        java.util.List<String> mapped = new java.util.ArrayList<>();
        for (String p : params.split(",")) {
            mapped.add(toTsType(p.trim()));
        }
        return base + "<" + String.join(", ", mapped) + ">";
    }

    private String removePackage(String source) {
        String trimmed = source.trim();
        if (!trimmed.startsWith("package")) {
            return source;
        }
        int semicolon = source.indexOf(';');
        if (semicolon == -1) {
            return source;
        }
        return source.substring(semicolon + 1).trim();
    }
}
