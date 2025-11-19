package com.magma.result;

import java.util.function.Function;

/**
 * A sealed interface representing a result that can be either a success (Ok)
 * or an error (Err).
 *
 * @param <T> The type of the success value
 * @param <X> The type of the error value
 */
public sealed interface Result<T, X> permits Ok, Err {
    /**
     * Transforms the value in an Ok using the given function, or passes
     * through an Err unchanged.
     *
     * @param <U> The type of the transformed value
     * @param mapper The function to apply to the value
     * @return A new Result with the transformed value, or the same Err
     */
    <U> Result<U, X> map(Function<? super T, ? extends U> mapper);

    /**
     * Transforms the value in an Ok using a function that returns a Result,
     * or passes through an Err unchanged.
     *
     * @param <U> The type of the transformed value
     * @param mapper The function to apply to the value
     * @return The Result returned by the mapper, or the same Err
     */
    <U> Result<U, X> flatMap(
            Function<? super T, ? extends Result<U, X>> mapper);

    /**
     * Pattern matches on the Result, applying the appropriate function.
     *
     * @param <R> The return type
     * @param onOk The function to apply if this is an Ok
     * @param onErr The function to apply if this is an Err
     * @return The result of applying the appropriate function
     */
    <R> R match(Function<? super T, ? extends R> onOk,
                Function<? super X, ? extends R> onErr);
}

