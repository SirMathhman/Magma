package magma;

/**
 * Simple wrapper for errors produced during test runs (compile or execution).
 * Optionally carries a cause which is another MagmaError.
 */
public final class RunError implements MagmaError {
	private final String message;
	private final Option<MagmaError> cause;

	public RunError(String message) {
		this.message = message;
		this.cause = Option.err();
	}

	public RunError(String message, MagmaError cause) {
		this.message = message;
		this.cause = Option.ok(cause);
	}

	public Option<MagmaError> getCause() {
		return cause;
	}

	@Override
	public String display() {
		if (cause instanceof Option.Ok<MagmaError> ok) {
			MagmaError c = ok.value();
			return message + " (cause: " + c.display() + ")";
		}
		return message;
	}

	@Override
	public String toString() {
		return display();
	}
}
