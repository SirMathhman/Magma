package magma.api;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Result<T, E> {
    Optional<T> findValue();

    <R> Result<R, E> mapValue(Function<T, R> mapper);

    <R> Result<T, R> mapErr(Function<E, R> mapper);

    <R> Result<Tuple<T, R>, E> and(Supplier<Result<R, E>> mapper);

    <R> Result<R, E> flatMapValue(Function<T, Result<R, E>> mapper);

    boolean isOk();

    boolean isErr();

    Optional<E> findError();

    <R> R match(Function<T, R> onOk, Function<E, R> onErr);

    T $() throws UnsafeException;

    <R> Result<T, R> replaceErr(Supplier<R> supplier);
}
