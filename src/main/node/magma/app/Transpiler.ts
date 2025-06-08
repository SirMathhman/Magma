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
        let pkg : unknown = ImportHelper.extractPackage(javaSource);
        let withoutPackage : unknown = ImportHelper.removePackage(javaSource);
        let withImports : unknown = ImportHelper.translateImports(withoutPackage, pkg);
        let lines : unknown = withImports.split("\\R");
        let ts : StringBuilder = new StringBuilder();
        // TODO
        let classIdx : unknown = line.indexOf("class");
        let enumIdx : unknown = line.indexOf("enum");
        let ifaceIdx : unknown = line.indexOf("interface");
        let brace : unknown = line.indexOf(/* TODO */);
        let ! : classIdx = /* TODO */;
        let afterClass : unknown = line.substring(classIdx);
        ts.append("export default ").append(afterClass).append(System.lineSeparator());
        let ! : (ifaceIdx = /* TODO */;
        let afterIface : unknown = line.substring(ifaceIdx);
        ts.append("export ").append(afterIface).append(System.lineSeparator());
        let ! : enumIdx = /* TODO */;
        let afterEnum : unknown = line.substring(enumIdx);
        ts.append("export ").append(afterEnum).append(System.lineSeparator());
        // TODO
        ts.append(line).append(System.lineSeparator());
        // TODO
        // TODO
        let withMethods : unknown = MethodStubber.stubMethods(ts.toString().trim());
        let withFields : unknown = FieldTranspiler.transpileFields(withMethods);
        let withArrows : unknown = ArrowHelper.convertArrowFunctions(withFields);
        return ArrowHelper.stubArrowAssignments(withArrows);
        // TODO
    }
