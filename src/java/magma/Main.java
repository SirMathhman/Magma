package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class Main {
    private static final String SEPARATOR = System.lineSeparator();

    private Main() {
    }

    public static void main(final String[] args) {
        Main.walk()
                .match(Main::runWithFiles, Optional::of)
                .ifPresent(Throwable::printStackTrace);
    }

    private static Optional<IOException> runWithFiles(final Collection<Path> files) {
        final var sources = files.stream()
                .filter(file -> file.toString()
                        .endsWith(".java"))
                .toList();

        final var compiled = new StringBuilder();
        for (final var source : sources) {
            final var fileName = source.getFileName()
                    .toString();
            final var separator = fileName.lastIndexOf('.');
            final var name = fileName.substring(0, separator);

            final var result = Main.readString(source)
                    .map(value -> Main.compile(value, name));

            switch (result) {
                case Err<StringBuilder>(final var error) -> {
                    return Optional.of(error);
                }
                case Ok<StringBuilder>(final var value) -> {
                    compiled.append(value);
                }
            }
        }

        final var target = Paths.get(".", "diagram.puml");
        final var output = String.join(Main.SEPARATOR,
                "@startuml",
                "skinparam linetype ortho",
                "class Main",
                compiled.toString(),
                "@enduml");

        return Main.writeString(target, output);
    }

    private static Result<List<Path>> walk() {
        try (final var stream = Files.walk(Paths.get(".", "src", "java"))) {
            return new Ok<>(stream.toList());
        } catch (final IOException e) {
            return new Err<>(e);
        }
    }

    private static Optional<IOException> writeString(final Path target, final CharSequence output) {
        try {
            Files.writeString(target, output);
            return Optional.empty();
        } catch (final IOException e) {
            return Optional.of(e);
        }
    }

    private static Result<String> readString(final Path source) {
        try {
            return new Ok<>(Files.readString(source));
        } catch (final IOException e) {
            return new Err<>(e);
        }
    }

    private static StringBuilder compile(final String input, final String name) {
        final var segments = input.split(";");

        final var output = new StringBuilder();
        for (final var segment : segments)
            Main.compileRootSegment(segment, name)
                    .ifPresent(output::append);

        return output;
    }

    private static Optional<String> compileRootSegment(final String input, final String name) {
        final var strip = input.strip();
        if (strip.startsWith("import ")) {
            final var withoutPrefix = strip.substring("import ".length());
            final var separator = withoutPrefix.lastIndexOf('.');
            if (0 <= separator) {
                final var child = withoutPrefix.substring(separator + ".".length());
                return Optional.of(name + " --> " + child + Main.SEPARATOR);
            }
        }

        return Optional.empty();
    }
}
