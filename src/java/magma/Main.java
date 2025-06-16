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

            final Map<String, String> inputs = new HashMap<>();
            for (var source : sources)
                inputs.putAll(readSource(source));

            new Compiler().compile(inputs)
                    .consume(output -> {
                        try {
                            Files.writeString(Paths.get(".", "diagram.puml"), output);
                        } catch (IOException e) {
                            //noinspection CallToPrintStackTrace
                            e.printStackTrace();
                        }
                    }, error -> System.err.println(error.display()));
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static Map<String, String> readSource(Path source) throws IOException {
        final var fileName = source.getFileName()
                .toString();

        final var extensionSeparator = fileName.lastIndexOf(".");
        final var name = fileName.substring(0, extensionSeparator);
        final var input = Files.readString(source);
        return Map.of(name, input);
    }

}
