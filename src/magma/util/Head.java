package magma.util;

/**
 * Produces elements on demand for an {@link Iterator} implementation.
 */
public interface Head<T> {
    Option<T> next();
}
