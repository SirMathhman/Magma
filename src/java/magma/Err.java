package magma;

import java.util.function.Function;

record Err<T, X>(X error) implements Result<T, X> {
	@Override
	public <R> R match(final Function<T, R> whenOk, final Function<X, R> whenErr) {
		return whenErr.apply(this.error);
	}
}
