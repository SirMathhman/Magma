package magma.app;

import magma.api.Err;
import magma.api.Ok;
import magma.api.Result;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;

public record SingletonSourceSet(Path source) implements SourceSet {
    private Set<Unit> collectSources1() {
        return Files.exists(source)
                ? Collections.singleton(new PathUnit(source.getParent(), source))
                : Collections.<Unit>emptySet();
    }

    @Override
    public Result<Set<Unit>, IOException> collectSources() {
        return new Ok<>(collectSources1());
    }
}