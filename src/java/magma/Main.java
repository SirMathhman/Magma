package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        try {
            final var input = Files.readString(Paths.get(".", "src", "java", "magma", "Main.java"));
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

            final var output = new StringBuilder();
            for (var segment : segments) {
                final var stripped = segment.strip();
                if (stripped.startsWith("import ")) {
                    final var withoutPrefix = stripped.substring("import ".length());
                    if (withoutPrefix.endsWith(";")) {
                        final var withoutEnd = withoutPrefix.substring(0, withoutPrefix.length() - ";".length());
                        final var separator = withoutEnd.lastIndexOf(".");
                        if (separator >= 0) {
                            final var name = withoutEnd.substring(separator + 1);
                            output.append("Main --> " + name + "\n");
                        }
                    }
                }
            }

            Files.writeString(Paths.get(".", "diagram.puml"), "@startuml\n" + output + "class Main\n@enduml");
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }
}
