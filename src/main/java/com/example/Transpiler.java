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
        String pkg = ImportHelper.extractPackage(javaSource);
        String withoutPackage = ImportHelper.removePackage(javaSource);
        String withImports = ImportHelper.translateImports(withoutPackage, pkg);

        String[] lines = withImports.split("\\R");
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

        String withMethods = MethodStubber.stubMethods(ts.toString().trim());
        String withFields = FieldTranspiler.transpileFields(withMethods);
        String withArrows = ArrowHelper.convertArrowFunctions(withFields);
        return ArrowHelper.stubArrowAssignments(withArrows);
    }
}
