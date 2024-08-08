package magma.app;

import magma.api.Ok;
import magma.api.Result;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;

public final class SingletonSourceSet implements SourceSet {
    private final Path source;

    public SingletonSourceSet(Path source) {
        this.source = source;
    }

    private Set<Unit> collectSources1() {
        return Files.exists(source)
                ? Collections.singleton(new PathUnit(source.getParent(), source))
                : Collections.emptySet();
    }

    @Override
    public Result<Set<Unit>, IOException> collectSources() {
        return new Ok<>(collectSources1());
    }
}