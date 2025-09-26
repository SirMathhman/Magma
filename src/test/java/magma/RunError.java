package magma;

import magma.api.MagmaError;
import magma.api.Option;

/**
 * Simple wrapper for errors produced during test runs (compile or execution).
 * Optionly carries a cause which is another MagmaError.
 */
public final class RunError implements MagmaError {
	private final String message;
	private final Option<MagmaError> cause;

	public RunError(String message) {
		this.message = message;
		this.cause = Option.empty();
	}

	public RunError(String message, MagmaError cause) {
		this.message = message;
		this.cause = Option.of(cause);
	}

	public Option<MagmaError> getCause() {
		return cause;
	}

	@Override
	public String display() {
		if (cause instanceof Option.Some<MagmaError>(MagmaError value)) {
			return message + " (cause: " + value.display() + ")";
		}
		return message;
	}

	@Override
	public String toString() {
		return display();
	}
}
