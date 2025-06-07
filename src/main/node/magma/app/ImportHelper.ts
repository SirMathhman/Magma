export default class ImportHelper {
    extractPackage(source: string): string {
        let lines: string[] = source.split("\\R");
        // TODO
        let trimmed: string = line.trim();
        if (trimmed.startsWith("package ").endsWith(";")) {
            // TODO
        }
        // TODO
        return "";
    }

    removePackage(source: string): string {
        let trimmed: string = source.trim();
        if (!trimmed.startsWith("package")) {
            // TODO
        }
        let semicolon: number = source.indexOf(';
        // TODO
        if (/* TODO */) {
            // TODO
        }
        return source.substring(/* TODO */).trim();
    }

    translateImports(source: string, currentPkg: string): string {
        let lines: string[] = source.split("\\R");
        let out: any = new StringBuilder();
        let i: any = 0;
        i lines.length: any;
        // TODO
        let line: string = /* TODO */;
        let trimmed: string = line.trim();
        // TODO
        // TODO
        /* */: any;
        let imp: string = trimmed.substring(7, trimmed.length()).trim();
        out.append("import ").append(buildImport(imp, currentPkg));
        // TODO
        .append(System.lineSeparator());
        // TODO
        // TODO
        // TODO
        out.append(line).append(System.lineSeparator());
        // TODO
        return out.toString().trim();
    }

    skipEmptyLines(lines: string[], start: number): number {
        let i: number = start;
        while (i < lines.length && lines[i].trim().isEmpty()) {
            // TODO
        }
        return /* TODO */;
    }

    buildImport(imp: string, currentPkg: string): string {
        let parts: string[] = imp.split("\\.");
        let (parts.length: any = /* TODO */;
        let className: string = parts[parts.length - 1];
        let importPkgParts: string[] = java.util.Arrays.copyOf(parts, parts.length - 1);
        let currentParts: string[] = currentPkg.isBlank().split("\\.");
        let shared: number = sharedPrefix(importPkgParts, currentParts);
        let path: string = relativePath(importPkgParts, currentParts, shared);
        if (!path.startsWith("../")) {
            // TODO
        }
        return /* TODO */;
    }

    sharedPrefix(a: string[], b: string[]): number {
        let i: number = 0;
        while (i < a.length && i < b.length && a[i].equals(/* TODO */)) {
            // TODO
        }
        return i;
    }

    relativePath(impParts: string[], currentParts: string[], shared: number): string {
        let path: string = upPath(currentParts.length - shared);
        joinParts(impParts, shared);
        return path;
    }

    upPath(count: number): string {
        let out: any = new StringBuilder();
        out.append("../".repeat(Math.max(0, count)));
        return out.toString();
    }

    joinParts(parts: string[], start: number): string {
        let out: any = new StringBuilder();
        let i: any = start;
        i parts.length: any;
        // TODO
        out.append(parts[i]).append(/* TODO */);
        // TODO
        return out.toString();
    }
}
