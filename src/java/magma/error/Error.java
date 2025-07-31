package magma.error;

/**
 * Base interface for all error types in the system.
 * Provides a common method for displaying errors in a human-readable format.
 */
public interface Error {
	/**
	 * Displays this error in a human-readable format.
	 *
	 * @return a string representation of this error
	 */
	String display();
}