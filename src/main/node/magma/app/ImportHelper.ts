export default class ImportHelper {
    extractPackage(source: string): string {
        let lines: string[] = source./* TODO */("\\R");
        // TODO
        let trimmed: string = line./* TODO */();
        if (trimmed./* TODO */("package ")./* TODO */(";")) {
            // TODO
        }
        // TODO
        return "";
    }

    removePackage(source: string): string {
        let trimmed: string = source./* TODO */();
        if (!trimmed./* TODO */("package")) {
            // TODO
        }
        let semicolon: number = source.indexOf(';
        // TODO
        if (/* TODO */) {
            // TODO
        }
        return source./* TODO */(/* TODO */)./* TODO */();
    }

    translateImports(source: string, currentPkg: string): string {
        let lines: string[] = source./* TODO */("\\R");
        let out: any = new StringBuilder();
        let i: any = 0;
        i lines.length: any;
        // TODO
        let line: string = /* TODO */;
        let trimmed: string = line./* TODO */();
        // TODO
        // TODO
        /* */: any;
        let imp: string = trimmed./* TODO */(7, /* TODO */)./* TODO */();
        /* TODO */(/* TODO */);
        // TODO
        /* TODO */(/* TODO */);
        // TODO
        // TODO
        // TODO
        /* TODO */(/* TODO */);
        // TODO
        return out./* TODO */()./* TODO */();
    }

    skipEmptyLines(lines: string[], start: number): number {
        let i: number = /* TODO */;
        while (i < lines.length && lines[i]./* TODO */()./* TODO */()) {
            // TODO
        }
        return /* TODO */;
    }

    buildImport(imp: string, currentPkg: string): string {
        let parts: string[] = imp./* TODO */("\\.");
        let (parts.length: any = /* TODO */;
        let className: string = parts[parts.length - 1];
        let importPkgParts: string[] = java.util.Arrays./* TODO */(/* TODO */, parts.length - 1);
        let currentParts: string[] = currentPkg./* TODO */()./* TODO */("\\.");
        let shared: number = /* TODO */(/* TODO */, /* TODO */);
        let path: string = /* TODO */(/* TODO */, /* TODO */, /* TODO */);
        if (!path./* TODO */("../")) {
            // TODO
        }
        return /* TODO */;
    }

    sharedPrefix(a: string[], b: string[]): number {
        let i: number = 0;
        while (i < a.length && i < b.length && a[i]./* TODO */(/* TODO */)) {
            // TODO
        }
        return /* TODO */;
    }

    relativePath(impParts: string[], currentParts: string[], shared: number): string {
        let path: any = new StringBuilder();
        /* TODO */(/* TODO */);
        /* TODO */(/* TODO */);
        return path./* TODO */();
    }

    upPath(count: number): string {
        let out: any = new StringBuilder();
        let i: any = 0;
        // TODO
        // TODO
        /* TODO */("../");
        // TODO
        return out./* TODO */();
    }

    joinParts(parts: string[], start: number): string {
        let out: any = new StringBuilder();
        let i: any = /* TODO */;
        i parts.length: any;
        // TODO
        /* TODO */(/* TODO */);
        // TODO
        return out./* TODO */();
    }
}
