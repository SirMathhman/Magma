package magma;

import magma.error.IOError;
import magma.error.JavaIOError;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

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
                .ifPresent(error -> System.err.println(error.display()));
    }

    private static Optional<IOError> runWithFiles(final Collection<Path> files) {
        final var sources = files.stream()
                .filter(file -> file.toString()
                        .endsWith(".java"))
                .toList();

        return Main.compileAll(sources)
                .match(Main::writeTarget, Optional::of);
    }

    private static Optional<IOError> writeTarget(final String compiled) {
        final var target = Paths.get(".", "diagram.puml");
        final var output = String.join(Main.SEPARATOR, "@startuml", "skinparam linetype ortho", compiled, "@enduml");

        return Main.writeString(target, output);
    }

    private static Result<String> compileAll(final Iterable<Path> sources) {
        Result<StringBuilder> maybeCompiled = new Ok<>(new StringBuilder());
        for (final var source : sources) {
            final var fileName = source.getFileName()
                    .toString();
            final var separator = fileName.lastIndexOf('.');
            final var name = fileName.substring(0, separator);

            final var maybeOutput = Main.readString(source)
                    .map(value -> Main.compile(value, name));

            maybeCompiled = maybeCompiled.flatMap(compiled -> maybeOutput.map(compiled::append));
        }

        return maybeCompiled.map(StringBuilder::toString);
    }

    private static Result<List<Path>> walk() {
        try (final var stream = Files.walk(Paths.get(".", "src", "java"))) {
            return new Ok<>(stream.toList());
        } catch (final IOException e) {
            return new Err<>(new JavaIOError(e));
        }
    }

    private static Optional<IOError> writeString(final Path target, final CharSequence output) {
        try {
            Files.writeString(target, output);
            return Optional.empty();
        } catch (final IOException e) {
            return Optional.of(new JavaIOError(e));
        }
    }

    private static Result<String> readString(final Path source) {
        try {
            return new Ok<>(Files.readString(source));
        } catch (final IOException e) {
            return new Err<>(new JavaIOError(e));
        }
    }

    private static String compile(final String input, final String name) {
        final var segments = input.split(";");

        final var output = new StringBuilder();
        for (final var segment : segments)
            Main.compileRootSegment(segment, name)
                    .ifPresent(output::append);

        return "class " + name + Main.SEPARATOR + output;
    }

    private static Optional<String> compileRootSegment(final String input, final String name) {
        final var strip = input.strip();
        if (!strip.startsWith("import "))
            return Optional.empty();

        final var withoutPrefix = strip.substring("import ".length());
        final var separator = withoutPrefix.lastIndexOf('.');
        if (0 > separator)
            return Optional.empty();

        final var child = withoutPrefix.substring(separator + ".".length());
        if (!List.of("Function", "Supplier")
                .contains(child))
            return Optional.of(name + " --> " + child + Main.SEPARATOR);

        return Optional.empty();
    }
}
