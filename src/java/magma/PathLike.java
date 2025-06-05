package magma;

import magma.option.Option;

import java.io.IOException;
import java.util.stream.Stream;
import magma.result.Result;

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
     * Returns a lazily populated stream of the files under this path wrapped in
     * a {@link Result}. The {@code Result} is {@code Err} when an I/O error
     * occurs.
     */
    Result<Stream<PathLike>, IOException> walk();

    boolean exists();

    /**
     * Lists the entries under this path. Errors are captured in the returned
     * {@link Result} instead of being thrown.
     */
    Result<Stream<PathLike>, IOException> list();

    /**
     * Reads the entire file content as a string. Any I/O failure is returned as
     * an {@code Err} value.
     */
    Result<String, IOException> readString();

    Stream<String> streamNames();

    boolean isRegularFile();
}
