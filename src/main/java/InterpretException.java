public class InterpretException extends Exception {
	public InterpretException() {
		super();
	}

	public InterpretException(String message) {
		super(message);
	}

	public InterpretException(String message, Throwable cause) {
		super(message, cause);
	}

	public InterpretException(Throwable cause) {
		super(cause);
	}
}