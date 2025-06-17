package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        try {
            final var source = Paths.get(".", "src", "java", "magma", "Main.java");
            final var input = Files.readString(source);
            final var segments = divide(input);
            final var output = compileSegments(segments);
            final var target = Paths.get(".", "diagram.puml");
            Files.writeString(target, "@startuml\nskinparam linetype ortho\nclass Main\n" + output + "@enduml");
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static List<String> divide(CharSequence input) {
        final List<String> segments = new ArrayList<>();

        var buffer = new StringBuilder();
        for (var i = 0; i < input.length(); i++) {
            final var c = input.charAt(i);
            buffer.append(c);
            if (c == ';') {
                final var fileName = buffer.toString();
                segments.add(fileName);
                buffer = new StringBuilder();
            }
        }

        segments.add(buffer.toString());
        return segments;
    }

    private static StringBuilder compileSegments(Iterable<String> segments) {
        final var output = new StringBuilder();
        for (var segment : segments)
            extracted(segment).ifPresent(output::append);
        return output;
    }

    private static Optional<String> extracted(String segment) {
        final var stripped = segment.strip();
        if (!stripped.startsWith("import "))
            return Optional.empty();

        final var withoutStart = stripped.substring("import ".length());
        if (!withoutStart.endsWith(";"))
            return Optional.empty();

        final var withoutEnd = withoutStart.substring(0, withoutStart.length() - ";".length());
        final var separator = withoutEnd.lastIndexOf(".");
        if (separator < 0)
            return Optional.empty();

        final var child = withoutEnd.substring(separator + ".".length());
        return Optional.of("Main --> " + child + "\n");
    }
}
