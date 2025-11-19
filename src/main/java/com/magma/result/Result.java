package com.magma.result;

/**
 * A sealed interface representing a result that can be either a success (Ok)
 * or an error (Err).
 *
 * @param <T> The type of the success value
 * @param <X> The type of the error value
 */
public sealed interface Result<T, X> permits Ok, Err {
}

