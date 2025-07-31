package magma.result;

/**
 * Represents a successful result containing a value of type T.
 *
 * @param <T> The type of the value
 * @param <X> The type of the error (not used in this variant)
 */
public record Ok<T, X>(T value) implements Result<T, X> {
	@Override
	public boolean isOk() {
		return true;
	}

	@Override
	public boolean isErr() {
		return false;
	}

	@Override
	public T unwrap() {
		return this.value;
	}

	@Override
	public X unwrapErr() {
		throw new IllegalStateException("Cannot unwrap error from Ok variant");
	}

	@Override
	public <U> Result<U, X> map(final java.util.function.Function<T, U> mapper) {
		return new Ok<>(mapper.apply(this.value));
	}

	@Override
	public <Y> Result<T, Y> mapErr(final java.util.function.Function<X, Y> mapper) {
		return new Ok<>(this.value);
	}

	@Override
	public <U> Result<U, X> flatMap(final java.util.function.Function<T, Result<U, X>> mapper) {
		return mapper.apply(this.value);
	}
}
