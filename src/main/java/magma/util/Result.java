package magma.util;

/**
 * A simple Result type to avoid throwing exceptions.
 * Use Result.Ok(value) for success and Result.Err(error) for failure.
 */
public sealed interface Result<T, E> permits Result.Ok, Result.Err {

	record Ok<T, E>(T value) implements Result<T, E> {
	}

	record Err<T, E>(E error) implements Result<T, E> {
	}

	default boolean isOk() {
		return this instanceof Ok;
	}

	default boolean isErr() {
		return this instanceof Err;
	}

	/**
	 * Return the success value or the supplied default if this is an Err.
	 */
	default T getOrElse(T other) {
		if (this instanceof Ok<T, E> o)
			return o.value();
		return other;
	}

	/**
	 * Return the error value or the supplied default if this is an Ok.
	 */
	default E getErrOrElse(E other) {
		if (this instanceof Err<T, E> e)
			return e.error();
		return other;
	}
}
