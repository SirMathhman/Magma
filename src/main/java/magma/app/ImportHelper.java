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
        var imports = new StringBuilder();
        java.util.Set<String> names = new java.util.HashSet<>();
        var body = new StringBuilder();
        for (var i = 0; i < lines.length; i++) {
            var line = lines[i];
            var trimmed = line.trim();
            if (trimmed.startsWith("import ") && trimmed.endsWith(";") &&
                    !trimmed.contains("*") && !trimmed.startsWith("import static")) {
                var imp = trimmed.substring(7, trimmed.length() - 1).trim();
                var cls = imp.substring(imp.lastIndexOf('.') + 1);
                names.add(cls);
                imports.append("import ").append(buildImport(imp, currentPkg)).append(";")
                       .append(System.lineSeparator());
                i = skipEmptyLines(lines, i + 1);
                continue;
            }
            body.append(line).append(System.lineSeparator());
        }

        var bodyText = body.toString().trim();
        var self = extractClassName(bodyText);
        var refs = inferReferences(bodyText, names, self);
        var refIt = refs.iterator();
        while (refIt.hasNext()) {
            var r = refIt.next();
            imports.append("import ").append(r).append(" from \"./").append(r)
                   .append("\";").append(System.lineSeparator());
        }
        return imports.toString() + bodyText;
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

    private static String extractClassName(String source) {
        var lines = source.split("\\R");
        for (var line : lines) {
            var trimmed = line.trim();
            var cls = pickName(trimmed, "class ");
            if (!cls.isBlank()) return cls;
            cls = pickName(trimmed, "interface ");
            if (!cls.isBlank()) return cls;
            cls = pickName(trimmed, "enum ");
            if (!cls.isBlank()) return cls;
        }
        return "";
    }

    private static String pickName(String line, String keyword) {
        var idx = line.indexOf(keyword);
        if (idx == -1) return "";
        var after = line.substring(idx + keyword.length()).trim();
        var space = after.indexOf(' ');
        var brace = after.indexOf('{');
        var end = after.length();
        if (space != -1 && space < end) end = space;
        if (brace != -1 && brace < end) end = brace;
        return after.substring(0, end).trim();
    }

    private static java.util.Set<String> inferReferences(String source, java.util.Set<String> imports,
                                                         String self) {
        java.util.Set<String> refs = new java.util.HashSet<>();
        var word = new StringBuilder();
        var capture = false;
        for (var i = 0; i < source.length(); i++) {
            var c = source.charAt(i);
            if (!capture) {
                if (Character.isUpperCase(c) && (i == 0 || !Character.isLetterOrDigit(source.charAt(i - 1)) &&
                        source.charAt(i - 1) != '_')) {
                    word.setLength(0);
                    word.append(c);
                    capture = true;
                }
                continue;
            }
            if (Character.isLetterOrDigit(c) || c == '_') {
                word.append(c);
                continue;
            }
            if (c == '.') {
                var name = word.toString();
                if (!name.equals(self) && !imports.contains(name) && !isBuiltIn(name)) {
                    refs.add(name);
                }
            }
            capture = false;
        }
        return refs;
    }

    private static boolean isBuiltIn(String name) {
        return name.equals("System") || name.equals("String") || name.equals("Integer") ||
               name.equals("Long") || name.equals("Float") || name.equals("Double") ||
               name.equals("Short") || name.equals("Byte") || name.equals("Character") ||
               name.equals("Boolean") || name.equals("Object") || name.equals("Math");
    }
}
