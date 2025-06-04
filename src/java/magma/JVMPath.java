package magma;

/** Wrapper implementation backed by {@code java.nio.file.Path}. */
public record JVMPath(java.nio.file.Path path) implements PathLike {
    @Override
    public java.nio.file.Path unwrap() {
        return path;
    }

    @Override
    public String toString() {
        return path.toString();
    }
}
