package com.example.option;

/**
 * Minimal optional value container with distinct variants.
 */
public interface Option<T> {
    boolean isSome();

    T get();
}
