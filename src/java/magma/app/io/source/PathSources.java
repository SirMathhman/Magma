package magma.app.io.source;

import magma.api.io.IOError;
import magma.api.io.PathLike;
import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public record PathSources(PathLike root) implements Sources {
    @Override
    public Result<Set<Source>, IOError> collect() {
        return switch (this.root.walk()) {
            case Ok(var sources) -> this.filter(sources);
            case Err(var error) -> new Err<>(error);
        };
    }

    private Result<Set<Source>, IOError> filter(Collection<PathLike> sources) {
        return new Ok<>(sources.stream()
                .filter(PathLike::isRegularFile)
                .filter(path -> path.asString()
                        .endsWith(".java"))
                .map(source -> new PathSource(this.root, source))
                .collect(Collectors.toSet()));
    }
}