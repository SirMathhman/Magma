package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            final var source = Paths.get(".", "src", "java", "magma", "Main.java");
            final var input = Files.readString(source);
            final List<String> segments = new ArrayList<>();

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

            final var output = new StringBuilder();
            for (var segment : segments) {
                final var stripped = segment.strip();
                if (stripped.startsWith("import ")) {
                    final var withoutStart = stripped.substring("import ".length());
                    if (withoutStart.endsWith(";")) {
                        final var withoutEnd = withoutStart.substring(0, withoutStart.length() - ";".length());
                        final var separator = withoutEnd.lastIndexOf(".");
                        if (separator >= 0) {
                            final var child = withoutEnd.substring(separator + ".".length());
                            output.append("Main --> " + child + "\n");
                        }
                    }
                }
            }

            final var target = Paths.get(".", "diagram.puml");
            Files.writeString(target, "@startuml\nskinparam linetype ortho\nclass Main\n" + output + "@enduml");
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }
}
