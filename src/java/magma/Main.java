package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class Main {
    private static final String SEPARATOR = System.lineSeparator();

    private Main() {
    }

    public static void main(final String[] args) {
        try {
            final var source = Paths.get(".", "src", "java", "magma", "Main.java");
            final var input = Main.getReadString(source);
            final var compiled = Main.compile(input);

            final var target = Paths.get(".", "diagram.puml");
            final var output = String.join(Main.SEPARATOR,
                    "@startuml",
                    "skinparam linetype ortho",
                    "class Main",
                    compiled.toString(),
                    "@enduml");

            Main.writeString(target, output);
        } catch (final IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    private static void writeString(final Path target, final CharSequence output) throws IOException {
        Files.writeString(target, output);
    }

    private static String getReadString(final Path source) throws IOException {
        return Files.readString(source);
    }

    private static StringBuilder compile(final String input) {
        final var segments = input.split(";");

        final var output = new StringBuilder();
        for (final var segment : segments)
            Main.compileRootSegment(segment)
                    .ifPresent(output::append);

        return output;
    }

    private static Optional<String> compileRootSegment(final String input) {
        final var strip = input.strip();
        if (strip.startsWith("import ")) {
            final var withoutPrefix = strip.substring("import ".length());
            final var separator = withoutPrefix.lastIndexOf('.');
            if (0 <= separator) {
                final var child = withoutPrefix.substring(separator + ".".length());
                return Optional.of("Main --> " + child + Main.SEPARATOR);
            }
        }

        return Optional.empty();
    }
}
