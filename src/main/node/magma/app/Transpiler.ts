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
        let pkg: string = ImportHelper.extractPackage(javaSource);
        let withoutPackage: string = ImportHelper.removePackage(javaSource);
        let withImports: string = ImportHelper.translateImports(withoutPackage, pkg);
        let lines: string[] = withImports.split("\\R");
        let ts: any = new StringBuilder();
        // TODO
        let classIdx: number = line.indexOf("class");
        let enumIdx: number = line.indexOf("enum");
        let ifaceIdx: number = line.indexOf("interface");
        let brace: number = line.indexOf(/* TODO */);
        let !: any = /* TODO */;
        let afterClass: string = line.substring(classIdx);
        ts.append("export default ").append(afterClass).append(System.lineSeparator());
        let !: any = /* TODO */;
        let afterIface: string = line.substring(ifaceIdx);
        ts.append("export ").append(afterIface).append(System.lineSeparator());
        let !: any = /* TODO */;
        let afterEnum: string = line.substring(enumIdx);
        ts.append("export ").append(afterEnum).append(System.lineSeparator());
        // TODO
        ts.append(line).append(System.lineSeparator());
        // TODO
        // TODO
        let withMethods: string = MethodStubber.stubMethods(ts.toString().trim());
        let withFields: string = FieldTranspiler.transpileFields(withMethods);
        let withArrows: string = ArrowHelper.convertArrowFunctions(withFields);
        return ArrowHelper.stubArrowAssignments(withArrows);
        // TODO
    }
