package magma.path;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;

import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

/**
 * Implementation of {@link PathLike} that delegates to a
 * {@link Path} instance.
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
        var parent = path.getParent();
        return parent == null ? null : new NioPath(parent);
    }

    @Override
    public Result<Set<PathLike>> walk() {
        Set<PathLike> out = new LinkedHashSet<>();
        try (var stream = Files.walk(path)) {
            stream.forEach(p -> out.add(new NioPath(p)));
            return new Ok<>(out);
        } catch (IOException e) {
            return new Err<>(e.getMessage());
        }
    }

    @Override
    public String toString() {
        return path.toString();
    }
}
