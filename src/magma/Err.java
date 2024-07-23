package magma;

import java.util.function.Function;
import java.util.function.Supplier;

public record Err<T, E extends Exception>(E value) implements Result<T, E> {
    @Override
    public T unwrap() throws E {
        throw value;
    }

    @Override
    public <R> Result<Tuple<T, R>, E> and(Supplier<Result<R, E>> mapper) {
        return new Err<>(value);
    }

    @Override
    public <R> Result<R, E> mapValue(Function<T, R> mapper) {
        return new Err<>(value);
    }
}
