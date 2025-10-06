package magma.option;

import java.util.function.Function;
import java.util.function.Supplier;

public final class None<T> implements Option<T> {
	@Override
	public <R> Option<R> map(Function<T, R> mapper) {
		return new None<R>();
	}

	@Override
	public <R> Option<R> flatMap(Function<T, Option<R>> mapper) {
		return new None<R>();
	}

	@Override
	public T orElse(T other) {
		return other;
	}

	@Override
	public Option<T> or(Supplier<Option<T>> other) {
		return other.get();
	}

	@Override
	public T orElseGet(Supplier<T> other) {
		return other.get();
	}
}
