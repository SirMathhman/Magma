package magma;

/**
 * Simple wrapper for errors produced during test runs (compile or execution).
 */
public final class RunError implements MagmaError {
	private final String message;

	public RunError(String message) {
		this.message = message;
	}

	@Override
	public String display() {
		return message;
	}

	@Override
	public String toString() {
		return display();
	}
}
