package magma.option;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public sealed interface Option<T> permits Option.Some, Option.None {
	static <T> Option<T> of(T value) {
		return new Some<>(value);
	}

	static <T> Option<T> empty() {
		return new None<>();
	}

	static <T> Option<T> ofNullable(T value) {
		if (Objects.isNull(value)) return new None<>();
		return new Some<>(value);
	}

	<R> Option<R> map(Function<T, R> mapper);

	<R> Option<R> flatMap(Function<T, Option<R>> mapper);

	T orElse(T other);

	Option<T> or(Supplier<Option<T>> other);

	T orElseGet(Supplier<T> other);

	final class None<T> implements Option<T> {
		@Override
		public <R> Option<R> map(Function<T, R> mapper) {
			return new None<>();
		}

		@Override
		public <R> Option<R> flatMap(Function<T, Option<R>> mapper) {
			return new None<>();
		}

		@Override
		public T orElse(T other) {
			return other;
		}

		@Override
		public Option<T> or(Supplier<Option<T>> other) {
			return other.get();
		}

		@Override
		public T orElseGet(Supplier<T> other) {
			return other.get();
		}
	}

	record Some<T>(T value) implements Option<T> {
		@Override
		public <R> Option<R> map(Function<T, R> mapper) {
			return new Some<>(mapper.apply(value));
		}

		@Override
		public <R> Option<R> flatMap(Function<T, Option<R>> mapper) {
			return mapper.apply(value);
		}

		@Override
		public T orElse(T other) {
			return value;
		}

		@Override
		public Option<T> or(Supplier<Option<T>> other) {
			return this;
		}

		@Override
		public T orElseGet(Supplier<T> other) {
			return value;
		}
	}
}
