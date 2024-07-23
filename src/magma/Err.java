package magma;

import java.util.function.Function;

record Err<T, E extends Exception>(E value) implements Result<T, E> {
    @Override
    public <R> Result<R, E> mapValue(Function<T, R> mapper) {
        return new Err<>(value);
    }

    @Override
    public T unwrap() throws E {
        throw value;
    }
}
