package magma;

/**
 * Simple error type returned by the interpreter.
 *
 * errorReason: a short description or offending fragment
 * sourceCode: the full source text related to the error (where available)
 */
public record InterpretError(String errorReason, String sourceCode) {
	public String display() {
		return errorReason + ": " + sourceCode;
	}
}
