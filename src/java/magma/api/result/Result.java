package magma.api.result;

public sealed interface Result<T, X> permits Err, Ok {
}
