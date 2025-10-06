package magma.option;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public sealed interface Option<T> permits Some, None {
	static <T> Option<T> of(T value) {
		return new Some<T>(value);
	}

	static <T> Option<T> empty() {
		return new None<T>();
	}

	static <T> Option<T> ofNullable(T value) {
		if (Objects.isNull(value)) return new None<T>();
		return new Some<T>(value);
	}

	<R> Option<R> map(Function<T, R> mapper);

	<R> Option<R> flatMap(Function<T, Option<R>> mapper);

	T orElse(T other);

	Option<T> or(Supplier<Option<T>> other);

	T orElseGet(Supplier<T> other);

	Option<T> filter(Predicate<T> predicate);
}
