package magma;

/**
 * A simple result type representing either a successful value or an error.
 *
 * @param <T> the successful value type
 * @param <X> the exception type, which must extend {@link Exception}
 */
public sealed interface Result<T, X extends Exception>
        permits Result.Ok, Result.Err {

    /** Result variant representing success. */
    final class Ok<T, X extends Exception> implements Result<T, X> {
        private final T value;

        public Ok(T value) {
            this.value = value;
        }

        public T value() {
            return value;
        }
    }

    /** Result variant representing failure. */
    final class Err<T, X extends Exception> implements Result<T, X> {
        private final X error;

        public Err(X error) {
            this.error = error;
        }

        public X error() {
            return error;
        }
    }

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
     * represents an error.
     */
    default T unwrap() throws X {
        if (this instanceof Ok<T, X> ok) {
            return ok.value();
        }
        throw ((Err<T, X>) this).error();
    }
}

