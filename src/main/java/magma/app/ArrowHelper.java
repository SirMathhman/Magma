package magma.app;

class ArrowHelper {
    static String convertArrowFunctions(String source) {
        var lines = source.split("\\R");
        var out = new StringBuilder();
        for (var line : lines) {
            if (line.contains("->")) {
                out.append(line.replace("->", "=>")).append(System.lineSeparator());
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
