package magma.result;

import java.util.function.Function;

sealed public interface Result<T, X> permits Err, Ok {
	<R> Result<R, X> mapValue(Function<T, R> fn);

	<R> Result<R, X> flatMap(Function<T, Result<R, X>> fn);

	<R> Result<T, R> mapErr(Function<X, R> mapper);
}
