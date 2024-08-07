package magma.app;

import magma.api.Err;
import magma.api.Ok;
import magma.api.Result;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

public final class SingletonSourceSet implements SourceSet {
    private final Path source;

    public SingletonSourceSet(Path source) {
        this.source = source;
    }

    private Set<Unit> collectSources1() {
        return Files.exists(source)
                ? Collections.singleton(new PathUnit(source.getParent(), source))
                : Collections.<Unit>emptySet();
    }

    @Override
    public Result<Set<Unit>, IOException> collectSources() {
        return new Ok<>(collectSources1());
    }

    public Path source() {
        return source;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (SingletonSourceSet) obj;
        return Objects.equals(this.source, that.source);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source);
    }

    @Override
    public String toString() {
        return "SingletonSourceSet[" +
               "source=" + source + ']';
    }

}