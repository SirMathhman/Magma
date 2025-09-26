package magma.api;

/**
 * Base error type for compiler/runner errors in this project.
 */
public interface MagmaError {
	/**
	 * Human-readable representation of the error.
	 */
	String display();
}
