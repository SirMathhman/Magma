package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public class Main {
    private static final String SEPARATOR = System.lineSeparator();

    private Main() {
    }

    public static void main(final String[] args) {
        try {
            final var source = Paths.get(".", "src", "java", "magma", "Main.java");
            final var input = Files.readString(source);
            final var output = Main.compile(input);

            final var target = Paths.get(".", "diagram.puml");
            final var joined = String.join(Main.SEPARATOR,
                    "@startuml",
                    "skinparam linetype ortho",
                    "class Main",
                    output,
                    "@enduml");
            Files.writeString(target, joined);
        } catch (final IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static String compile(final CharSequence input) {
        final var segments = Main.divide(input);

        final var outputBuilder = new StringBuilder();
        for (final var segment : segments)
            Main.compileRootSegment(segment)
                    .ifPresent(outputBuilder::append);

        return outputBuilder.toString();
    }

    private static Optional<String> compileRootSegment(final String segment) {
        final var strip = segment.strip();
        if (!strip.startsWith("import "))
            return Optional.empty();

        final var prefixLength = "import ".length();
        final var withoutPrefix = strip.substring(prefixLength);
        final var inputLength0 = withoutPrefix.length();
        if (withoutPrefix.isEmpty() || ';' != withoutPrefix.charAt(inputLength0 - 1))
            return Optional.empty();

        final var inputLength = withoutPrefix.length();
        final var suffixLength = ";".length();
        final var withoutEnd = withoutPrefix.substring(0, inputLength - suffixLength);

        final var separator = withoutEnd.lastIndexOf('.');
        if (0 > separator)
            return Optional.empty();

        final var infixLength = ".".length();
        final var name = withoutEnd.substring(separator + infixLength);
        return Optional.of("Main --> " + name + Main.SEPARATOR);
    }

    private static List<String> divide(final CharSequence input) {
        final State state = new MutableState();
        final var length = input.length();
        var current = state;
        for (var i = 0; i < length; i++) {
            final var c = input.charAt(i);
            current = Main.fold(current, c);
        }

        return current.advance()
                .segments();
    }

    private static State fold(final State state, final char c) {
        final var appended = state.append(c);
        if (';' == c)
            return appended.advance();
        return appended;
    }
}
