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
     * Existing files are left untouched.
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
                if (!Files.exists(tsFile)) {
                    String content = "// Auto-generated from " + relative + System.lineSeparator() + "export {};" + System.lineSeparator();
                    Files.writeString(tsFile, content);
                }
            } catch (IOException e) {
                return Optional.of(e);
            }
        }
        return Optional.empty();
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
