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
        let lines: string[] = /* TODO */(/* TODO */);
        let ts: any = new StringBuilder();
        // TODO
        let classIdx: number = /* TODO */(/* TODO */);
        let enumIdx: number = /* TODO */(/* TODO */);
        let ifaceIdx: number = /* TODO */(/* TODO */);
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
