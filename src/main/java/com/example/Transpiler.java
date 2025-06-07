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
        String[] lines = javaSource.split("\\R");
        StringBuilder ts = new StringBuilder();
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.startsWith("package ")) {
                continue; // skip package declarations entirely
            }

            int brace = line.indexOf('{');
            int classIdx = line.indexOf("class");
            if (brace != -1 && classIdx != -1 && classIdx < brace) {
                String afterClass = line.substring(classIdx, line.length());
                ts.append("export default ").append(afterClass)
                        .append(System.lineSeparator());
                continue;
            }

            ts.append(line).append(System.lineSeparator());
        }
        return ts.toString().trim();
    }
}
