package magma;

import magma.error.IOError;
import magma.path.PathLike;
import magma.path.PathLikes;
import magma.result.Ok;
import magma.result.Result;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class Main {
    private static final String SEPARATOR = System.lineSeparator();

    private Main() {
    }

    public static void main(final String[] args) {
        PathLikes.get(".", "src", "java")
                .walk()
                .match(Main::runWithFiles, Optional::of)
                .ifPresent(error -> System.err.println(error.display()));
    }

    private static Optional<IOError> runWithFiles(final Collection<PathLike> files) {
        final var sources = files.stream()
                .filter(file -> file.asString()
                        .endsWith(".java"))
                .toList();

        return Main.compileAll(sources)
                .match(Main::writeTarget, Optional::of);
    }

    private static Optional<IOError> writeTarget(final String compiled) {
        final var target = PathLikes.get(".", "diagram.puml");
        final var output = String.join(Main.SEPARATOR, "@startuml", "skinparam linetype ortho", compiled, "@enduml");
        return target.writeString(output);
    }

    private static Result<String> compileAll(final Iterable<PathLike> sources) {
        Result<StringBuilder> maybeCompiled = new Ok<>(new StringBuilder());
        for (final var source : sources) {
            final var fileName = source.getFileName()
                    .asString();
            final var separator = fileName.lastIndexOf('.');
            final var name = fileName.substring(0, separator);

            final var maybeOutput = source.readString()
                    .map(value -> Main.compile(value, name));

            maybeCompiled = maybeCompiled.flatMap(compiled -> maybeOutput.map(compiled::append));
        }

        return maybeCompiled.map(StringBuilder::toString);
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
