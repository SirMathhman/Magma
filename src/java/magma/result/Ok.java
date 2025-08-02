package magma.result;

import java.util.function.Function;

public record Ok<T, X>(T value) implements Result<T, X> {
	@Override
	public <R> R match(final Function<T, R> whenOk, final Function<X, R> whenErr) {
		return whenOk.apply(this.value);
	}
}
