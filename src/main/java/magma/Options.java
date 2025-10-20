package magma;

import java.util.function.Function;
import java.util.function.Supplier;

public class Options {
	sealed public interface Option<T> permits Some, None {
		<R> Option<R> map(Function<T, R> mapper);

		Option<T> or(Supplier<Option<T>> other);

		T orElseGet(Supplier<T> other);

		<R> Option<R> flatMap(Function<T, Option<R>> mapper);

		T orElse(T other);
	}

	record Some<T>(T value) implements Option<T> {
		@Override
		public <R> Option<R> map(Function<T, R> mapper) {
			return new Some<R>(mapper.apply(this.value));
		}

		@Override
		public Option<T> or(Supplier<Option<T>> other) {
			return this;
		}

		@Override
		public T orElseGet(Supplier<T> other) {
			return this.value;
		}

		@Override
		public <R> Option<R> flatMap(Function<T, Option<R>> mapper) {
			return mapper.apply(this.value);
		}

		@Override
		public T orElse(T other) {
			return this.value;
		}
	}

	public record None<T>() implements Option<T> {
		@Override
		public <R> Option<R> map(Function<T, R> mapper) {
			return new None<R>();
		}

		@Override
		public Option<T> or(Supplier<Option<T>> other) {
			return other.get();
		}

		@Override
		public T orElseGet(Supplier<T> other) {
			return other.get();
		}

		@Override
		public <R> Option<R> flatMap(Function<T, Option<R>> mapper) {
			return new None<R>();
		}

		@Override
		public T orElse(T other) {
			return other;
		}
	}
}
