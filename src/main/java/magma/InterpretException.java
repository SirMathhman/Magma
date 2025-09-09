package magma;

/**
 * Exception thrown when interpreting source code fails.
 *
 * The exception message is formatted as: message + ": " + source
 */
public class InterpretException extends Exception {
	private final String source;

	public InterpretException(String message, String source) {
		super(message + ": " + source);
		this.source = source;
	}

	/**
	 * Returns the original source string that caused the error.
	 */
	public String getSource() {
		return source;
	}
}
