package magma.api.result;

import magma.api.Tuple;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public record Err<T, E>(E value) implements Result<T, E> {
    @Override
    public <R> Result<Tuple<T, R>, E> and(Supplier<Result<R, E>> other) {
        return new Err<>(value);
    }

    @Override
    public <R> Result<R, E> mapValue(Function<T, R> mapper) {
        return new Err<>(value);
    }

    @Override
    public Optional<T> findValue() {
        return Optional.empty();
    }

    @Override
    public Optional<E> findError() {
        return Optional.of(value);
    }
}
