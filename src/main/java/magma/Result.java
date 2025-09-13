package magma;

import java.util.Objects;
import java.util.Optional;

/**
 * Simple generic Result type: either Success with a value, or Error with an
 * error value.
 */
public final class Result<T, E> {
	private final T value;
	private final E error;

	private Result(T value, E error) {
		this.value = value;
		this.error = error;
	}

	public static <T, E> Result<T, E> success(T value) {
		return new Result<>(value, null);
	}

	public static <T, E> Result<T, E> error(E error) {
		return new Result<>(null, Objects.requireNonNull(error));
	}

	public boolean isSuccess() {
		return error == null;
	}

	public boolean isError() {
		return !isSuccess();
	}

	public Optional<T> getValue() {
		return Optional.ofNullable(value);
	}

	public Optional<E> getError() {
		return Optional.ofNullable(error);
	}
}
