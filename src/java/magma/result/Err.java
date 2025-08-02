package magma.result;

import java.util.function.Function;

public record Err<T, X>(X error) implements Result<T, X> {
	@Override
	public <R> R match(final Function<T, R> whenOk, final Function<X, R> whenErr) {
		return whenErr.apply(this.error);
	}
}
