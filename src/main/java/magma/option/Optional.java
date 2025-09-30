package magma.option;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public sealed interface Optional<T> permits Some, None {
	static <T> Optional<T> of(T value) {
		return new Some<>(value);
	}

	static <T> Optional<T> empty() {
		return new None<>();
	}

	static <T> Optional<T> ofNullable(T value) {
		return value == null ? new None<>() : new Some<>(value);
	}

	void ifPresent(Consumer<T> consumer);

	Optional<T> or(Supplier<Optional<T>> other);

	T orElse(T other);

	<R> Optional<R> map(Function<T, R> mapper);

	T orElseGet(Supplier<T> supplier);
}
