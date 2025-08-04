package magma;

/**
 * Exception thrown when there is an error during the compilation process.
 * This exception is used by the Compiler class instead of IOException to maintain
 * separation of concerns, as the Compiler should not be aware of IO operations.
 */
public class CompileException extends Exception {

	/**
	 * Constructs a new CompileException with the specified detail message.
	 *
	 * @param message the detail message
	 */
	public CompileException(String message) {
		super(message);
	}

	/**
	 * Constructs a new CompileException with the specified detail message and cause.
	 *
	 * @param message the detail message
	 * @param cause   the cause of the exception
	 */
	public CompileException(String message, Throwable cause) {
		super(message, cause);
	}
}