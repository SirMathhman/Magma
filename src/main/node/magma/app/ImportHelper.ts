import Arrays from "../../java/util/Arrays";
export default class ImportHelper {
    extractPackage(source: string): string {
        let lines: any = source.split("\\R");
        // TODO
        let trimmed: any = line.trim();
        if (trimmed.startsWith("package ").endsWith(";")) {
            // TODO
        }
        // TODO
        return "";
    }

    removePackage(source: string): string {
        let trimmed: any = source.trim();
        if (!trimmed.startsWith("package")) {
            // TODO
        }
        let semicolon: any = source.indexOf(';
        // TODO
        if (/* TODO */) {
            // TODO
        }
        return source.substring(/* TODO */).trim();
    }

    translateImports(source: string, currentPkg: string): string {
        let lines: any = source.split("\\R");
        let out: any = new StringBuilder();
        let i: any = 0;
        i lines.length: any;
        // TODO
        let line: any = /* TODO */;
        let trimmed: any = line.trim();
        // TODO
        // TODO
        /* */: any;
        let imp: any = trimmed.substring(7, trimmed.length()).trim();
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
        let i: any = start;
        while (i < lines.length && lines[i].trim().isEmpty()) {
            // TODO
        }
        return /* TODO */;
    }

    buildImport(imp: string, currentPkg: string): string {
        let parts: any = imp.split("\\.");
        let (parts.length: any = /* TODO */;
        let className: any = parts[parts.length - 1];
        let importPkgParts: any = Arrays.copyOf(parts, parts.length - 1);
        let currentParts: any = currentPkg.isBlank().split("\\.");
        let shared: any = sharedPrefix(importPkgParts, currentParts);
        let path: any = relativePath(importPkgParts, currentParts, shared);
        if (!path.startsWith("../")) {
            // TODO
        }
        return /* TODO */;
    }

    sharedPrefix(a: string[], b: string[]): number {
        let i: any = 0;
        while (i < a.length && i < b.length && a[i].equals(/* TODO */)) {
            // TODO
        }
        return i;
    }

    relativePath(impParts: string[], currentParts: string[], shared: number): string {
        return upPath(currentParts.length - shared);
        joinParts(impParts, shared);
    }

    upPath(count: number): string {
        return "../".repeat(Math.max(0, count));
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
