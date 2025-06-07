package magma.path;

import java.util.Set;
import magma.result.Result;
import magma.option.Option;

/**
 * Minimal abstraction over file system paths. This wrapper lets the
 * rest of the code avoid a hard dependency on {@code java.nio.file.Path}.
 */
public interface PathLike {
    PathLike resolve(String other);
    PathLike relativize(PathLike other);
    PathLike getParent();
    Result<Set<PathLike>> walk();
    Result<String> readString();
    Option<String> createDirectories();
    Option<String> writeString(String text);
    Option<String> deleteIfExists();
    @Override String toString();
}
