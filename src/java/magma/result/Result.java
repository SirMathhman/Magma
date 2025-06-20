package magma.result;

import java.util.function.Function;

/**
 * A simple result type representing either a successful value or an error.
 * It is meant for situations where a returned value is meaningful. If there is
 * no value to return on success, prefer using {@link magma.option.Option
 * Option} or another type instead of {@code Result}.
 *
 * @param <T> the successful value type
 * @param <X> the error type
 */
public interface Result<T, X> {
    /**
     * Convenience method to check if this result is successful.
     */
    boolean isOk();

    /**
     * Convenience method to check if this result is an error.
     */
    boolean isErr();

    /**
     * Transforms the successful value using {@code mapper}. If this result is
     * an error, the same error is returned unchanged.
     */
    <U> Result<U, X> mapValue(Function<? super T, ? extends U> mapper);

    /**
     * Transforms the successful value using a function that itself returns a
     * {@code Result}. This allows for chaining operations without explicit
     * casts.
     */
    <U> Result<U, X> flatMapValue(Function<? super T, Result<U, X>> mapper);

    <R> R match(Function<T, R> whenOk, Function<X, R> whenErr);
}

