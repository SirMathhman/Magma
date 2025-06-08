package magma.app;

class ArrowHelper {
    static String convertArrowFunctions(String source) {
        var lines = source.split("\\R");
        var out = new StringBuilder();
        for (var line : lines) {
            if (line.contains("->")) {
                var replaced = line.replace("->", "=>");
                out.append(mapTypedParams(replaced)).append(System.lineSeparator());
            } else {
                out.append(line).append(System.lineSeparator());
            }
        }
        return out.toString().trim();
    }

    private static String mapTypedParams(String line) {
        var arrow = line.indexOf("=>");
        if (arrow == -1) return line;
        var close = line.lastIndexOf(')', arrow);
        var open = line.lastIndexOf('(', close);
        if (open == -1 || close == -1 || close <= open) return line;
        var inside = line.substring(open + 1, close).trim();
        if (!inside.contains(" ")) return line;
        var parts = inside.split(",");
        var mapped = new StringBuilder();
        for (var i = 0; i < parts.length; i++) {
            var p = parts[i].trim();
            var tokens = p.split("\\s+");
            if (tokens.length >= 2) {
                var name = tokens[tokens.length - 1];
                var type = tokens[tokens.length - 2];
                mapped.append(name)
                    .append(" : ")
                    .append(TypeMapper.toTsType(type));
            } else {
                mapped.append(p);
            }
            if (i < parts.length - 1) mapped.append(", ");
        }
        return line.substring(0, open + 1) + mapped + line.substring(close);
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
        java.util.Map<String, String> vars = new java.util.HashMap<>();
        out.append(header).append(System.lineSeparator());
        for (var part : body.split(";")) {
            var trimmedPart = part.trim();
            if (trimmedPart.isEmpty()) continue;
            if (trimmedPart.contains("=")) {
                out.append(StatementParser.parseAssignment(trimmedPart, indent, vars, java.util.Collections.emptyMap()))
                    .append(System.lineSeparator());
            } else {
                out.append(indent).append("    // TODO").append(System.lineSeparator());
            }
        }
        out.append(indent).append("};");
        return out.toString();
    }
}
