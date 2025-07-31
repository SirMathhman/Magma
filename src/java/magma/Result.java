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
}