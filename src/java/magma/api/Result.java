package magma.api;

import java.util.Optional;
import java.util.function.Function;

public interface Result<T, X> {
    <R> Result<R, X> mapValue(Function<T, R> mapper);

    <R> Result<R, X> flatMap(Function<T, Result<R, X>> mapper);

    Optional<T> findValue();

    <R> Result<T, R> mapErr(Function<X, R> mapper);

    <R> R match(Function<T, R> whenOk, Function<X, R> whenErr);
}
