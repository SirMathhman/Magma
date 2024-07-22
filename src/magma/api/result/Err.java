package magma.api.result;

import magma.api.Tuple;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public record Err<T, E extends Exception>(E value) implements Result<T, E> {
    @Override
    public Optional<T> findValue() {
        return Optional.empty();
    }

    @Override
    public T $() throws E {
        throw value;
    }

    @Override
    public <R> Result<R, E> mapValue(Function<T, R> mapper) {
        return new Err<>(value);
    }

    @Override
    public <R> Result<R, E> flatMapValue(Function<T, Result<R, E>> mapper) {
        return new Err<>(value);
    }

    @Override
    public <R> Result<Tuple<T, R>, E> and(Supplier<Result<R, E>> supplier) {
        return new Err<>(value);
    }

    @Override
    public boolean isOk() {
        return false;
    }
}
