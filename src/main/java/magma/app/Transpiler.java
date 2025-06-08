package magma.app;

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
        if (javaSource.contains("@Actual")) {
            return "";
        }
        var pkg = ImportHelper.extractPackage(javaSource);
        var withoutPackage = ImportHelper.removePackage(javaSource);
        var withImports = ImportHelper.translateImports(withoutPackage, pkg);

        var lines = withImports.split("\\R");
        var ts = new StringBuilder();
        for (var line : lines) {
            var classIdx = line.indexOf("class");
            var enumIdx = line.indexOf("enum");
            var ifaceIdx = line.indexOf("interface");
            var brace = line.indexOf('{');
            if (classIdx != -1 && brace != -1 && classIdx < brace) {
                var afterClass = line.substring(classIdx);
                ts.append("export default ").append(afterClass).append(System.lineSeparator());
            } else if (ifaceIdx != -1 && brace != -1 && ifaceIdx < brace) {
                var afterIface = line.substring(ifaceIdx);
                ts.append("export ").append(afterIface).append(System.lineSeparator());
            } else if (enumIdx != -1 && brace != -1 && enumIdx < brace) {
                var afterEnum = line.substring(enumIdx);
                ts.append("export ").append(afterEnum).append(System.lineSeparator());
            } else {
                ts.append(line).append(System.lineSeparator());
            }
        }

        var withMethods = MethodStubber.stubMethods(ts.toString().trim());
        var withFields = FieldTranspiler.transpileFields(withMethods);
        var withArrows = ArrowHelper.convertArrowFunctions(withFields);
        return ArrowHelper.stubArrowAssignments(withArrows);
    }
}
