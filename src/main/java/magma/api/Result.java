package magma.api;

/**
 * Lightweight Result type: either Ok(value) or Err(error).
 *
 * @param <T> success type
 * @param <X> error type
 */
public sealed interface Result<T, X> permits Result.Ok, Result.Err {

    record Ok<T, X>(T value) implements Result<T, X> {
    }

    record Err<T, X>(X error) implements Result<T, X> {
    }

    static <T, X> Result<T, X> ok(T value) {
        return new Ok<>(value);
    }

    static <T, X> Result<T, X> err(X error) {
        return new Err<>(error);
    }

}
