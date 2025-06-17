package magma.api.result;

import java.util.function.Function;

public sealed interface Result<T, X> permits Ok, Err {
    <R> Result<R, X> mapValue(Function<T, R> mapper);

    <R> Result<R, X> flatMapValue(Function<T, Result<R, X>> mapper);

    <R> Result<T, R> mapErr(Function<X, R> mapper);
}
