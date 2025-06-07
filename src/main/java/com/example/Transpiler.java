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
        String withoutPackage = javaSource.replaceFirst("(?s)^\\s*package\\s+.*?;\\s*", "");

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
        String pattern = "(?ms)^([ \t]*)(?:public|private|protected|static|final|abstract|synchronized|native|strictfp\\s+)*" +
                "([a-zA-Z_][a-zA-Z0-9_<>\\[\\]]*)\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*\\(([^)]*)\\)\\s*\\{[^}]*\\}";
        java.util.regex.Matcher m = java.util.regex.Pattern.compile(pattern).matcher(source);
        StringBuffer out = new StringBuffer();
        while (m.find()) {
            String indent = m.group(1);
            String returnType = m.group(2);
            String name = m.group(3);
            String params = m.group(4).trim();
            String tsParams = toTsParams(params);
            String tsReturn = toTsType(returnType);
            String replacement = indent + name + "(" + tsParams + ")" +
                    (tsReturn.isBlank() ? "" : ": " + tsReturn) + " {" +
                    System.lineSeparator() +
                    indent + "    // TODO" + System.lineSeparator() +
                    indent + "}";
            m.appendReplacement(out, java.util.regex.Matcher.quoteReplacement(replacement));
        }
        m.appendTail(out);
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
        return switch (javaType) {
            case "int", "long", "float", "double" -> "number";
            case "boolean" -> "boolean";
            case "char", "String" -> "string";
            case "void" -> "void";
            default -> "any";
        };
    }
}
