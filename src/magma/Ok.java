package magma;

import java.util.function.Function;

record Ok<T, E extends Exception>(T value) implements Result<T, E> {
    @Override
    public <R> Result<R, E> mapValue(Function<T, R> mapper) {
        return new Ok<>(mapper.apply(value));
    }

    @Override
    public T unwrap() throws E {
        return value;
    }
}
