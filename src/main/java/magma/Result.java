package magma;

import java.util.Objects;
import java.util.Optional;

/**
 * Simple generic Result type: either Ok with a value, or Err with an error
 * value.
 */
public sealed interface Result<T, E> permits Result.Ok, Result.Err {
	static <T, E> Result<T, E> success(T value) {
		return new Ok<>(value);
	}

	static <T, E> Result<T, E> error(E error) {
		return new Err<>(Objects.requireNonNull(error));
	}

	default boolean isSuccess() {
		return this instanceof Ok;
	}

	default boolean isError() {
		return this instanceof Err;
	}

	@SuppressWarnings("unchecked")
	default Optional<T> getValue() {
		if (this instanceof Ok<T, E> o) {
			return Optional.ofNullable(o.value());
		}
		return Optional.empty();
	}

	@SuppressWarnings("unchecked")
	default Optional<E> getError() {
		if (this instanceof Err<T, E> e) {
			return Optional.of(e.error());
		}
		return Optional.empty();
	}

	record Ok<T, E>(T value) implements Result<T, E> {
	}

	record Err<T, E>(E error) implements Result<T, E> {
	}
}
