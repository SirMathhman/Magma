package magma.interpret;

/**
 * Interpreter error type used by the interpreter implementation.
 */
public record InterpretError(String message) {
	public String display() {
		return message;
	}
}
