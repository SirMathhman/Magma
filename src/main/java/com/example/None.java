package com.example;

/** Represents the absence of a value. */
public final class None<T> implements Option<T> {
    None() {
    }

    @Override
    public boolean isSome() {
        return false;
    }

    @Override
    public boolean isNone() {
        return true;
    }

    @Override
    public T get() {
        return null;
    }
}
