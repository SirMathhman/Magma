package magma;

public class InterpreterException extends Exception {
	private final String reason;
	private final String sourceCode;

	public InterpreterException(String reason, String sourceCode) {
		this.reason = reason;
		this.sourceCode = sourceCode;
	}

	@Override
	public String getMessage() {
		return reason + ": " + sourceCode;
	}
}
