package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        try (final var stream = Files.walk(Paths.get(".", "src", "java"))) {
            final var sources = stream.filter(Files::isRegularFile)
                    .filter(path -> path.toString()
                            .endsWith(".java"))
                    .collect(Collectors.toSet());

            final var output = new StringBuilder();
            for (var source : sources) {
                final var fileName = source.getFileName()
                        .toString();
                final var separator = fileName.lastIndexOf(".");
                final var name = fileName.substring(0, separator);
                output.append("class " + name + "\n");
            }

            Files.writeString(Paths.get(".", "diagram.puml"), "@startuml\n" + output + "@enduml");
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }
}
