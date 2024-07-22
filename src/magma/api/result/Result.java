package magma.api.result;

import magma.api.Tuple;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Result<T, E extends Exception> {
    Optional<T> findValue();

    T $() throws E;

    <R> Result<R, E> mapValue(Function<T, R> mapper);

    <R> Result<R, E> flatMapValue(Function<T, Result<R, E>> mapper);

    <R> Result<Tuple<T, R>, E> and(Supplier<Result<R, E>> supplier);

    boolean isOk();
}
