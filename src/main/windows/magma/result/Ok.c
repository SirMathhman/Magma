/*

public record Ok<T, X>(T value) implements Result<T, X> {
	@Override
	public <R> Result<R, X> map(Function<T, R> fn) {
		return new Ok<>(fn.apply(this.value));
	}

	@Override
	public <R> Result<R, X> flatMap(Function<T, Result<R, X>> fn) {
		return fn.apply(this.value);
	}
}*//*
*/