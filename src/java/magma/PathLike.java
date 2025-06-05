package magma;

import magma.option.Option;

import java.io.IOException;
import java.util.stream.Stream;

/**
 * Lightweight wrapper interface around {@code java.nio.file.Path}.
 */
public interface PathLike {
    /**
     * Resolves {@code other} against this path.
     */
    PathLike resolve(String other);

    /**
     * Resolves another {@code PathLike}.
     */
    PathLike resolve(PathLike other);

    /**
     * Returns the parent of this path.
     */
    PathLike getParent();

    /**
     * Returns a relative path from this path to {@code other}.
     */
    PathLike relativize(PathLike other);

    /**
     * Writes {@code content} to this path, returning any I/O error wrapped
     * in {@link Option}.
     */
    Option<IOException> writeString(String content);

    /**
     * Creates the directories represented by this path if necessary.
     */
    Option<IOException> createDirectories();

    /**
     * Returns a lazily populated stream of the files under this path.
     */
    Stream<PathLike> walk() throws IOException;

    boolean exists();

    Stream<PathLike> list() throws IOException;

    String readString() throws IOException;

    Stream<String> streamNames();

    boolean isRegularFile();
}
