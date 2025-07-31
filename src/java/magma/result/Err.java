package magma.result;

import magma.Tuple;

import java.util.function.Supplier;

/**
 * Represents a failed result containing an error of type X.
 *
 * @param <T> The type of the value (not used in this variant)
 * @param <X> The type of the error
 */
public record Err<T, X>(X error) implements Result<T, X> {
	@Override
	public boolean isOk() {
		return false;
	}

	@Override
	public boolean isErr() {
		return true;
	}

	@Override
	public T unwrap() {
		throw new IllegalStateException("Cannot unwrap value from Err variant");
	}

	@Override
	public X unwrapErr() {
		return this.error;
	}

	@Override
	public <U> Result<U, X> map(final java.util.function.Function<T, U> mapper) {
		return new Err<>(this.error);
	}

	@Override
	public <Y> Result<T, Y> mapErr(final java.util.function.Function<X, Y> mapper) {
		return new Err<>(mapper.apply(this.error));
	}

	@Override
	public <U> Result<U, X> flatMap(final java.util.function.Function<T, Result<U, X>> mapper) {
		return new Err<>(this.error);
	}

	@Override
	public <R> R match(final java.util.function.Function<T, R> okMapper,
										 final java.util.function.Function<X, R> errMapper) {
		return errMapper.apply(this.error);
	}

	@Override
	public <R> Result<Tuple<T, R>, X> and(final Supplier<Result<R, X>> other) {
		return new Err<>(this.error);
	}
}
