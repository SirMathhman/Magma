package com.example;

/**
 * Very small prototype that converts a fragment of Java to TypeScript.
 */
public class Transpiler {

    /**
     * Transpiles the given Java source code to TypeScript.
     * Currently only removes the package declaration.
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
            ts.append(line).append(System.lineSeparator());
        }
        return ts.toString().trim();
    }
}
