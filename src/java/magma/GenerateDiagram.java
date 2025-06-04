package magma;

import magma.result.Err;
import magma.result.Ok;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class GenerateDiagram {
    // Helper methods split to comply with SRP (Single Responsibility Principle)

    /**
     * Generates a PlantUML diagram and writes it to {@code output}. Instead of
     * throwing an exception, any I/O error is returned wrapped in an
     * {@link Optional}.
     */
    public static Optional<IOException> writeDiagram(Path output) {
        Path src = Path.of("src/java/magma");
        var sources = Sources.read(src);
        if (sources.isErr()) {
            return Optional.of(((Err<List<String>, IOException>) sources).error());
        }
        List<String> allSources = ((Ok<List<String>, IOException>) sources).value();
        Sources analysis = new Sources(allSources);
        List<String> classes = analysis.findClasses();

        var implementations = analysis.findImplementations();
        var sourceMap = analysis.mapSourcesByClass();

        StringBuilder content = new StringBuilder("@startuml\n");
        content.append(classesSection(classes, sourceMap));
        content.append(analysis.formatRelations(classes, implementations));
        content.append("@enduml\n");
        try {
            Files.writeString(output, content.toString());
            return Optional.empty();
        } catch (IOException e) {
            return Optional.of(e);
        }
    }

    private static String classesSection(List<String> classes,
                                         java.util.Map<String, String> sourceMap) {
        StringBuilder builder = new StringBuilder();
        for (String name : classes) {
            String source = sourceMap.getOrDefault(name, "");
            String type = classType(name, source);
            builder.append(type).append(' ').append(name).append("\n");
        }
        return builder.toString();
    }

    private static String classType(String name, String source) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
                "(class|interface|record)\\s+" + java.util.regex.Pattern.quote(name) + "\\b");
        java.util.regex.Matcher matcher = pattern.matcher(source);
        if (matcher.find()) {
            String kind = matcher.group(1);
            if ("interface".equals(kind)) {
                return "interface";
            }
        }
        return "class";
    }

    /**
     * Creates a .ts file for every .java file under {@code javaRoot}. The
     * generated files mirror the directory structure under {@code tsRoot}.
     * Existing files are overwritten so that imports stay in sync with the
     * corresponding Java sources.
     */
    public static Optional<IOException> writeTypeScriptStubs(Path javaRoot, Path tsRoot) {
        List<Path> files;
        try (Stream<Path> stream = Files.walk(javaRoot)) {
            files = stream.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".java"))
                    .toList();
        } catch (IOException e) {
            return Optional.of(e);
        }
        for (Path file : files) {
            Path relative = javaRoot.relativize(file);
            Path tsFile = tsRoot.resolve(relative.toString().replaceFirst("\\.java$", ".ts"));
            try {
                Files.createDirectories(tsFile.getParent());
                var imports = readImports(file);
                var declarations = readDeclarations(file);
                var methods = readMethods(file);
                String content = stubContent(relative, tsFile.getParent(), tsRoot, imports, declarations, methods);
                Files.writeString(tsFile, content);
            } catch (IOException e) {
                return Optional.of(e);
            }
        }
        return Optional.empty();
    }

    private static java.util.List<String> readImports(Path file) throws IOException {
        String source = Files.readString(file);
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("^import\\s+([\\w.]+);", java.util.regex.Pattern.MULTILINE);
        java.util.regex.Matcher matcher = pattern.matcher(source);
        java.util.List<String> imports = new java.util.ArrayList<>();
        while (matcher.find()) {
            String name = matcher.group(1);
            if (!name.startsWith("java.")) {
                imports.add(name);
            }
        }
        return imports;
    }

    private static java.util.List<String> readDeclarations(Path file) throws IOException {
        String source = Files.readString(file);
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
                "^(?:public\\s+|protected\\s+|private\\s+)?(?:static\\s+)?(?:final\\s+)?(?:sealed\\s+)?(class|interface|record)\\s+(\\w+(?:<[^>]+>)?)",
                java.util.regex.Pattern.MULTILINE);
        java.util.regex.Matcher matcher = pattern.matcher(source);
        java.util.List<String> declarations = new java.util.ArrayList<>();
        while (matcher.find()) {
            String kind = matcher.group(1);
            String name = matcher.group(2);
            if ("record".equals(kind)) {
                kind = "class";
            }
            declarations.add("export " + kind + " " + name + " {}");
        }
        return declarations;
    }

    private static java.util.Map<String, java.util.List<String>> readMethods(Path file) throws IOException {
        String source = Files.readString(file);
        source = source.replaceAll("(?s)/\\*.*?\\*/", "");
        source = source.replaceAll("//.*", "");
        java.util.Map<String, java.util.List<String>> map = new java.util.LinkedHashMap<>();
        java.util.regex.Pattern classPat = java.util.regex.Pattern.compile("(?:class|interface|record)\\s+(\\w+)[^{]*\\{");
        java.util.regex.Matcher cMatcher = classPat.matcher(source);
        while (cMatcher.find()) {
            String name = cMatcher.group(1);
            int start = cMatcher.end();
            int level = 1;
            int i = start;
            while (i < source.length() && level > 0) {
                char ch = source.charAt(i);
                if (ch == '{') level++; else if (ch == '}') level--;
                i++;
            }
            String body = source.substring(start, i - 1);
            java.util.regex.Pattern methodPat = java.util.regex.Pattern.compile(
                    "(?:public\\s+|protected\\s+|private\\s+)?(?:static\\s+)?(?:final\\s+)?([\\w<>\\[\\]]+)\\s+(\\w+)\\s*\\([^)]*\\)\\s*\\{");
            java.util.regex.Matcher mMatcher = methodPat.matcher(body);
            java.util.List<String> list = new java.util.ArrayList<>();
            while (mMatcher.find()) {
                String returnType = mMatcher.group(1);
                String mName = mMatcher.group(2);
                if (!mName.equals(name)) {
                    list.add("\t" + mName + "(): " + returnType + " {");
                    list.add("\t}");
                }
            }
            map.put(name, list);
        }
        return map;
    }

    private static String stubContent(Path relative, Path from, Path root,
                                      java.util.List<String> imports,
                                      java.util.List<String> declarations,
                                      java.util.Map<String, java.util.List<String>> methods) {
        StringBuilder builder = new StringBuilder();
        builder.append("// Auto-generated from ").append(relative).append(System.lineSeparator());

        java.util.Map<String, java.util.List<String>> byPath = new java.util.LinkedHashMap<>();
        for (String imp : imports) {
            String className = imp.substring(imp.lastIndexOf('.') + 1);
            Path target = root.resolve(imp.replace('.', '/') + ".ts");
            Path rel = from.relativize(target);
            String path = rel.toString().replace('\\', '/');
            path = path.replaceFirst("\\.ts$", "");
            if (!path.startsWith(".")) {
                path = "./" + path;
            }
            byPath.computeIfAbsent(path, k -> new java.util.ArrayList<>()).add(className);
        }

        for (var entry : byPath.entrySet()) {
            builder.append("import { ");
            builder.append(String.join(", ", entry.getValue()));
            builder.append(" } from \"").append(entry.getKey()).append("\"");
            builder.append(";").append(System.lineSeparator());
        }

        if (declarations.isEmpty()) {
            builder.append("export {};").append(System.lineSeparator());
            return builder.toString();
        }

        java.util.regex.Pattern namePattern = java.util.regex.Pattern.compile("export \\w+ (\\w+) \\{\\}");
        for (String decl : declarations) {
            java.util.regex.Matcher m = namePattern.matcher(decl);
            if (!m.matches()) {
                builder.append(decl).append(System.lineSeparator());
                continue;
            }
            String name = m.group(1);
            java.util.List<String> mList = methods.getOrDefault(name, java.util.Collections.emptyList());
            if (mList.isEmpty()) {
                builder.append(decl).append(System.lineSeparator());
                continue;
            }
            builder.append(decl.substring(0, decl.length() - 1)).append(System.lineSeparator());
            for (String method : mList) {
                builder.append(method).append(System.lineSeparator());
            }
            builder.append("}").append(System.lineSeparator());
        }
        return builder.toString();
    }

    private static String importLine(Path from, Path root, String name) {
        String className = name.substring(name.lastIndexOf('.') + 1);
        Path target = root.resolve(name.replace('.', '/') + ".ts");
        Path rel = from.relativize(target);
        String path = rel.toString().replace('\\', '/');
        path = path.replaceFirst("\\.ts$", "");
        if (!path.startsWith(".")) {
            path = "./" + path;
        }
        return "import { " + className + " } from \"" + path + "\";";
    }

    public static void main(String[] args) {
        Path javaRoot = Path.of("src/java");
        Path tsRoot = Path.of("src/node");
        writeTypeScriptStubs(javaRoot, tsRoot).ifPresent(e -> {
            e.printStackTrace();
            System.exit(1);
        });
        writeDiagram(Path.of("diagram.puml")).ifPresent(e -> {
            e.printStackTrace();
            System.exit(1);
        });
    }
}
