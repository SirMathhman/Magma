package magma.option;

import java.util.function.Function;

public final class None<T> implements Optional<T> {
	@Override
	public <R> Optional<R> map(Function<T, R> mapper) {
		return new None<>();
	}
}
