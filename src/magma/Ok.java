package magma;

import java.util.function.Function;
import java.util.function.Supplier;

public record Ok<T, E extends Exception>(T value) implements Result<T, E> {
    @Override
    public T unwrap() {
        return value;
    }

    @Override
    public <R> Result<Tuple<T, R>, E> and(Supplier<Result<R, E>> mapper) {
        return mapper.get().mapValue(inner -> new Tuple<>(value, inner));
    }

    @Override
    public <R> Result<R, E> mapValue(Function<T, R> mapper) {
        return new Ok<>(mapper.apply(value));
    }
}
