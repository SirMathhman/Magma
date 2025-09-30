package magma;

import java.util.function.Function;

sealed interface Result<T, X> permits Main.Err, Main.Ok {
	<R> Result<R, X> map(Function<T, R> fn);

	<R> Result<R, X> flatMap(Function<T, Result<R, X>> fn);

}
