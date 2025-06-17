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
import java.util.function.Function;

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
        Result<List<Path>, IOException> listIOExceptionResult = collect(this.rootDirectory);
        Result<Map<Source, String>, IOException> mapIOExceptionResult = switch (listIOExceptionResult) {
            case Err<List<Path>, IOException>(IOException error2) -> new Err<>(error2);
            case Ok<List<Path>, IOException>(List<Path> value2) ->
                    ((Function<List<Path>, Result<Map<Source, String>, IOException>>) this::readIterable).apply(value2);
        };
        Result<Map<Source, String>, ThrowableError> mapThrowableErrorResult = switch (mapIOExceptionResult) {
            case Err<Map<Source, String>, IOException>(IOException error) ->
                    new Err<>(((Function<IOException, ThrowableError>) ThrowableError::new).apply(error));
            case Ok<Map<Source, String>, IOException>(Map<Source, String> value) -> new Ok<>(value);
        };
        return switch (mapThrowableErrorResult) {
            case Err<Map<Source, String>, ThrowableError>(
                    ThrowableError error1
            ) -> new Err<>(((Function<ThrowableError, ApplicationError>) ApplicationError::new).apply(error1));
            case Ok<Map<Source, String>, ThrowableError>(Map<Source, String> value1) -> new Ok<>(value1);
        };
    }

    private Result<Map<Source, String>, IOException> readFile(Path source, Result<Map<Source, String>, IOException> maybeInputs) {
        return switch (maybeInputs) {
            case Err<Map<Source, String>, IOException>(IOException error1) -> new Err<>(error1);
            case Ok<Map<Source, String>, IOException>(
                    Map<Source, String> value1
            ) -> ((Function<Map<Source, String>, Result<Map<Source, String>, IOException>>) inner -> {
                Result<String, IOException> stringIOExceptionResult = readString(source);
                return switch (stringIOExceptionResult) {
                    case Err<String, IOException>(IOException error) -> new Err<>(error);
                    case Ok<String, IOException>(
                            String value
                    ) -> new Ok<>(((Function<String, Map<Source, String>>) input -> {
                        inner.put(new PathSource(this.rootDirectory, source), input);
                        return inner;
                    }).apply(value));
                };
            }).apply(value1);
        };
    }

    public Result<Map<Source, String>, IOException> readIterable(Iterable<Path> files) {
        Result<Map<Source, String>, IOException> maybeInputs = new Ok<>(new HashMap<>());
        for (var source : files)
            maybeInputs = this.readFile(source, maybeInputs);
        return maybeInputs;
    }
}