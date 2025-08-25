package magma;

public class InterpretError {
	private final String message;
	private final String context;

	public InterpretError(String message, String context) {
		this.message = message;
		this.context = context;
	}

	public String display() {
		return message + ": " + context;
	}
}
