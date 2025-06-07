package magma.path;

import java.nio.file.Path;

/**
 * Implementation of {@link PathLike} that delegates to a
 * {@link java.nio.file.Path} instance.
 */
public class NioPath implements PathLike {
    private final Path path;

    private NioPath(Path path) {
        this.path = path;
    }

    /** Create a wrapper from path segments. */
    public static NioPath of(String first) {
        return new NioPath(Path.of(first));
    }

    /** Wrap an existing NIO path. */
    public static NioPath wrap(Path path) {
        return new NioPath(path);
    }

    public Path toNio() {
        return path;
    }

    @Override
    public PathLike resolve(String other) {
        return new NioPath(path.resolve(other));
    }

    @Override
    public PathLike relativize(PathLike other) {
        return new NioPath(path.relativize(((NioPath) other).path));
    }

    @Override
    public PathLike getParent() {
        Path parent = path.getParent();
        return parent == null ? null : new NioPath(parent);
    }

    @Override
    public String toString() {
        return path.toString();
    }
}
