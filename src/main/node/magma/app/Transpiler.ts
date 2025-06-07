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
        let pkg: any = ImportHelper.extractPackage(javaSource);
        let withoutPackage: any = ImportHelper.removePackage(javaSource);
        let withImports: any = ImportHelper.translateImports(withoutPackage, pkg);
        let lines: any = withImports.split("\\R");
        let ts: any = new StringBuilder();
        // TODO
        let classIdx: any = line.indexOf("class");
        let enumIdx: any = line.indexOf("enum");
        let ifaceIdx: any = line.indexOf("interface");
        let brace: any = line.indexOf(/* TODO */);
        let !: any = /* TODO */;
        let afterClass: any = line.substring(classIdx);
        ts.append("export default ").append(afterClass).append(System.lineSeparator());
        let !: any = /* TODO */;
        let afterIface: any = line.substring(ifaceIdx);
        ts.append("export ").append(afterIface).append(System.lineSeparator());
        let !: any = /* TODO */;
        let afterEnum: any = line.substring(enumIdx);
        ts.append("export ").append(afterEnum).append(System.lineSeparator());
        // TODO
        ts.append(line).append(System.lineSeparator());
        // TODO
        // TODO
        let withMethods: any = MethodStubber.stubMethods(ts.toString().trim());
        let withFields: any = FieldTranspiler.transpileFields(withMethods);
        let withArrows: any = ArrowHelper.convertArrowFunctions(withFields);
        return ArrowHelper.stubArrowAssignments(withArrows);
        // TODO
    }
