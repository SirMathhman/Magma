package com.meti;

public interface Option<T> {
    <R, E extends Exception> Option<R> map(F1<T, R, E> mapper) throws E;

    T orElse(T other);

    boolean isPresent();
}