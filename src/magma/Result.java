package magma;

/**
 * A simple result type representing either a successful value or an error.
 * It is meant for situations where a returned value is meaningful. If the
 * success case carries no value (for example {@code Result<Void, X>}), prefer
 * using {@link java.util.Optional Optional}&lt;X&gt; instead.
 *
 * @param <T> the successful value type
 * @param <X> the exception type, which must extend {@link Exception}
 */
public sealed interface Result<T, X extends Exception>
        permits Ok, Err {



    /** Convenience method to check if this result is successful. */
    boolean isOk();

    /** Convenience method to check if this result is an error. */
    boolean isErr();

    /**
     * Transforms the successful value using {@code mapper}. If this result is
     * an error, the same error is returned unchanged.
     */
    <U> Result<U, X> mapValue(java.util.function.Function<? super T, ? extends U> mapper);

    /**
     * Transforms the successful value using a function that itself returns a
     * {@code Result}. This allows for chaining operations without explicit
     * casts.
     */
    <U> Result<U, X> flatMapValue(java.util.function.Function<? super T, Result<U, X>> mapper);

    /**
     * Gets the successful value or throws the stored exception if this result
     * represents an error. Most code should avoid calling this method and
     * handle both cases explicitly.
     */
    T unwrap() throws X;
}

