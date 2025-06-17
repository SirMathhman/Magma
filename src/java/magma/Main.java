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
            var depth = 0;
            for (var i = 0; i < input.length(); i++) {
                final var c = input.charAt(i);
                buffer.append(c);
                if (c == ';' && depth == 0) {
                    segments.add(buffer.toString());
                    buffer = new StringBuilder();
                }
                else {
                    if (c == '{')
                        depth++;
                    if (c == '}')
                        depth--;
                }
            }
            segments.add(buffer.toString());

            final var output = new StringBuilder();
            for (var segment : segments) {
                final var strip = segment.strip();
                if (strip.startsWith("import ")) {
                    final var withoutStart = strip.substring("import ".length());
                    if (withoutStart.endsWith(";")) {
                        final var withoutEnd = withoutStart.substring(0, withoutStart.length() - ";".length());
                        output.append("magma.Main --> " + withoutEnd + "\n");
                    }
                }
                if (strip.contains("class "))
                    output.append("class magma.Main\n");
            }

            final var path = Paths.get(".", "diagram.puml");
            Files.writeString(path, "@startuml\nskinparam linetype ortho\n" + output + "@enduml");
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }
}
