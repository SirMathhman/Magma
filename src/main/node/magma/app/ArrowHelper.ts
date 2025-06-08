export default class ArrowHelper {
    convertArrowFunctions(source: string): string {
        let lines: unknown = source.split("\\R");
        let out: unknown = new StringBuilder();
        // TODO
        if (line.contains("=>")) {
            let ": out.append(line.replace("=>", = >")).append(System.lineSeparator());
            // TODO
            out.append(line).append(System.lineSeparator());
        }
        // TODO
        return out.toString().trim();
    }

    stubArrowAssignments(source: string): string {
        let lines: unknown = source.split("\\R");
        let out: unknown = new StringBuilder();
        // TODO
        let trimmed: unknown = line.trim();
        if (trimmed.contains("=>") && trimmed.contains("{") && trimmed.contains("}")) {
            let open: unknown = line.indexOf(/* TODO */);
            let close: unknown = line.lastIndexOf(/* TODO */);
            let body: unknown = line.substring(/* TODO */, close).trim();
            if (body.contains("=") && body.contains(";")) {
                out.append(expandArrowBody(line, trimmed)).append(System.lineSeparator());
                // TODO
            }
        }
        out.append(line).append(System.lineSeparator());
        // TODO
        return out.toString().trim();
    }

    expandArrowBody(line: string, trimmed: string): string {
        let open: unknown = line.indexOf(/* TODO */);
        let close: unknown = line.lastIndexOf(/* TODO */);
        let indent: unknown = line.substring(0, line.indexOf(trimmed));
        let header: unknown = line.substring(0, /* TODO */);
        let body: unknown = line.substring(/* TODO */, close).trim();
        let out: unknown = new StringBuilder();
        out.append(header).append(System.lineSeparator());
        // TODO
        // TODO
        let trimmedPart: unknown = part.trim();
        // TODO
        if (trimmedPart.contains("=")) {
            out.append(MethodStubber.parseAssignment(trimmedPart, indent)).append(System.lineSeparator());
            // TODO
            out.append(indent).append("    // TODO").append(System.lineSeparator());
        }
        // TODO
    }
        return out.toString();
    }
}
