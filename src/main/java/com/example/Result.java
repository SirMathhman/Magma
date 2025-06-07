package com.example;

/**
 * Simple result type for success or error with distinct variants.
 */
public interface Result<T> {
    boolean isOk();

    Option<T> value();

    Option<String> error();

    static <T> Result<T> ok(T value) {
        return new Ok<>(value);
    }

    static <T> Result<T> error(String message) {
        return new Err<>(message);
    }
}
