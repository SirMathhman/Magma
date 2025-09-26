package magma.api;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Lightweight Option type: either Ok(value) or Err (absent).
 *
 * @param <T> contained type
 */
public sealed interface Option<T> permits Option.Some, Option.None {
	record Some<T>(T value) implements Option<T> {
		@Override
		public boolean isEmpty() {
			return false;
		}

		@Override
		public boolean isPresent() {
			return true;
		}

		@Override
		public T get() {
			return value;
		}

		@Override
		public void ifPresent(Consumer<T> consumer) {
			consumer.accept(value);
		}

		@Override
		public <U> Option<U> map(Function<T, U> mapper) {
			return Option.of(mapper.apply(value));
		}

		@Override
		public T orElseThrow() {
			return value;
		}

		@Override
		public T orElse(T other) {
			return value;
		}
	}

	record None<T>() implements Option<T> {
		@Override
		public boolean isEmpty() {
			return true;
		}

		@Override
		public boolean isPresent() {
			return false;
		}

		@Override
		public T get() {
			throw new RuntimeException("Cannot get value from None");
		}

		@Override
		public void ifPresent(Consumer<T> consumer) {
			// Do nothing
		}

		@Override
		public <U> Option<U> map(Function<T, U> mapper) {
			return Option.empty();
		}

		@Override
		public T orElseThrow() {
			throw new RuntimeException("Cannot get value from None");
		}

		@Override
		public T orElse(T other) {
			return other;
		}
	}

	boolean isEmpty();

	boolean isPresent();

	T get();

	void ifPresent(Consumer<T> consumer);

	<U> Option<U> map(Function<T, U> mapper);

	T orElseThrow();

	T orElse(T other);

	static <T> Option<T> of(T value) {
		return new Some<>(value);
	}

	static <T> Option<T> empty() {
		return new None<>();
	}

	static <T> Option<T> some(T value) {
		return new Some<>(value);
	}

	static <T> Option<T> none() {
		return new None<>();
	}
}
