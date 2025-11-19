package magma;

import magma.Result.Err;
import magma.Result.Ok;

import java.util.Optional;
import java.util.function.Function;

/**
 * Generic sealed result type with success and error variants.
 *
 * @param <T> success type
 * @param <X> error type
 */
public sealed interface Result<T, X> permits Ok, Err {
	/**
	 * Success variant wrapping a value of type T.
	 */
	record Ok<T, X>(T value) implements Result<T, X> {
		public boolean isOk() {
			return true;
		}

		@Override
		public Optional<T> asOk() {
			return Optional.of(this.value());
		}

		@Override
		public Optional<X> asErr() {
			return Optional.empty();
		}
	}

	/**
	 * Error variant wrapping an error value of type X.
	 */
	record Err<T, X>(X error) implements Result<T, X> {
		public boolean isErr() {
			return true;
		}

		@Override
		public Optional<T> asOk() {
			return Optional.empty();
		}

		@Override
		public Optional<X> asErr() {
			return Optional.of(this.error());
		}
	}

	default Optional<T> asOk() {
		return Optional.empty();
	}

	default Optional<X> asErr() {
		return Optional.empty();
	}

	@SuppressWarnings("unchecked")
	default <U> Result<U, X> mapValue(Function<? super T, ? extends U> mapper) {
		if (this instanceof Ok<T, X>) {
			return new Ok<U, X>(mapper.apply(this.asOk().get()));
		}
		return (Result<U, X>) this;
	}

	@SuppressWarnings("unchecked")
	default <U> Result<U, X> flatMap(Function<? super T, Result<U, X>> mapper) {
		if (this instanceof Ok<T, X>) {
			return mapper.apply(this.asOk().get());
		}
		return (Result<U, X>) this;
	}
}
