package magma;

/**
 * Produces elements on demand for an {@link Iterator} implementation.
 */
interface Head<T> {
    Option<T> next();
}
