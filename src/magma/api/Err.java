package magma.api;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class Err<T, E> implements Result<T, E> {
    private final E error;

    public Err(E error) {
        this.error = error;
    }

    @Override
    public Optional<T> findValue() {
        return Optional.empty();
    }

    @Override
    public <R> Result<R, E> mapValue(Function<T, R> mapper) {
        return new Err<>(error);
    }

    @Override
    public <R> Result<T, R> mapErr(Function<E, R> mapper) {
        return new Err<>(mapper.apply(error));
    }

    @Override
    public <R> Result<Tuple<T, R>, E> and(Supplier<Result<R, E>> mapper) {
        return new Err<>(error);
    }

    @Override
    public <R> Result<R, E> flatMapValue(Function<T, Result<R, E>> mapper) {
        return new Err<>(error);
    }

    @Override
    public boolean isOk() {
        return false;
    }

    @Override
    public boolean isErr() {
        return true;
    }

    @Override
    public Optional<E> findError() {
        return Optional.of(error);
    }

    @Override
    public <R> R match(Function<T, R> onOk, Function<E, R> onErr) {
        return onErr.apply(error);
    }

    @Override
    public T $() throws UnsafeException {
        throw new UnsafeException(error);
    }

    @Override
    public <R> Result<T, R> replaceErr(Supplier<R> supplier) {
        return new Err<>(supplier.get());
    }
}
