package magma;

import java.util.function.Function;
import java.util.function.Supplier;

public class Lib {
	sealed interface Result<T, X> permits Err, Ok {}

	sealed public interface Optional<T> permits Some, None {
		static <T> Optional<T> empty() {
			return new None<T>();
		}

		static <T> Optional<T> of(T value) {
			return new Some<T>(value);
		}

		<R> Optional<R> map(Function<T, R> mapper);

		Optional<T> or(Supplier<Optional<T>> other);

		T orElseGet(Supplier<T> other);

		<R> Optional<R> flatMap(Function<T, Optional<R>> mapper);

		T orElse(T other);
	}

	public interface IOError {
		String display();
	}

	record Ok<T, X>(T value) implements Result<T, X> {}

	record Err<T, X>(X error) implements Result<T, X> {}

	record Some<T>(T value) implements Optional<T> {
		@Override
		public <R> Optional<R> map(Function<T, R> mapper) {
			return new Some<R>(mapper.apply(this.value));
		}

		@Override
		public Optional<T> or(Supplier<Optional<T>> other) {
			return this;
		}

		@Override
		public T orElseGet(Supplier<T> other) {
			return this.value;
		}

		@Override
		public <R> Optional<R> flatMap(Function<T, Optional<R>> mapper) {
			return mapper.apply(this.value);
		}

		@Override
		public T orElse(T other) {
			return this.value;
		}
	}

	private record None<T>() implements Optional<T> {
		@Override
		public <R> Optional<R> map(Function<T, R> mapper) {
			return new None<R>();
		}

		@Override
		public Optional<T> or(Supplier<Optional<T>> other) {
			return other.get();
		}

		@Override
		public T orElseGet(Supplier<T> other) {
			return other.get();
		}

		@Override
		public <R> Optional<R> flatMap(Function<T, Optional<R>> mapper) {
			return new None<R>();
		}

		@Override
		public T orElse(T other) {
			return other;
		}
	}
}
