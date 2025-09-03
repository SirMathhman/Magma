package magma;

// Sealed Result type for explicit success/error handling
public sealed interface Result<T, E> permits Ok, Err {
}
