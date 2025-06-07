package com.example;

/**
 * Simple result type for success or error with distinct variants.
 */
public interface Result<T> {
    boolean isOk();

    Option<T> value();

    Option<String> error();
}
