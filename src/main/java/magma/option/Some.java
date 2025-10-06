package magma.option;

import java.util.function.Function;
import java.util.function.Supplier;

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
	public Option<T> or(Supplier<Option<T>> other) {
		return this;
	}

	@Override
	public T orElseGet(Supplier<T> other) {
		return value;
	}
}
