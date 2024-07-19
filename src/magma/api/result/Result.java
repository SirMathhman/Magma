package magma.api.result;

import magma.api.Tuple;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Result<T, E> {
    <R> Result<Tuple<T, R>, E> and(Supplier<Result<R, E>> other);

    <R> Result<R, E> mapValue(Function<T, R> mapper);

    Optional<T> findValue();

    Optional<E> findError();
}
