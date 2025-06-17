package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
            Files.writeString(target, "@startuml\nskinparam linetype ortho\nclass magma.Main\n" + output + "@enduml");
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static List<String> divide(CharSequence input) {
        State current = new MutableState();
        for (var i = 0; i < input.length(); i++) {
            final var c = input.charAt(i);
            current = fold(current, c);
        }

        return current.advance()
                .segments();
    }

    private static State fold(State state, char c) {
        final var appended = state.append(c);
        if (c == ';')
            return appended.advance();
        return appended;
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
        return Optional.of("magma.Main --> " + withoutEnd + "\n");
    }
}
