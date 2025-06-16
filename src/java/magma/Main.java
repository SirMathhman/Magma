package magma;

import magma.api.Err;
import magma.api.Error;
import magma.api.Ok;
import magma.api.Result;
import magma.api.ThrowableError;
import magma.app.Compiler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        run().ifPresent(error -> System.err.println(error.display()));
    }

    private static Optional<? extends Error> run() {
        return collect().match(sources -> readAll(sources).match(Main::compileAndWrite, Optional::of), Optional::of);
    }

    private static Optional<? extends Error> compileAndWrite(Map<String, String> inputs) {
        return new Compiler().compile(inputs)
                .match(output -> writeString(Paths.get(".", "diagram.puml"), output), Optional::of);
    }

    private static Result<Map<String, String>, Error> readAll(Iterable<Path> sources) {
        Result<Map<String, String>, Error> inputs = new Ok<>(new HashMap<>());
        for (var source : sources)
            inputs = inputs.flatMap(inner -> readSource(source).mapValue(inner0 -> {
                inner.putAll(inner0);
                return inner;
            }));
        return inputs;
    }

    private static Result<Set<Path>, Error> collect() {
        try (final var stream = Files.walk(Paths.get(".", "src", "java"))) {
            final var sources = stream.filter(Files::isRegularFile)
                    .filter(path -> path.toString()
                            .endsWith(".java"))
                    .collect(Collectors.toSet());

            return new Ok<>(sources);
        } catch (IOException e) {
            return new Err<>(new ThrowableError(e));
        }
    }

    private static Optional<Error> writeString(Path path, CharSequence content) {
        try {
            Files.writeString(path, content);
            return Optional.empty();
        } catch (IOException e) {
            return Optional.of(new ThrowableError(e));
        }
    }

    private static Result<Map<String, String>, Error> readSource(Path source) {
        try {
            final var fileName = source.getFileName()
                    .toString();

            final var extensionSeparator = fileName.lastIndexOf(".");
            final var name = fileName.substring(0, extensionSeparator);
            final var input = Files.readString(source);
            return new Ok<>(Map.of(name, input));
        } catch (IOException e) {
            return new Err<>(new ThrowableError(e));
        }
    }
}
