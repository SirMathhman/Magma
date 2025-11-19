package magma;

/**
 * Generic sealed result type with success and error variants.
 *
 * @param <T> success type
 * @param <X> error type
 */
public sealed interface Result<T, X> permits Result.Ok, Result.Err {
	/**
	 * Success variant wrapping a value of type T.
	 */
	public static final record Ok<T, X>(T value) implements Result<T, X> {
	}

	/**
	 * Error variant wrapping an error value of type X.
	 */
	public static final record Err<T, X>(X error) implements Result<T, X> {
	}
}
