package magma;

import magma.app.Compiler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        try (final var stream = Files.walk(Paths.get(".", "src", "java"))) {
            final var sources = stream.filter(Files::isRegularFile)
                    .filter(path -> path.toString()
                            .endsWith(".java"))
                    .collect(Collectors.toSet());

            final var output = compileAll(sources);
            Files.writeString(Paths.get(".", "diagram.puml"),
                    "@startuml\nskinparam linetype ortho\n" + output + "@enduml");
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static String compileAll(Iterable<Path> sources) throws IOException {
        final var sourceMap = readAll(sources);
        return Compiler.compile(sourceMap);
    }

    private static Map<String, String> readAll(Iterable<Path> sources) throws IOException {
        final Map<String, String> sourceMap = new HashMap<>();
        for (var source : sources) {
            final var fileName = source.getFileName()
                    .toString();
            final var separator = fileName.lastIndexOf(".");
            final var name = fileName.substring(0, separator);
            final var input = Files.readString(source);

            sourceMap.put(name, input);
        }
        return sourceMap;
    }

}
