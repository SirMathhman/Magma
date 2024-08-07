package magma.app;

import magma.api.Err;
import magma.api.Ok;
import magma.api.Result;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class PathUnit implements Unit {
    private final Path root;
    private final Path child;

    public PathUnit(Path root, Path child) {
        this.root = root;
        this.child = child;
    }

    @Override
    public Result<String, IOException> read() {
        try {
            return new Ok<>(Files.readString(child));
        } catch (IOException e) {
            return Err.Err(e);
        }
    }

    @Override
    public String computeName() {
        var fileName = child.getFileName().toString();
        var separator = fileName.lastIndexOf('.');
        return fileName.substring(0, separator);
    }

    @Override
    public Stream<String> computeNamespace() {
        var relativized = root.relativize(child.getParent());
        return IntStream.range(0, relativized.getNameCount())
                .mapToObj(relativized::getName)
                .map(Path::toString);
    }

    @Override
    public String format() {
        return child.toString();
    }

    public Path root() {
        return root;
    }

    public Path child() {
        return child;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (PathUnit) obj;
        return Objects.equals(this.root, that.root) &&
               Objects.equals(this.child, that.child);
    }

    @Override
    public int hashCode() {
        return Objects.hash(root, child);
    }

    @Override
    public String toString() {
        return "PathUnit[" +
               "root=" + root + ", " +
               "child=" + child + ']';
    }

}