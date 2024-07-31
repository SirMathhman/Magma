package magma;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class Ok<T, E extends Exception> implements Result<T, E> {
    private final T value;

    public Ok(T value) {
        this.value = value;
    }

    @Override
    public Optional<T> findValue() {
        return Optional.of(value);
    }

    @Override
    public T $() {
        return value;
    }

    @Override
    public <R> Result<R, E> mapValue(Function<T, R> mapper) {
        return new Ok<>(mapper.apply(value));
    }

    @Override
    public <R extends Exception> Result<T, R> mapErr(Function<E, R> mapper) {
        return new Ok<>(value);
    }

    @Override
    public <R> Result<Tuple<T, R>, E> and(Supplier<Result<R, E>> mapper) {
        return mapper.get().mapValue(inner -> new Tuple<>(value, inner));
    }
}
