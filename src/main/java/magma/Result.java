package magma;

import java.util.Optional;

/**
 * Simple generic Result type: either Ok with a value, or Err with an error
 * value.
 */
public sealed interface Result<T, E> permits Result.Ok, Result.Err {
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
