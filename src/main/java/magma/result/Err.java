package magma.result;

import java.util.function.Function;

public record Err<T, X>(X error) implements Result<T, X> {
	@Override
	public <R> Result<R, X> mapValue(Function<T, R> fn) {
		return new Err<>(error);
	}

	@Override
	public <R> Result<R, X> flatMap(Function<T, Result<R, X>> fn) {
		return new Err<>(error);
	}

	@Override
	public <R> Result<T, R> mapErr(Function<X, R> mapper) {
		return new Err<>(mapper.apply(error));
	}
}
