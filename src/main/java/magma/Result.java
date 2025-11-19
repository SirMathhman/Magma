package magma;

import magma.Result.Err;
import magma.Result.Ok;

import java.util.function.Function;

/**
 * Generic sealed result type with success and error variants.
 *
 * @param <T> success type
 * @param <X> error type
 */
public sealed interface Result<T, X> permits Ok, Err {
	/**
	 * Success variant wrapping a value of type T.
	 */
	record Ok<T, X>(T value) implements Result<T, X> {}

	/**
	 * Error variant wrapping an error value of type X.
	 */
	record Err<T, X>(X error) implements Result<T, X> {}

	@SuppressWarnings("unchecked")
	default <U> Result<U, X> map(Function<? super T, ? extends U> mapper) {
		if (this instanceof Ok<T, X>(T value)) {
			return new Ok<U, X>(mapper.apply(value));
		}
		return (Result<U, X>) this;
	}

	@SuppressWarnings("unchecked")
	default <U> Result<U, X> flatMap(Function<? super T, Result<U, X>> mapper) {
		if (this instanceof Ok<T, X>(T value)) {
			return mapper.apply(value);
		}
		return (Result<U, X>) this;
	}
}
