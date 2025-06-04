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
        for (var info : extractClasses(source)) {
            map.put(info.name(), methodsFromBody(info.body(), info.name()));
        }
        return map;
    }

    private static java.util.List<ClassInfo> extractClasses(String source) {
        java.util.regex.Pattern classPat = java.util.regex.Pattern.compile("(?:class|interface|record)\\s+(\\w+)[^{]*\\{");
        java.util.regex.Matcher matcher = classPat.matcher(source);
        java.util.List<ClassInfo> list = new java.util.ArrayList<>();
        while (matcher.find()) {
            String name = matcher.group(1);
            int end = classEnd(source, matcher.end());
            String body = source.substring(matcher.end(), end);
            list.add(new ClassInfo(name, body));
        }
        return list;
    }

    private static int classEnd(String source, int start) {
        int level = 1;
        int i = start;
        while (i < source.length() && level > 0) {
            char ch = source.charAt(i);
            if (ch == '{') level++; else if (ch == '}') level--;
            i++;
        }
        return i - 1;
    }

    private static java.util.List<String> methodsFromBody(String body, String className) {
        java.util.regex.Pattern methodPat = java.util.regex.Pattern.compile(
                "(?:public\\s+|protected\\s+|private\\s+)?(static\\s+)?(?:final\\s+)?([\\w<>\\[\\]]+)\\s+(\\w+)\\s*\\([^)]*\\)\\s*\\{");
        java.util.regex.Matcher matcher = methodPat.matcher(body);
        java.util.List<String> list = new java.util.ArrayList<>();
        while (matcher.find()) {
            String staticKw = matcher.group(1);
            String returnType = matcher.group(2);
            String name = matcher.group(3);
            if (!name.equals(className)) {
                String prefix = staticKw == null ? "" : "static ";
                list.add("\t" + prefix + name + "(): " + tsType(returnType) + " {");
                list.add("\t}");
            }
        }
        return list;
    }

    private record ClassInfo(String name, String body) {}

    private static String stubContent(Path relative, Path from, Path root,
                                      java.util.List<String> imports,
                                      java.util.List<String> declarations,
                                      java.util.Map<String, java.util.List<String>> methods) {
        StringBuilder builder = new StringBuilder();
        builder.append("// Auto-generated from ").append(relative).append(System.lineSeparator());

        java.util.Map<String, java.util.List<String>> byPath = importsByPath(from, root, imports);
        builder.append(formatImports(byPath));

        if (declarations.isEmpty()) {
            builder.append("export {};").append(System.lineSeparator());
            return builder.toString();
        }

        builder.append(formatDeclarations(declarations, methods));
        return builder.toString();
    }

    private static java.util.Map<String, java.util.List<String>> importsByPath(Path from,
                                                                               Path root,
                                                                               java.util.List<String> imports) {
        java.util.Map<String, java.util.List<String>> map = new java.util.LinkedHashMap<>();
        for (String imp : imports) {
            String className = imp.substring(imp.lastIndexOf('.') + 1);
            Path target = root.resolve(imp.replace('.', '/') + ".ts");
            Path rel = from.relativize(target);
            String path = rel.toString().replace('\\', '/');
            path = path.replaceFirst("\\.ts$", "");
            if (!path.startsWith(".")) {
                path = "./" + path;
            }
            map.computeIfAbsent(path, k -> new java.util.ArrayList<>()).add(className);
        }
        return map;
    }

    private static String formatImports(java.util.Map<String, java.util.List<String>> byPath) {
        StringBuilder builder = new StringBuilder();
        for (var entry : byPath.entrySet()) {
            builder.append("import { ")
                   .append(String.join(", ", entry.getValue()))
                   .append(" } from \"")
                   .append(entry.getKey())
                   .append("\";")
                   .append(System.lineSeparator());
        }
        return builder.toString();
    }

    private static String formatDeclarations(java.util.List<String> declarations,
                                             java.util.Map<String, java.util.List<String>> methods) {
        StringBuilder builder = new StringBuilder();
        java.util.regex.Pattern namePattern = java.util.regex.Pattern.compile(
                "export \\w+ (\\w+)(?:<[^>]+>)? \\{\\}");
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
            builder.append(methodLines(mList));
            builder.append("}").append(System.lineSeparator());
        }
        return builder.toString();
    }

    private static String methodLines(java.util.List<String> mList) {
        StringBuilder builder = new StringBuilder();
        for (String method : mList) {
            builder.append(method).append(System.lineSeparator());
        }
        return builder.toString();
    }

    private static String tsType(String javaType) {
        return switch (javaType) {
            case "char", "String" -> "string";
            default -> javaType;
        };
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
