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
            int brace = line.indexOf('{');
            if (classIdx != -1 && brace != -1 && classIdx < brace) {
                String afterClass = line.substring(classIdx);
                ts.append("export default ").append(afterClass).append(System.lineSeparator());
            } else {
                ts.append(line).append(System.lineSeparator());
            }
        }

        return stubMethods(ts.toString().trim());
    }

    private String stubMethods(String source) {
        String[] lines = source.split("\\R");
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            String trimmed = line.trim();
            if (trimmed.endsWith("{") && trimmed.contains("(") && !trimmed.startsWith("export")) {
                String indent = line.substring(0, line.indexOf(trimmed));
                String beforeBrace = trimmed.substring(0, trimmed.length() - 1).trim();
                int parenStart = beforeBrace.indexOf('(');
                int parenEnd = beforeBrace.lastIndexOf(')');
                if (parenStart == -1 || parenEnd == -1) {
                    out.append(line).append(System.lineSeparator());
                    continue;
                }
                String signatureStart = beforeBrace.substring(0, parenStart).trim();
                String params = beforeBrace.substring(parenStart + 1, parenEnd).trim();
                String[] sigTokens = signatureStart.split("\\s+");
                if (sigTokens.length == 0) {
                    out.append(line).append(System.lineSeparator());
                    continue;
                }
                String name = sigTokens[sigTokens.length - 1];
                String returnType = sigTokens.length > 1 ? sigTokens[sigTokens.length - 2] : "void";

                String tsParams = toTsParams(params);
                String tsReturn = toTsType(returnType);
                out.append(indent).append(name).append("(").append(tsParams).append(")");
                if (!tsReturn.isBlank()) {
                    out.append(": ").append(tsReturn);
                }
                out.append(" {").append(System.lineSeparator());
                out.append(indent).append("    // TODO").append(System.lineSeparator());
                out.append(indent).append("}").append(System.lineSeparator());

                int braceDepth = 1;
                while (i + 1 < lines.length && braceDepth > 0) {
                    i++;
                    String bodyLine = lines[i];
                    for (char c : bodyLine.toCharArray()) {
                        if (c == '{') braceDepth++;
                        else if (c == '}') braceDepth--;
                    }
                }
            } else {
                out.append(line).append(System.lineSeparator());
            }
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
            String base = javaType.substring(0, genericStart).trim();
            String params = javaType.substring(genericStart + 1, genericEnd);
            java.util.List<String> mapped = new java.util.ArrayList<>();
            for (String p : params.split(",")) {
                mapped.add(toTsType(p.trim()));
            }
            return base + "<" + String.join(", ", mapped) + ">";
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
