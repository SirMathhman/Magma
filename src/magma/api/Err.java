package magma.api;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class Err<T, E extends Exception> implements Result<T, E> {
    private final E error;

    public Err(E error) {
        this.error = error;
    }

    @Override
    public Optional<T> findValue() {
        return Optional.empty();
    }

    @Override
    public T $() throws E {
        throw error;
    }

    @Override
    public <R> Result<Tuple<T, R>, E> and(Supplier<Result<R, E>> other) {
        return new Err<>(error);
    }

    @Override
    public <R> Result<R, E> mapValue(Function<T, R> mapper) {
        return new Err<>(error);
    }

    @Override
    public <R extends Exception> Result<T, R> mapErr(Function<E, R> mapper) {
        return new Err<>(mapper.apply(error));
    }
}
