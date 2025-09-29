package magma.compiler;

import java.util.Optional;

public sealed interface Result<T, E> permits Ok, Err {
	/**
	 * Return the success value as an Optional. Prefer pattern matching on the
	 * sealed types rather than using this when possible.
	 */
	Optional<T> asOptional();

	/**
	 * Return the error as an Optional.
	 */
	Optional<E> asErrorOptional();

	static <T, E> Result<T, E> ok(T value) {
		return new Ok<>(value);
	}

	static <T, E> Result<T, E> err(E error) {
		return new Err<>(error);
	}
}
