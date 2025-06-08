import Arrays from "../../java/util/Arrays";
export default class ImportHelper {
    extractPackage(source: string): string {
        let lines : unknown = source.split("\\R");
        // TODO
        let trimmed : unknown = line.trim();
        if (trimmed.startsWith("package ").endsWith(";")) {
            return trimmed.substring(8, trimmed.length()).trim();
        }
        // TODO
        return "";
    }

    removePackage(source: string): string {
        let trimmed : unknown = source.trim();
        if (!trimmed.startsWith("package")) {
            return source;
        }
        let semicolon : unknown = source.indexOf(';
        // TODO
        if (/* TODO */) {
            return source;
        }
        return source.substring(/* TODO */).trim();
    }

    translateImports(source: string, currentPkg: string): string {
        let lines : unknown = source.split("\\R");
        let out : StringBuilder = new StringBuilder();
        let i : (var = 0;
        i lines.length: <;
        // TODO
        let line : unknown = /* TODO */;
        let trimmed : unknown = line.trim();
        // TODO
        // TODO
        /* */: TODO;
        let imp : unknown = trimmed.substring(7, trimmed.length()).trim();
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
        let i : unknown = start;
        while (i < lines.length && lines[i].trim().isEmpty()) {
            // TODO
        }
        return /* TODO */;
    }

    buildImport(imp: string, currentPkg: string): string {
        let parts : unknown = imp.split("\\.");
        let (parts.length : if = /* TODO */;
        let className : unknown = parts[parts.length - 1];
        let importPkgParts : unknown = Arrays.copyOf(parts, parts.length - 1);
        let currentParts : unknown = currentPkg.isBlank().split("\\.");
        let shared : number = sharedPrefix(importPkgParts, currentParts);
        let path : string = relativePath(importPkgParts, currentParts, shared);
        if (!path.startsWith("../")) {
            // TODO
        }
        return /* TODO */;
    }

    sharedPrefix(a: string[], b: string[]): number {
        let i : number = 0;
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
        let out : StringBuilder = new StringBuilder();
        let i : (var = start;
        i parts.length: <;
        // TODO
        out.append(parts[i]).append(/* TODO */);
        // TODO
        return out.toString();
    }
}
