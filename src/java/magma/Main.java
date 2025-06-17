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
            Files.writeString(target, "@startuml\nskinparam linetype ortho\nclass magma.Main\n" + output + "@enduml");
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static List<String> divide(CharSequence input) {
        final List<String> segments = new ArrayList<>();
        var buffer = new StringBuilder();

        return getStrings(input, new State(segments, buffer));
    }

    private static List<String> getStrings(CharSequence input, State state) {
        for (var i = 0; i < input.length(); i++) {
            final var c = input.charAt(i);
            append(state, c);
            if (c == ';')
                extracted(state);
        }

        extracted(state);
        return state.segments();
    }

    private static State append(State state, char c) {
        state.getBuffer()
                .append(c);

        return state;
    }

    private static State extracted(State state) {
        final var fileName = state.getBuffer()
                .toString();
        state.segments()
                .add(fileName);
        state.setBuffer(new StringBuilder());
        return state;
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
