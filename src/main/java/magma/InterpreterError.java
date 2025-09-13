package magma;

/**
 * InterpreterError is a simple data holder representing interpreter failures.
 */
public final class InterpreterError {
	private final String message;

	public InterpreterError(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
