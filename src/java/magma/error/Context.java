package magma.error;

/**
 * Represents the context in which a compilation error occurred.
 * This can be used to provide additional information about where the error occurred.
 */
public interface Context {
	/**
	 * Displays this context in a human-readable format.
	 *
	 * @return a string representation of this context
	 */
	String display();
}