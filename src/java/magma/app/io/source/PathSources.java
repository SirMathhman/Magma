package magma.app.io.source;

import magma.api.io.JavaPath;
import magma.api.result.Err;
import magma.api.result.Ok;
import magma.api.result.Result;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public record PathSources(JavaPath root) implements Sources {
    @Override
    public Result<Set<Source>, IOException> collect() {
        return switch (this.root.walk()) {
            case Ok(var sources) -> this.filter(sources);
            case Err(var error) -> new Err<>(error);
        };
    }

    private Result<Set<Source>, IOException> filter(Collection<JavaPath> sources) {
        return new Ok<>(sources.stream()
                .filter(JavaPath::isRegularFile)
                .filter(path -> path.asString()
                        .endsWith(".java"))
                .map(source -> new PathSource(this.root, source))
                .collect(Collectors.toSet()));
    }
}