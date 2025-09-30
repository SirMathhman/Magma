/*

sealed public interface Result<T, X> permits Err, Ok {
	<R> Result<R, X> map(Function<T, R> fn);

	<R> Result<R, X> flatMap(Function<T, Result<R, X>> fn);
}*//*
*/