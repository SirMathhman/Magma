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
        final var source = Paths.get(".", "src", "java", "magma", "Main.java");
        Main.readString(source)
                .match(Main::compileAndWrite, Optional::of)
                .ifPresent(Throwable::printStackTrace);
    }

    private static Optional<IOException> compileAndWrite(final String input) {
        final var compiled = Main.compile(input);

        final var target = Paths.get(".", "diagram.puml");
        final var output = String.join(Main.SEPARATOR,
                "@startuml",
                "skinparam linetype ortho",
                "class Main",
                compiled.toString(),
                "@enduml");

        return Main.writeString(target, output);
    }

    private static Optional<IOException> writeString(final Path target, final CharSequence output) {
        try {
            Files.writeString(target, output);
            return Optional.empty();
        } catch (final IOException e) {
            return Optional.of(e);
        }
    }

    private static Result readString(final Path source) {
        try {
            return new Ok(Files.readString(source));
        } catch (final IOException e) {
            return new Err(e);
        }
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
