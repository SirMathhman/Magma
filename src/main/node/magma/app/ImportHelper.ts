export default class ImportHelper {
    extractPackage(source: string): string {
        let lines: string[] = /* TODO */(/* TODO */);
        // TODO
        let trimmed: string = /* TODO */();
        if (/* TODO */) {
            // TODO
        }
        // TODO
        return /* TODO */;
    }

    removePackage(source: string): string {
        let trimmed: string = /* TODO */();
        if (/* TODO */) {
            // TODO
        }
        let semicolon: number = /* TODO */;
        // TODO
        if (/* TODO */) {
            // TODO
        }
        return /* TODO */;
    }

    translateImports(source: string, currentPkg: string): string {
        let lines: string[] = /* TODO */(/* TODO */);
        let out: any = new /* TODO */();
        let i: any = /* TODO */;
        // TODO
        // TODO
        let line: string = /* TODO */;
        let trimmed: string = /* TODO */();
        // TODO
        // TODO
        /* TODO */(/* TODO */);
        let imp: string = /* TODO */(/* TODO */, /* TODO */);
        /* TODO */(/* TODO */, /* TODO */);
        // TODO
        /* TODO */(/* TODO */);
        // TODO
        // TODO
        // TODO
        /* TODO */(/* TODO */);
        // TODO
        return /* TODO */;
    }

    skipEmptyLines(lines: string[], start: number): number {
        let i: number = /* TODO */;
        while (/* TODO */) {
            // TODO
        }
        return /* TODO */;
    }

    buildImport(imp: string, currentPkg: string): string {
        let parts: string[] = /* TODO */(/* TODO */);
        let (parts.length: any = /* TODO */;
        let className: string = /* TODO */;
        let importPkgParts: string[] = /* TODO */(/* TODO */, /* TODO */);
        let currentParts: string[] = /* TODO */(/* TODO */);
        let shared: number = /* TODO */(/* TODO */, /* TODO */);
        let path: string = /* TODO */(/* TODO */, /* TODO */, /* TODO */);
        if (/* TODO */) {
            // TODO
        }
        return /* TODO */;
    }

    sharedPrefix(a: string[], b: string[]): number {
        let i: number = /* TODO */;
        while (/* TODO */) {
            // TODO
        }
        return /* TODO */;
    }

    relativePath(impParts: string[], currentParts: string[], shared: number): string {
        let path: any = new /* TODO */();
        /* TODO */(/* TODO */);
        /* TODO */(/* TODO */, /* TODO */);
        return /* TODO */;
    }

    upPath(count: number): string {
        let out: any = new /* TODO */();
        let i: any = /* TODO */;
        // TODO
        // TODO
        /* TODO */(/* TODO */);
        // TODO
        return /* TODO */;
    }

    joinParts(parts: string[], start: number): string {
        let out: any = new /* TODO */();
        let i: any = /* TODO */;
        // TODO
        // TODO
        /* TODO */(/* TODO */);
        // TODO
        return /* TODO */;
    }
}
