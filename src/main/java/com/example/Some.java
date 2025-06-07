package com.example;

/** Holds a present value. */
public final class Some<T> implements Option<T> {
    private final T value;

    Some(T value) {
        this.value = value;
    }

    @Override
    public boolean isSome() {
        return true;
    }

    @Override
    public T get() {
        return value;
    }
}
