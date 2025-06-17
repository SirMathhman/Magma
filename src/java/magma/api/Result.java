package magma.api;

public sealed interface Result<T, X> permits Err, Ok {
}
