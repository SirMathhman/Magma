package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        try (final var stream = Files.walk(Paths.get(".", "src", "java"))) {
            final var sources = stream.filter(Files::isRegularFile).filter(path -> path.toString().endsWith(".java")).toList();

            final var buffer = new StringBuilder();
            for (var source : sources) {
                final var fileName = source.getFileName().toString();
                final var separator = fileName.lastIndexOf(".");
                final var name = fileName.substring(0, separator);
                buffer.append("class " + name + "\n");
            }

            final var target = Paths.get(".", "diagram.puml");
            Files.writeString(target, "@startuml\nskinparam linetype ortho\n" +
                    buffer +
                    "@enduml");
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }
}
