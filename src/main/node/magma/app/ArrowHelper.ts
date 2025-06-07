export default class ArrowHelper {
    convertArrowFunctions(source: string): string {
        let lines: var = source.split("\\R");
        let out: var = new StringBuilder();
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
        let lines: var = source.split("\\R");
        let out: var = new StringBuilder();
        // TODO
        let trimmed: var = line.trim();
        if (trimmed.contains("=>") && trimmed.contains("{") && trimmed.contains("}")) {
            let open: var = line.indexOf(/* TODO */);
            let close: var = line.lastIndexOf(/* TODO */);
            let body: var = line.substring(/* TODO */, close).trim();
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
        let open: var = line.indexOf(/* TODO */);
        let close: var = line.lastIndexOf(/* TODO */);
        let indent: var = line.substring(0, line.indexOf(trimmed));
        let header: var = line.substring(0, /* TODO */);
        let body: var = line.substring(/* TODO */, close).trim();
        let out: var = new StringBuilder();
        out.append(header).append(System.lineSeparator());
        // TODO
        // TODO
        let trimmedPart: var = part.trim();
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
