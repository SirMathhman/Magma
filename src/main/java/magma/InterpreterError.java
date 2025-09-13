package magma;

public record InterpreterError(String reason, String sourceCode) {
	@Override
	public String toString() {
		return reason + ": " + sourceCode;
	}
}
