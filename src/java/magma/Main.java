package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
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
                final var extensionSeparator = fileName.lastIndexOf(".");
                final var name = fileName.substring(0, extensionSeparator);
                output.append("class " + name + "\n");

                final var input = Files.readString(source);
                final var segments = new ArrayList<String>();
                var buffer = new StringBuilder();
                for (var i = 0; i < input.length(); i++) {
                    final var c = input.charAt(i);
                    buffer.append(c);
                    if (c == ';') {
                        segments.add(buffer.toString());
                        buffer = new StringBuilder();
                    }
                }
                segments.add(buffer.toString());

                for (var segment : segments) {
                    final var stripped = segment.strip();
                    if (stripped.startsWith("import ")) {
                        final var withoutPrefix = stripped.substring("import ".length());
                        if (withoutPrefix.endsWith(";")) {
                            final var withoutSuffix = withoutPrefix.substring(0, withoutPrefix.length() - ";".length());
                            final var separator = withoutSuffix.lastIndexOf(".");
                            if (separator >= 0) {
                                final var child = withoutSuffix.substring(separator + ".".length());
                                output.append(name + " --> " + child + "\n");
                            }
                        }
                    }
                }
            }

            Files.writeString(Paths.get(".", "diagram.puml"), "@startuml\nskinparam linetype ortho\n" + output + "@enduml");
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }
}
