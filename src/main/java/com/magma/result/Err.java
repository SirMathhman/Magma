package com.magma.result;

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
}

