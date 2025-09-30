package magma.option;

import java.util.Objects;
import java.util.function.Function;

public sealed interface Option<T> permits Some, None {
	static <T> Option<T> of(T value) {
		return new Some<>(value);
	}

	static <T> Option<T> empty() {
		return new None<>();
	}

	static <T> Option<T> ofNullable(T value) {
		return Objects.isNull(value) ? new None<>() : new Some<>(value);
	}

	<R> Option<R> map(Function<T, R> mapper);
}
