export default class ArrowHelper {
    convertArrowFunctions(source: string): string {
        let lines: unknown = source.split("\\R");
        let out: unknown = new StringBuilder();
        // TODO
            if (line.contains("=>")) {
                out.append(line.replace("=>", "=>")).append(System.lineSeparator());
            } else {
                out.append(line).append(System.lineSeparator());
            }
        }
        return out.toString().trim();
    }

    static String stubArrowAssignments(String source) {
        var lines = source.split("\\R");
        var out = new StringBuilder();
        for (var line : lines) {
            var trimmed = line.trim();
            if (trimmed.contains("=>") && trimmed.contains("{") && trimmed.contains("}")) {
                var open = line.indexOf('{');
                var close = line.lastIndexOf('}');
                var body = line.substring(open + 1, close).trim();
                if (body.contains("=") && body.contains(";")) {
                    out.append(expandArrowBody(line, trimmed)).append(System.lineSeparator());
                    continue;
                }
            }
            out.append(line).append(System.lineSeparator());
        }
        return out.toString().trim();
    }

    private static String expandArrowBody(String line, String trimmed) {
        var open = line.indexOf('{');
        var close = line.lastIndexOf('}');
        var indent = line.substring(0, line.indexOf(trimmed));
        var header = line.substring(0, open + 1);
        var body = line.substring(open + 1, close).trim();
        var out = new StringBuilder();
        out.append(header).append(System.lineSeparator());
        for (var part : body.split(";")) {
            var trimmedPart = part.trim();
            if (trimmedPart.isEmpty()) continue;
            if (trimmedPart.contains("=")) {
                out.append(MethodStubber.parseAssignment(trimmedPart, indent)).append(System.lineSeparator());
            } else {
                out.append(indent).append("    // TODO").append(System.lineSeparator());
            }
        }
        out.append(indent).append("};");
        return out.toString();
    }
}
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
