package magma;

/**
 * Base error type for compiler/runner errors in this project.
 */
public interface MagmaError {
	/**
	 * Human-readable representation of the error.
	 */
	public String display();
}
