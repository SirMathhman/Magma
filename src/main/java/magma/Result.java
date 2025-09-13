package magma;

import java.util.Objects;
import java.util.Optional;

/**
 * Simple generic Result type: either Success with a value, or Error with an
 * error value.
 */
public final class Result<T, E> {
	private final Optional<T> value;
	private final Optional<E> error;

	private Result(Optional<T> value, Optional<E> error) {
		this.value = value;
		this.error = error;
	}

	public static <T, E> Result<T, E> success(T value) {
		return new Result<>(Optional.ofNullable(value), Optional.empty());
	}

	public static <T, E> Result<T, E> error(E error) {
		return new Result<>(Optional.empty(), Optional.of(Objects.requireNonNull(error)));
	}

	public boolean isSuccess() {
		return error.isEmpty();
	}

	public boolean isError() {
		return error.isPresent();
	}

	public Optional<T> getValue() {
		return value;
	}

	public Optional<E> getError() {
		return error;
	}
}
