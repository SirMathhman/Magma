package magma.app;

class ImportHelper {
    static String extractPackage(String source) {
        String[] lines = source.split("\\R");
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.startsWith("package ") && trimmed.endsWith(";")) {
                return trimmed.substring(8, trimmed.length() - 1).trim();
            }
        }
        return "";
    }

    static String removePackage(String source) {
        String trimmed = source.trim();
        if (!trimmed.startsWith("package")) {
            return source;
        }
        int semicolon = source.indexOf(';');
        if (semicolon == -1) {
            return source;
        }
        return source.substring(semicolon + 1).trim();
    }

    static String translateImports(String source, String currentPkg) {
        String[] lines = source.split("\\R");
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            String trimmed = line.trim();
            if (trimmed.startsWith("import ") && trimmed.endsWith(";") &&
                    !trimmed.contains("*") && !trimmed.startsWith("import static")) {
                String imp = trimmed.substring(7, trimmed.length() - 1).trim();
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
        int i = start;
        while (i < lines.length && lines[i].trim().isEmpty()) {
            i++;
        }
        return i - 1;
    }

    static String buildImport(String imp, String currentPkg) {
        String[] parts = imp.split("\\.");
        if (parts.length == 0) return imp;
        String className = parts[parts.length - 1];
        String[] importPkgParts = java.util.Arrays.copyOf(parts, parts.length - 1);
        String[] currentParts = currentPkg.isBlank() ? new String[0] : currentPkg.split("\\.");
        int shared = sharedPrefix(importPkgParts, currentParts);
        String path = relativePath(importPkgParts, currentParts, shared) + className;
        if (!path.startsWith("../")) {
            path = "./" + path;
        }
        return className + " from \"" + path + "\"";
    }

    private static int sharedPrefix(String[] a, String[] b) {
        int i = 0;
        while (i < a.length && i < b.length && a[i].equals(b[i])) {
            i++;
        }
        return i;
    }

    private static String relativePath(String[] impParts, String[] currentParts, int shared) {
        String path = upPath(currentParts.length - shared) +
                joinParts(impParts, shared);
        return path;
    }

    private static String upPath(int count) {
        StringBuilder out = new StringBuilder();
        out.append("../".repeat(Math.max(0, count)));
        return out.toString();
    }

    private static String joinParts(String[] parts, int start) {
        StringBuilder out = new StringBuilder();
        for (int i = start; i < parts.length; i++) {
            out.append(parts[i]).append('/');
        }
        return out.toString();
    }
}
