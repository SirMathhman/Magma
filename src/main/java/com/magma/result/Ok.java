package com.magma.result;

/**
 * Represents a successful result containing a value.
 *
 * @param <T> The type of the success value
 * @param <X> The type of the error value (unused in Ok)
 */
public final class Ok<T, X> implements Result<T, X> {
    /**
     * The success value.
     */
    private final T value;

    /**
     * Creates a new Ok instance with the given value.
     *
     * @param valueParam The success value
     */
    public Ok(final T valueParam) {
        this.value = valueParam;
    }

    /**
     * Gets the success value.
     *
     * @return The success value
     */
    public T getValue() {
        return value;
    }
}

