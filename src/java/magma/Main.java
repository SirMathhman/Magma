package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class Main {
    private static final String LINE_SEPARATOR = System.lineSeparator();

    private Main() {}

    public static void main(final String[] args) {
        try {
            final var source = Paths.get(".", "src", "java", "magma", "Main.java");
            final var input = Files.readString(source);
            final var output = Main.compile(input);
            final var target = Paths.get(".", "diagram.puml");
            final var pre = List.of("@startuml", "skinparam linetype ortho", "class Main");
            final Collection<String> outputRootSegments = new ArrayList<>(pre);
            outputRootSegments.addAll(output);
            outputRootSegments.add("@enduml");

            final var joined = String.join(Main.LINE_SEPARATOR, outputRootSegments);
            Files.writeString(target, joined);
        } catch (final IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static List<String> compile(final CharSequence input) {
        final var segments = Main.divide(input).toList();
        final List<String> output = new ArrayList<>();
        for (final var segment : segments) Main.compileRootSegment(segment, output);
        return output;
    }

    private static void compileRootSegment(final String input, final Collection<String> output) {
        final var strip = input.strip();
        final var length = strip.length();
        if (strip.isEmpty() || ';' != strip.charAt(length - 1)) return;

        final var suffixLength = ";".length();
        final var substring = strip.substring(0, length - suffixLength);
        if (!substring.startsWith("import ")) return;

        final var prefixLength = "import ".length();
        final var substring1 = substring.substring(prefixLength);
        final var separator = substring1.lastIndexOf('.');
        if (0 <= separator) {
            final var infixLength = ".".length();
            final var child = substring1.substring(separator + infixLength);
            output.add("Main --> " + child);
        }
    }

    private static Stream<String> divide(final CharSequence input) {
        return Main.compile(new MutableDivideState(input));
    }

    private static Stream<String> compile(final DivideState state) {
        var current = new Tuple<>(true, state);
        while (current.left()) {
            final var right = current.right();
            current = Main.fold(right);
        }

        return current.right().advance().stream();
    }

    private static Tuple<Boolean, DivideState> fold(final DivideState state) {
        final var maybeNextTuple = state.pop();
        if (maybeNextTuple.isEmpty()) return new Tuple<>(false, state);

        final var nextTuple = maybeNextTuple.get();
        final var nextState = nextTuple.left();
        final var next = nextTuple.right();

        final var folded = Main.fold(nextState, next);
        return new Tuple<>(true, folded);
    }

    private static DivideState fold(final DivideState current, final char next) {
        final var appended = current.append(next);
        if (';' == next) return appended.advance();
        return appended;
    }
}
