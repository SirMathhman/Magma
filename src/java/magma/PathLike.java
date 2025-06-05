package magma;

import magma.option.Option;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Lightweight wrapper interface around {@code java.nio.file.Path}.
 */
public interface PathLike {
    /**
     * Creates a new wrapper from a string path.
     */
    static PathLike of(String first, String... more) {
        return new JVMPath(Path.of(first, more));
    }

    /**
     * Returns the wrapped {@link Path}.
     */
    Path unwrap();

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
    Stream<Path> walk() throws IOException;
}
