package magma.api;

import java.util.Optional;
import java.util.function.Function;

public interface Result<T, X> {
    <R> Result<R, X> map(Function<T, R> mapper);

    <R> Result<R, X> flatMap(Function<T, Result<R, X>> mapper);

    Optional<T> findValue();
}
