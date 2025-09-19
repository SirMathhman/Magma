package magma;

/**
 * Checked exception thrown when App.interpret cannot interpret the input.
 */
public class InterpretException extends Exception {
	public InterpretException(String message) {
		super(message);
	}

	public InterpretException(String message, Throwable cause) {
		super(message, cause);
	}
}
