package magma.option;

import magma.Tuple;

import java.util.function.Function;

public record Some<T>(T value) implements Option<T> {
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
	public Tuple<Boolean, T> toTuple(T other) {
		return new Tuple<>(true, value);
	}
}
