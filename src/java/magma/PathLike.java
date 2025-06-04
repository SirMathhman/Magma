package magma;

/** Lightweight wrapper interface around {@code java.nio.file.Path}. */
public interface PathLike {
    /** Returns the wrapped {@link java.nio.file.Path}. */
    java.nio.file.Path unwrap();

    /**
     * Resolves {@code other} against this path.
     */
    default PathLike resolve(String other) {
        return new JVMPath(unwrap().resolve(other));
    }

    /** Resolves another {@code PathLike}. */
    default PathLike resolve(PathLike other) {
        return new JVMPath(unwrap().resolve(other.unwrap()));
    }

    /** Returns the parent of this path. */
    default PathLike getParent() {
        java.nio.file.Path parent = unwrap().getParent();
        return new JVMPath(parent);
    }

    /** Returns a relative path from this path to {@code other}. */
    default PathLike relativize(PathLike other) {
        return new JVMPath(unwrap().relativize(other.unwrap()));
    }

    /** Creates a new wrapper from a string path. */
    static PathLike of(String first, String... more) {
        return new JVMPath(java.nio.file.Path.of(first, more));
    }

}
