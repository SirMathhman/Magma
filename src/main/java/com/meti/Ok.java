package com.meti;

import java.util.function.Function;

record Ok<T, E>(T inner) implements Result<T, E> {
    @Override
    public Option<T> value() {
        return Some.apply(inner);
    }

    @Override
    public Option<E> err() {
        return new None<>();
    }

    @Override
    public <R> Result<R, E> mapValue(Function<T, R> mapper) {
        return new Ok<>(mapper.apply(inner));
    }
}
