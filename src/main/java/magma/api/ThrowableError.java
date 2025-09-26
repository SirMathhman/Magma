package magma.api;

import java.util.Objects;

/**
 * Wraps a Throwable as a MagmaError so exceptions can be carried through the
 * lightweight error model used in tests and the compiler.
 */
public record ThrowableError(Throwable throwable) implements MagmaError {
	/**
	 * Returns a compact human-readable representation for the throwable.
	 */
	@Override
	public String display() {
		if (Objects.isNull(throwable)) return "(no throwable)";
		String msg = throwable.getMessage();
		return Objects.toString(msg, throwable.toString());
	}
}
