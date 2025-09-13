package magma;

/**
 * Custom runtime exception used by the Interpreter stub.
 */
public class InterpreterError extends RuntimeException {
	public InterpreterError() {
		super();
	}

	public InterpreterError(String message) {
		super(message);
	}

	public InterpreterError(String message, Throwable cause) {
		super(message, cause);
	}
}
