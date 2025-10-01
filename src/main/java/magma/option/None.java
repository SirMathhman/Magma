package magma.option;

import java.util.function.Function;

public final class None<T> implements Option<T> {
	@Override
	public <R> Option<R> map(Function<T, R> mapper) {
		return new None<>();
	}

	@Override
	public <R> Option<R> flatMap(Function<T, Option<R>> mapper) {
		return new None<>();
	}

	@Override
	public T orElse(T other) {
		return other;
	}
}
