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


    /** Factory method for creating a successful result. */
    static <T, X extends Exception> Result<T, X> ok(T value) {
        return new Ok<>(value);
    }

    /** Factory method for creating an error result. */
    static <T, X extends Exception> Result<T, X> err(X error) {
        return new Err<>(error);
    }

    /** Convenience method to check if this result is successful. */
    default boolean isOk() {
        return this instanceof Ok;
    }

    /** Convenience method to check if this result is an error. */
    default boolean isErr() {
        return this instanceof Err;
    }

    /**
     * Gets the successful value or throws the stored exception if this result
     * represents an error. Most code should avoid calling this method and
     * handle both cases explicitly.
     */
    default T unwrap() throws X {
        if (this instanceof Ok<T, X> ok) {
            return ok.value();
        }
        throw ((Err<T, X>) this).error();
    }
}

