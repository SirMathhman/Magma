package magma.result;

/**
 * A result type that represents either success (Ok) with a value of type T
 * or failure (Err) with an error value of type X.
 *
 * @param <T> The type of the value in case of success
 * @param <X> The type of the error in case of failure
 */
public sealed interface Result<T, X> permits Ok, Err {

	/**
	 * Returns true if this is an Ok variant.
	 */
	boolean isOk();

	/**
	 * Returns true if this is an Err variant.
	 */
	boolean isErr();

	/**
	 * Unwraps the value from an Ok variant.
	 *
	 * @throws IllegalStateException if this is an Err variant
	 */
	T unwrap();

	/**
	 * Unwraps the error from an Err variant.
	 *
	 * @throws IllegalStateException if this is an Ok variant
	 */
	X unwrapErr();

	/**
	 * Maps the value inside an Ok variant using the provided function.
	 * If this is an Err variant, the error is propagated.
	 */
	<U> Result<U, X> map(java.util.function.Function<T, U> mapper);

	/**
	 * Maps the error inside an Err variant using the provided function.
	 * If this is an Ok variant, the value is propagated.
	 */
	<Y> Result<T, Y> mapErr(java.util.function.Function<X, Y> mapper);

	/**
	 * Maps the value inside an Ok variant to a new Result using the provided function.
	 * If this is an Err variant, the error is propagated.
	 */
	<U> Result<U, X> flatMap(java.util.function.Function<T, Result<U, X>> mapper);
}