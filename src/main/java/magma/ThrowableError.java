package magma;

import java.util.Objects;

/**
 * Wraps a Throwable as a MagmaError so exceptions can be carried through the
 * lightweight error model used in tests and the compiler.
 */
public final class ThrowableError implements MagmaError {
	private final Throwable throwable;

	public ThrowableError(Throwable throwable) {
		this.throwable = throwable;
	}

	/**
	 * Returns a compact human-readable representation for the throwable.
	 */
	@Override
	public String display() {
		if (Objects.isNull(throwable))
			return "(no throwable)";
		String msg = throwable.getMessage();
		return Objects.toString(msg, throwable.toString());
	}

	/**
	 * Expose the wrapped throwable if callers need it.
	 */
	public Throwable getThrowable() {
		return throwable;
	}
}
