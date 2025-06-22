package magma.app.io.sources;

import magma.api.error.WrappedError;
import magma.api.io.IOError;
import magma.api.io.PathLike;
import magma.api.list.ListLike;
import magma.api.result.Ok;
import magma.api.result.Result;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record PathSources(PathLike rootDirectory) implements Sources {
    private static Result<Map<String, String>, IOError> attachEntry(final PathLike source, final Result<Map<String, String>, IOError> maybeInputs) {
        final var fileName = source.getFileName()
                .asString();
        final var separator = fileName.lastIndexOf('.');
        final var name = fileName.substring(0, separator);

        return maybeInputs.flatMapValue(inputs -> source.readString()
                .mapValue(input -> {
                    inputs.put(name, input);
                    return inputs;
                }));
    }

    private static Result<Map<String, String>, IOError> readAll(final Iterable<PathLike> sources) {
        Result<Map<String, String>, IOError> maybeInputs = new Ok<>(new HashMap<>());
        for (final var source : sources)
            maybeInputs = PathSources.attachEntry(source, maybeInputs);
        return maybeInputs;
    }

    private static List<PathLike> filter(final ListLike<PathLike> files) {
        return files.stream()
                .filter(file -> file.asString()
                        .endsWith(".java"))
                .toList();
    }

    @Override
    public Result<Map<String, String>, WrappedError> collect() {
        return this.rootDirectory.walk()
                .mapValue(PathSources::filter)
                .mapErr(WrappedError::new)
                .flatMapValue(files -> PathSources.readAll(files)
                        .mapErr(WrappedError::new));
    }
}