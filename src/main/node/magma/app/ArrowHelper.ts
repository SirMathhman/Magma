export default class ArrowHelper {
    convertArrowFunctions(source: string): string {
        let lines: any = source.split("\\R");
        let out: any = new StringBuilder();
        // TODO
        if (line.contains("=>")) {
            // TODO
        }
        // TODO
        return out.toString().trim();
    }

    stubArrowAssignments(source: string): string {
        let lines: any = source.split("\\R");
        let out: any = new StringBuilder();
        // TODO
        let trimmed: any = line.trim();
        if (trimmed.contains("=>") && trimmed.contains("{") && trimmed.contains("}")) {
            // TODO
        }
        out.append(line).append(System.lineSeparator());
        // TODO
        return out.toString().trim();
    }

    expandArrowBody(line: string, trimmed: string): string {
        let open: any = line.indexOf(/* TODO */);
        let close: any = line.lastIndexOf(/* TODO */);
        let indent: any = line.substring(0, line.indexOf(trimmed));
        let header: any = line.substring(0, /* TODO */);
        let body: any = line.substring(/* TODO */, close).trim();
        let out: any = new StringBuilder();
        out.append(header).append(System.lineSeparator());
        // TODO
        // TODO
        let trimmedPart: any = part.trim();
        // TODO
        if (trimmedPart.contains("=")) {
            // TODO
        }
        // TODO
    }
        return out.toString();
    }
}
