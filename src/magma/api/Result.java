package magma.api;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Result<T, E extends Exception> {
    Optional<T> findValue();

    T $() throws E;

    <R> Result<Tuple<T, R>, E> and(Supplier<Result<R, E>> other);

    <R> Result<R, E> mapValue(Function<T, R> mapper);

    <R extends Exception> Result<T, R> mapErr(Function<E, R> mapper);

    <R> Result<R,E> flatMapValue(Function<T, Result<R, E>> mapper);

    boolean isOk();

    boolean isErr();

    <R> R match(Function<T, R> onOk, Function<E, R> onErr);

    Optional<E> findErr();
}
