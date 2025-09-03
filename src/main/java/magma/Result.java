package magma;

/**
 * Generic Result type with Ok and Err variants.
 */
public sealed interface Result<T, E> permits Ok, Err {
}
