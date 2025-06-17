package magma.app.io;

import magma.api.Error;
import magma.api.collect.iter.Iterable;
import magma.api.collect.list.JavaList;
import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;
import magma.app.error.ApplicationError;
import magma.app.error.ThrowableError;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public record PathSources(Path rootDirectory) implements Sources {
    public static Result<String, IOException> readString(Path source) {
        try {
            return new Ok<>(Files.readString(source));
        } catch (IOException e) {
            return new Err<>(e);
        }
    }

    static Result<Iterable<Path>, IOException> collect(Path sourceDirectory) {
        try (var files = Files.walk(sourceDirectory)) {
            final var sources = new JavaList<>(files.filter(Files::isRegularFile)
                    .filter(path -> path.toString()
                            .endsWith("java"))
                    .toList());

            return new Ok<>(sources);
        } catch (IOException e) {
            return new Err<>(e);
        }
    }

    @Override
    public Result<Map<Source, String>, Error> readAll() {
        Result<Iterable<Path>, IOException> listIOExceptionResult = collect(this.rootDirectory);
        Result<Map<Source, String>, IOException> mapIOExceptionResult = switch (listIOExceptionResult) {
            case Err<Iterable<Path>, IOException>(IOException error2) -> new Err<>(error2);
            case Ok<Iterable<Path>, IOException>(Iterable<Path> value2) -> this.readIterable(value2);
        };
        Result<Map<Source, String>, Error> mapThrowableErrorResult = switch (mapIOExceptionResult) {
            case Err<Map<Source, String>, IOException>(IOException error) -> new Err<>(new ThrowableError(error));
            case Ok<Map<Source, String>, IOException>(Map<Source, String> value) -> new Ok<>(value);
        };
        return switch (mapThrowableErrorResult) {
            case Err<Map<Source, String>, Error>(Error error1) -> new Err<>(new ApplicationError(error1));
            case Ok<Map<Source, String>, Error>(Map<Source, String> value1) -> new Ok<>(value1);
        };
    }

    private Result<Map<Source, String>, IOException> readFile(Path source, Result<Map<Source, String>, IOException> maybeInputs) {
        return switch (maybeInputs) {
            case Err<Map<Source, String>, IOException>(IOException error1) -> new Err<>(error1);
            case Ok<Map<Source, String>, IOException>(
                    Map<Source, String> value1
            ) -> this.getMapIOExceptionResult(source, value1);
        };
    }

    private Result<Map<Source, String>, IOException> getMapIOExceptionResult(Path source, Map<Source, String> value1) {
        return switch (readString(source)) {
            case Err<String, IOException>(IOException error) -> new Err<>(error);
            case Ok<String, IOException>(
                    String value
            ) -> {
                value1.put(new PathSource(this.rootDirectory, source), value);
                yield new Ok<>(value1);
            }
        };
    }

    public Result<Map<Source, String>, IOException> readIterable(Iterable<Path> files) {
        return files.iter()
                .<Result<Map<Source, String>, IOException>>fold(new Ok<>(new HashMap<>()),
                        (currentResult, path) -> this.readFile(path, currentResult));
    }

}