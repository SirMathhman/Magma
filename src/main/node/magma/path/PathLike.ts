/**
 * Minimal abstraction over file system paths. This wrapper lets the
 * rest of the code avoid a hard dependency on {@code java.nio.file.Path}.
 */
export interface PathLike {
    PathLike resolve(String other);
    PathLike relativize(PathLike other);
    PathLike getParent();
    @Override String toString();
}
