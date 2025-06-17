package magma.api.result;

import java.util.function.Function;

public interface Result<T, X> extends Matchable<T, X> {
    <R> Result<R, X> mapValue(Function<T, R> mapper);

    <R> Result<R, X> flatMapValue(Function<T, Result<R, X>> mapper);

    <R> Result<T, R> mapErr(Function<X, R> mapper);
}
