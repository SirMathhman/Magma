package magma.option;

import java.util.function.Function;

public final class None<T> implements Option<T> {
	@Override
	public <R> Option<R> map(Function<T, R> mapper) {
		return new None<>();
	}
}
