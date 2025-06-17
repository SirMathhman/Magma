package magma;

import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;
import magma.app.error.ApplicationError;
import magma.app.error.ThrowableError;
import magma.app.io.PathSource;
import magma.app.io.Source;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        final var sourceDirectory = Paths.get(".", "src", "java");
        collect(sourceDirectory).mapErr(ThrowableError::new)
                .mapErr(ApplicationError::new)
                .match(sources -> collect(sources, sourceDirectory), Optional::of)
                .ifPresent(error -> System.err.println(error.display()));
    }

    private static Optional<ApplicationError> collect(Iterable<Path> sources, Path sourceDirectory) {
        return readAll(sources, sourceDirectory).match(inputs -> handleCompileResult(new Compiler().compile(inputs)),
                Optional::of);
    }

    private static Optional<ApplicationError> handleCompileResult(Result<String, ApplicationError> result) {
        return result.match(currentOutput -> {
            final var target = Paths.get(".", "diagram.puml");
            final var content = "@startuml\nskinparam linetype ortho\n" + currentOutput + "@enduml";
            return writeString(target, content).map(ThrowableError::new)
                    .map(ApplicationError::new);
        }, Optional::of);
    }

    private static Result<Map<Source, String>, ApplicationError> readAll(Iterable<Path> sources, Path sourceDirectory) {
        Result<Map<Source, String>, ApplicationError> maybeInputs = new Ok<>(new HashMap<>());
        for (var source : sources)
            maybeInputs = readFile(sourceDirectory, source, maybeInputs);
        return maybeInputs;
    }

    private static Optional<IOException> writeString(Path target, CharSequence content) {
        try {
            Files.writeString(target, content);
            return Optional.empty();
        } catch (IOException e) {
            return Optional.of(e);
        }
    }

    public static Result<String, IOException> readString(Path source) {
        try {
            return new Ok<>(Files.readString(source));
        } catch (IOException e) {
            return new Err<>(e);
        }
    }

    static Result<Map<Source, String>, ApplicationError> readFile(Path sourceDirectory, Path source, Result<Map<Source, String>, ApplicationError> maybeInputs) {
        return maybeInputs.flatMapValue(inner -> readString(source).mapErr(ThrowableError::new)
                .mapErr(ApplicationError::new)
                .mapValue(input -> {
                    inner.put(new PathSource(sourceDirectory, source), input);
                    return inner;
                }));
    }

    static Result<List<Path>, IOException> collect(Path sourceDirectory) {
        try (var files = Files.walk(sourceDirectory)) {
            final var sources = files.filter(Files::isRegularFile)
                    .filter(path -> path.toString()
                            .endsWith("java"))
                    .toList();

            return new Ok<>(sources);
        } catch (IOException e) {
            return new Err<>(e);
        }
    }
}
