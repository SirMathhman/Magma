package magma;

import java.util.function.Function;

interface Result<T, X> {
	<R> R match(Function<T, R> whenOk, Function<X, R> whenErr);
}
