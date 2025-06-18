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
            final var input = Files.readString(Paths.get(".", "src", "java", "magma", "Main.java"));
            final var segments = divide(input);
            final var output = compileRootSegments(segments);
            Files.writeString(Paths.get(".", "diagram.puml"), "@startuml\n" + output + "class Main\n@enduml");
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static List<String> divide(CharSequence input) {
        final var segments = new ArrayList<String>();
        var buffer = new StringBuilder();
        return getStrings(input, new State(buffer, segments));
    }

    private static List<String> getStrings(CharSequence input, State state) {
        var current = state;
        for (var i = 0; i < input.length(); i++) {
            final var c = input.charAt(i);
            current = fold(current, c);
        }

        return current.advance()
                .segments();
    }

    private static State fold(State current, char c) {
        final var appended = current.append(c);
        if (c == ';')
            return appended.advance();
        return appended;
    }

    private static String compileRootSegments(Iterable<String> segments) {
        final var output = new StringBuilder();
        for (var segment : segments)
            compileRootSegment(segment.strip()).ifPresent(output::append);

        return output.toString();
    }

    private static Optional<String> compileRootSegment(String input) {
        if (!input.startsWith("import "))
            return Optional.empty();

        final var withoutPrefix = input.substring("import ".length());
        if (!withoutPrefix.endsWith(";"))
            return Optional.empty();

        final var withoutEnd = withoutPrefix.substring(0, withoutPrefix.length() - ";".length());
        final var separator = withoutEnd.lastIndexOf(".");
        if (separator < 0)
            return Optional.empty();

        final var name = withoutEnd.substring(separator + 1);
        return Optional.of("Main --> " + name + "\n");
    }
}
