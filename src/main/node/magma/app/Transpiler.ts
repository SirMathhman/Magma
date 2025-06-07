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
        let pkg: string = /* TODO */(/* TODO */);
        let withoutPackage: string = /* TODO */(/* TODO */);
        let withImports: string = /* TODO */(/* TODO */, /* TODO */);
        let lines: string[] = /* TODO */("\\R");
        let ts: any = new StringBuilder();
        // TODO
        let classIdx: number = /* TODO */("class");
        let enumIdx: number = /* TODO */("enum");
        let ifaceIdx: number = /* TODO */("interface");
        let brace: number = /* TODO */(/* TODO */);
        let !: any = /* TODO */;
        let afterClass: string = /* TODO */(/* TODO */);
        /* TODO */(/* TODO */);
        let !: any = /* TODO */;
        let afterIface: string = /* TODO */(/* TODO */);
        /* TODO */(/* TODO */);
        let !: any = /* TODO */;
        let afterEnum: string = /* TODO */(/* TODO */);
        /* TODO */(/* TODO */);
        // TODO
        /* TODO */(/* TODO */);
        // TODO
        // TODO
        let withMethods: string = /* TODO */(/* TODO */);
        let withFields: string = /* TODO */(/* TODO */);
        let withArrows: string = /* TODO */(/* TODO */);
        return /* TODO */;
        // TODO
    }
