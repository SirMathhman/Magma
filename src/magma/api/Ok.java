package magma.api;

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
    public T $() throws E {
        return value;
    }

    @Override
    public <R> Result<Tuple<T, R>, E> and(Supplier<Result<R, E>> other) {
        return other.get().mapValue(otherValue -> new Tuple<>(value, otherValue));
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
    public <R> Result<R, E> flatMapValue(Function<T, Result<R, E>> mapper) {
        return mapper.apply(value);
    }

    @Override
    public boolean isOk() {
        return true;
    }
}