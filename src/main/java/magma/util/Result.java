package magma.util;

public sealed interface Result<T, X> permits Ok, Err {
}
