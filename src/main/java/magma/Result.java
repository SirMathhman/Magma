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

	@SuppressWarnings("unchecked")
	default <U> Result<U, X> map(java.util.function.Function<? super T, ? extends U> mapper) {
		if (this instanceof Result.Ok<T, X> ok) {
			return new Result.Ok<>(mapper.apply(ok.value()));
		}
		return (Result<U, X>) this;
	}

	@SuppressWarnings("unchecked")
	default <U> Result<U, X> flatMap(java.util.function.Function<? super T, Result<U, X>> mapper) {
		if (this instanceof Result.Ok<T, X> ok) {
			return mapper.apply(ok.value());
		}
		return (Result<U, X>) this;
	}
}
