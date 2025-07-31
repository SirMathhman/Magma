package magma.result;

/**
 * A generic Result interface representing either a successful operation (Ok) or an error (Err).
 * This is used in place of exceptions for error handling.
 * 
 * This is a sealed interface that can only be implemented by Ok and Err classes,
 * allowing for exhaustive pattern matching with switch expressions.
 *
 * @param <T> The type of the value in case of success
 * @param <E> The type of the error in case of failure
 */
public sealed interface Result<T, E> permits Ok, Err {
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
	T value();

	/**
	 * Gets the error if this is an Err result.
	 *
	 * @return the error
	 * @throws IllegalStateException if this is an Ok result
	 */
	E error();
}