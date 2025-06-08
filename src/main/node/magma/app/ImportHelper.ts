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
        let imports : StringBuilder = new StringBuilder();
        let names : java.util.Set<string> = new java.util.HashSet<string>();
        let body : StringBuilder = new StringBuilder();
        let i : (var = 0;
        i lines.length: <;
        // TODO
        let line : unknown = /* TODO */;
        let trimmed : unknown = line.trim();
        // TODO
        // TODO
        /* */: TODO;
        let imp : unknown = trimmed.substring(7, trimmed.length()).trim();
        let cls : unknown = imp.substring(imp.lastIndexOf('.'));
        names.add(cls);
        imports.append("import ").append(buildImport(imp, currentPkg));
        // TODO
        .append(System.lineSeparator());
        // TODO
        // TODO
        // TODO
        body.append(line).append(System.lineSeparator());
        // TODO
        let bodyText : unknown = body.toString().trim();
        let self : string = extractClassName(bodyText);
        let refs : unknown = inferReferences(bodyText, names, self);
        let refIt : unknown = refs.iterator();
        while (refIt.hasNext()) {
            let r : unknown = refIt.next();
            imports.append("import ").append(r).append(" from \"./").append(r);
            // TODO
            ").append(System.lineSeparator());
        }
        return imports.toString();
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

    extractClassName(source: string): string {
        let lines : unknown = source.split("\\R");
        // TODO
        let trimmed : unknown = line.trim();
        let cls : string = pickName(trimmed, "class ");
        // TODO
        // TODO
        // TODO
        // TODO
        // TODO
        // TODO
        return "";
    }

    pickName(line: string, keyword: string): string {
        let idx : unknown = line.indexOf(keyword);
        let (idx : if = /* TODO */;
        let after : unknown = line.substring(idx + keyword.length()).trim();
        let space : unknown = after.indexOf(/* TODO */);
        let brace : unknown = after.indexOf(/* TODO */);
        let end : unknown = after.length();
        let ! : (space = /* TODO */;
        let ! : (brace = /* TODO */;
        return after.substring(0, end).trim();
        // TODO
        // TODO
        // TODO
        let refs : java.util.Set<string> = new java.util.HashSet<string>();
        let word : StringBuilder = new StringBuilder();
        let capture : boolean = /* TODO */;
        let i : (var = 0;
        i < source.length();
        // TODO
        let c : unknown = source.charAt(i);
        if (!capture) {
            let (i : && = = 0 || !Character.isLetterOrDigit(source.charAt(/* TODO */));
            let ! : 1) = /* TODO */;
            word.setLength(0);
            word.append(c);
            // TODO
            // TODO
            // TODO
        }
        if (Character.isLetterOrDigit(c)) {
            word.append(c);
            // TODO
        }
        if (/* TODO */) {
            let name : unknown = word.toString();
            if (!name.equals(self).contains(name) && !isBuiltIn(name)) {
                refs.add(name);
            }
        }
        // TODO
        // TODO
        return refs;
        // TODO
        private static boolean isBuiltIn(/* TODO */);
        return name.equals("System").equals("String").equals("Integer");
        name.equals("Long") || name.equals("Float") || name.equals("Double");
        name.equals("Short") || name.equals("Byte") || name.equals("Character");
        name.equals("Boolean") || name.equals("Object") || name.equals("Math");
        // TODO
    }
