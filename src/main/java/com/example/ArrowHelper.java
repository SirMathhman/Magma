package com.example;

class ArrowHelper {
    static String convertArrowFunctions(String source) {
        String[] lines = source.split("\\R");
        StringBuilder out = new StringBuilder();
        for (String line : lines) {
            if (line.contains("->")) {
                out.append(line.replace("->", "=>")).append(System.lineSeparator());
            } else {
                out.append(line).append(System.lineSeparator());
            }
        }
        return out.toString().trim();
    }

    static String stubArrowAssignments(String source) {
        String[] lines = source.split("\\R");
        StringBuilder out = new StringBuilder();
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.contains("=>") && trimmed.contains("{") && trimmed.contains("}")) {
                int open = line.indexOf('{');
                int close = line.lastIndexOf('}');
                String body = line.substring(open + 1, close).trim();
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
        int open = line.indexOf('{');
        int close = line.lastIndexOf('}');
        String indent = line.substring(0, line.indexOf(trimmed));
        String header = line.substring(0, open + 1);
        String body = line.substring(open + 1, close).trim();
        StringBuilder out = new StringBuilder();
        out.append(header).append(System.lineSeparator());
        for (String part : body.split(";")) {
            String trimmedPart = part.trim();
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
