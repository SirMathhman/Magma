package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import magma.Sources;
import java.util.Optional;

public class GenerateDiagram {
    // Helper methods split to comply with SRP (Single Responsibility Principle)
    /**
     * Generates a PlantUML diagram and writes it to {@code output}. Instead of
     * throwing an exception, any I/O error is returned wrapped in an
     * {@link Optional}.
     */
    public static Optional<IOException> writeDiagram(Path output) {
        Path src = Path.of("src/java/magma");
        Result<List<String>, IOException> sources = readSources(src);
        if (sources.isErr()) {
            return Optional.of(((Err<List<String>, IOException>) sources).error());
        }
        List<String> allSources = ((Ok<List<String>, IOException>) sources).value();
        Sources analysis = new Sources(allSources);
        List<String> classes = analysis.findClasses();
        var implementations = analysis.findImplementations();
        StringBuilder content = new StringBuilder("@startuml\n");
        content.append(classesSection(classes));
        content.append(analysis.formatRelations(classes, implementations));
        content.append("@enduml\n");
        try {
            Files.writeString(output, content.toString());
            return Optional.empty();
        } catch (IOException e) {
            return Optional.of(e);
        }
    }
    private static String classesSection(List<String> classes) {
        StringBuilder builder = new StringBuilder();
        for (String name : classes) {
            builder.append("class ").append(name).append("\n");
        }
        return builder.toString();
    }

    private static Result<List<String>, IOException> readSources(Path directory) {
        List<Path> files;
        try (Stream<Path> stream = Files.walk(directory)) {
            files = stream.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".java"))
                    .toList();
        } catch (IOException e) {
            return new Err<>(e);
        }

        List<String> sources = new ArrayList<>();
        for (Path file : files) {
            try {
                sources.add(Files.readString(file));
            } catch (IOException e) {
                return new Err<>(e);
            }
        }
        return new Ok<>(sources);
    }

    public static void main(String[] args) {
        writeDiagram(Path.of("diagram.puml"));
    }
}
