package magma.app.io;

import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;
import magma.app.error.ApplicationError;
import magma.app.error.ThrowableError;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record PathSources(Path rootDirectory) implements Sources {
    public static Result<String, IOException> readString(Path source) {
        try {
            return new Ok<>(Files.readString(source));
        } catch (IOException e) {
            return new Err<>(e);
        }
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

    @Override
    public Result<Map<Source, String>, ApplicationError> readAll() {
        return collect(this.rootDirectory).flatMapValue(this::readIterable)
                .mapErr(ThrowableError::new)
                .mapErr(ApplicationError::new);
    }

    private Result<Map<Source, String>, IOException> readFile(Path source, Result<Map<Source, String>, IOException> maybeInputs) {
        return maybeInputs.flatMapValue(inner -> readString(source).mapValue(input -> {
            inner.put(new PathSource(this.rootDirectory, source), input);
            return inner;
        }));
    }

    public Result<Map<Source, String>, IOException> readIterable(Iterable<Path> files) {
        Result<Map<Source, String>, IOException> maybeInputs = new Ok<>(new HashMap<>());
        for (var source : files)
            maybeInputs = this.readFile(source, maybeInputs);
        return maybeInputs;
    }
}