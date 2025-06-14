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
            final var output = compile(input);
            final var target = Paths.get(".", "diagram.puml");
            Files.writeString(target, output);
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static String compile(String input) {
        final var segments = divide(input);

        final var output = new StringBuilder();
        for (var segment : segments) {
            final var stripped = segment.strip();
            if (stripped.startsWith("import ")) {
                final var substring = stripped.substring("import ".length());
                if (substring.endsWith(";")) {
                    final var substring1 = substring.substring(0, substring.length() - ";".length());
                    final var index = substring1.lastIndexOf(".");
                    if (index >= 0) {
                        final var substring2 = substring1.substring(index + 1);
                        output.append("Main --> " + substring2 + "\n");
                    }
                }
            }
        }

        return "@startuml\nskinparam linetype ortho\nclass Main\n" +
                output +
                "@enduml";
    }

    private static List<String> divide(String input) {
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
        return segments;
    }
}