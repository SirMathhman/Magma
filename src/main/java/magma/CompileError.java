package magma;

/**
 * Simple compile error wrapper used as the error type from Compiler.compile.
 */
public final class CompileError implements MagmaError {
	private final String message;

	public CompileError(String message) {
		this.message = message;
	}

	@Override
	public String display() {
		return message;
	}
}
