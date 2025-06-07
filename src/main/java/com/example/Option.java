package com.example;

/**
 * Minimal optional value container.
 */
public final class Option<T> {
    private final T value;

    private Option(T value) {
        this.value = value;
    }

    public static <T> Option<T> some(T value) {
        return new Option<>(value);
    }

    public static <T> Option<T> none() {
        return new Option<>(null);
    }

    public boolean isSome() {
        return value != null;
    }

    public boolean isNone() {
        return value == null;
    }

    public T get() {
        return value;
    }
}
