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
        let pkg: string = ImportHelper./* TODO */(/* TODO */);
        let withoutPackage: string = ImportHelper./* TODO */(/* TODO */);
        let withImports: string = ImportHelper./* TODO */(/* TODO */, /* TODO */);
        let lines: string[] = withImports./* TODO */("\\R");
        let ts: any = new StringBuilder();
        // TODO
        let classIdx: number = line./* TODO */("class");
        let enumIdx: number = line./* TODO */("enum");
        let ifaceIdx: number = line./* TODO */("interface");
        let brace: number = line./* TODO */(/* TODO */);
        let !: any = /* TODO */;
        let afterClass: string = line./* TODO */(/* TODO */);
        /* TODO */(/* TODO */);
        let !: any = /* TODO */;
        let afterIface: string = line./* TODO */(/* TODO */);
        /* TODO */(/* TODO */);
        let !: any = /* TODO */;
        let afterEnum: string = line./* TODO */(/* TODO */);
        /* TODO */(/* TODO */);
        // TODO
        /* TODO */(/* TODO */);
        // TODO
        // TODO
        let withMethods: string = MethodStubber./* TODO */(/* TODO */);
        let withFields: string = FieldTranspiler./* TODO */(/* TODO */);
        let withArrows: string = ArrowHelper./* TODO */(/* TODO */);
        return /* TODO */;
        // TODO
    }
