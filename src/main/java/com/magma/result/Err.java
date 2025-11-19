package com.magma.result;

import java.util.function.Function;

/**
 * Represents an error result containing an error value.
 *
 * @param <T> The type of the success value (unused in Err)
 * @param <X> The type of the error value
 */
public final class Err<T, X> implements Result<T, X> {
    /**
     * The error value.
     */
    private final X error;

    /**
     * Creates a new Err instance with the given error value.
     *
     * @param errorParam The error value
     */
    public Err(final X errorParam) {
        this.error = errorParam;
    }

    /**
     * Gets the error value.
     *
     * @return The error value
     */
    public X getError() {
        return error;
    }

    @Override
    public <U> Result<U, X> map(
            final Function<? super T, ? extends U> mapper) {
        return new Err<>(error);
    }

    @Override
    public <U> Result<U, X> flatMap(
            final Function<? super T, ? extends Result<U, X>> mapper) {
        return new Err<>(error);
    }

    @Override
    public <R> R match(final Function<? super T, ? extends R> onOk,
            final Function<? super X, ? extends R> onErr) {
        return onErr.apply(error);
    }
}
