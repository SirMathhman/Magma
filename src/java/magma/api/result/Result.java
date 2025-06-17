package magma.api.result;

import java.util.function.Function;

public sealed interface Result<T, X> permits Ok, Err {
    <R> Result<R, X> flatMapValue(Function<T, Result<R, X>> mapper);
}
