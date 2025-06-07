package magma.app;

import java.util.Arrays;

class ImportHelper {
    static String extractPackage(String source) {
        var lines = source.split("\\R");
        for (var line : lines) {
            var trimmed = line.trim();
            if (trimmed.startsWith("package ") && trimmed.endsWith(";")) {
                return trimmed.substring(8, trimmed.length() - 1).trim();
            }
        }
        return "";
    }

    static String removePackage(String source) {
        var trimmed = source.trim();
        if (!trimmed.startsWith("package")) {
            return source;
        }
        var semicolon = source.indexOf(';');
        if (semicolon == -1) {
            return source;
        }
        return source.substring(semicolon + 1).trim();
    }

    static String translateImports(String source, String currentPkg) {
        var lines = source.split("\\R");
        var out = new StringBuilder();
        for (var i = 0; i < lines.length; i++) {
            var line = lines[i];
            var trimmed = line.trim();
            if (trimmed.startsWith("import ") && trimmed.endsWith(";") &&
                    !trimmed.contains("*") && !trimmed.startsWith("import static")) {
                var imp = trimmed.substring(7, trimmed.length() - 1).trim();
                out.append("import ").append(buildImport(imp, currentPkg)).append(";")
                   .append(System.lineSeparator());
                i = skipEmptyLines(lines, i + 1);
                continue;
            }
            out.append(line).append(System.lineSeparator());
        }
        return out.toString().trim();
    }

    private static int skipEmptyLines(String[] lines, int start) {
        var i = start;
        while (i < lines.length && lines[i].trim().isEmpty()) {
            i++;
        }
        return i - 1;
    }

    static String buildImport(String imp, String currentPkg) {
        var parts = imp.split("\\.");
        if (parts.length == 0) return imp;
        var className = parts[parts.length - 1];
        var importPkgParts = Arrays.copyOf(parts, parts.length - 1);
        var currentParts = currentPkg.isBlank() ? new String[0] : currentPkg.split("\\.");
        var shared = sharedPrefix(importPkgParts, currentParts);
        var path = relativePath(importPkgParts, currentParts, shared) + className;
        if (!path.startsWith("../")) {
            path = "./" + path;
        }
        return className + " from \"" + path + "\"";
    }

    private static int sharedPrefix(String[] a, String[] b) {
        var i = 0;
        while (i < a.length && i < b.length && a[i].equals(b[i])) {
            i++;
        }
        return i;
    }

    private static String relativePath(String[] impParts, String[] currentParts, int shared) {
        return upPath(currentParts.length - shared) +
                joinParts(impParts, shared);
    }

    private static String upPath(int count) {
        return "../".repeat(Math.max(0, count));
    }

    private static String joinParts(String[] parts, int start) {
        var out = new StringBuilder();
        for (var i = start; i < parts.length; i++) {
            out.append(parts[i]).append('/');
        }
        return out.toString();
    }
}
