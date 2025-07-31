package magma;

/**
 * A generic Result interface representing either a successful operation (Ok) or an error (Err).
 * This is used in place of exceptions for error handling.
 *
 * @param <T> The type of the value in case of success
 * @param <E> The type of the error in case of failure
 */
public interface Result<T, E> {
    /**
     * Checks if this result is an Ok variant.
     *
     * @return true if this is an Ok result, false otherwise
     */
    boolean isOk();

    /**
     * Checks if this result is an Err variant.
     *
     * @return true if this is an Err result, false otherwise
     */
    boolean isErr();

    /**
     * Gets the value if this is an Ok result.
     *
     * @return the value
     * @throws IllegalStateException if this is an Err result
     */
    T getValue();

    /**
     * Gets the error if this is an Err result.
     *
     * @return the error
     * @throws IllegalStateException if this is an Ok result
     */
    E getError();

    /**
     * Creates a new Ok result with the given value.
     *
     * @param value the value
     * @param <T> the type of the value
     * @param <E> the type of the error
     * @return a new Ok result
     */
    static <T, E> Result<T, E> ok(T value) {
        return new Ok<>(value);
    }

    /**
     * Creates a new Err result with the given error.
     *
     * @param error the error
     * @param <T> the type of the value
     * @param <E> the type of the error
     * @return a new Err result
     */
    static <T, E> Result<T, E> err(E error) {
        return new Err<>(error);
    }

    /**
     * An Ok variant of Result representing a successful operation.
     *
     * @param <T> The type of the value
     * @param <E> The type of the error (unused in this variant)
     */
    final class Ok<T, E> implements Result<T, E> {
        private final T value;

        private Ok(T value) {
            this.value = value;
        }

        @Override
isOk() {
            return true;
        }

        @Override
isErr() {
            return false;
        }

        @Override
getValue() {
            return value;
        }

        @Override
getError() {
            throw new IllegalStateException("Cannot get error from Ok result");
        }
    }

    /**
     * An Err variant of Result representing a failed operation.
     *
     * @param <T> The type of the value (unused in this variant)
     * @param <E> The type of the error
     */
    final class Err<T, E> implements Result<T, E> {
        private final E error;

        private Err(E error) {
            this.error = error;
        }

        @Override
isOk() {
            return false;
        }

        @Override
isErr() {
            return true;
        }

        @Override
getValue() {
            throw new IllegalStateException("Cannot get value from Err result");
        }

        @Override
getError() {
            return error;
        }
    }
}