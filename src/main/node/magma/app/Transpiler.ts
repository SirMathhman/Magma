/**
 * Very small prototype that converts a fragment of Java to TypeScript.
 */
export default class Transpiler {

    /**
     * Transpiles the given Java source code to TypeScript.
     * Currently removes the package declaration and rewrites simple class
     * definitions. Modifiers before the {@code class} keyword are replaced with
     * {@code export default}.
     *
     * @param javaSource the Java source text
     * @return transpiled TypeScript
     */
    toTypeScript(javaSource: string): string {
        let pkg: var = ImportHelper.extractPackage(javaSource);
        let withoutPackage: var = ImportHelper.removePackage(javaSource);
        let withImports: var = ImportHelper.translateImports(withoutPackage, pkg);
        let lines: var = withImports.split("\\R");
        let ts: var = new StringBuilder();
        // TODO
        let classIdx: var = line.indexOf("class");
        let enumIdx: var = line.indexOf("enum");
        let ifaceIdx: var = line.indexOf("interface");
        let brace: var = line.indexOf(/* TODO */);
        let !: classIdx = /* TODO */;
        let afterClass: var = line.substring(classIdx);
        ts.append("export default ").append(afterClass).append(System.lineSeparator());
        let !: (ifaceIdx = /* TODO */;
        let afterIface: var = line.substring(ifaceIdx);
        ts.append("export ").append(afterIface).append(System.lineSeparator());
        let !: enumIdx = /* TODO */;
        let afterEnum: var = line.substring(enumIdx);
        ts.append("export ").append(afterEnum).append(System.lineSeparator());
        // TODO
        ts.append(line).append(System.lineSeparator());
        // TODO
        // TODO
        let withMethods: var = MethodStubber.stubMethods(ts.toString().trim());
        let withFields: var = FieldTranspiler.transpileFields(withMethods);
        let withArrows: var = ArrowHelper.convertArrowFunctions(withFields);
        return ArrowHelper.stubArrowAssignments(withArrows);
        // TODO
    }
