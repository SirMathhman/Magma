package com.example.option;

/** Represents the absence of a value. */
public final class None<T> implements Option<T> {
    public None() {
    }

    @Override
    public boolean isSome() {
        return false;
    }

    @Override
    public T get() {
        return null;
    }
}
