package magma.option;

import java.util.Objects;
import java.util.function.Function;

public sealed interface Optional<T> permits Some, None {
	static <T> Optional<T> of(T value) {
		return new Some<>(value);
	}

	static <T> Optional<T> empty() {
		return new None<>();
	}

	static <T> Optional<T> ofNullable(T value) {
		return Objects.isNull(value) ? new None<>() : new Some<>(value);
	}

	<R> Optional<R> map(Function<T, R> mapper);
}
