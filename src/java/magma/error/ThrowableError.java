package magma.error;

/**
 * Converts Java's Throwables into our custom error type.
 * This allows for consistent error handling across the application.
 */
public class ThrowableError implements Error {
	private final Throwable throwable;

	/**
	 * Creates a new ThrowableError wrapping the given Throwable.
	 *
	 * @param throwable the Throwable to wrap
	 */
	public ThrowableError(final Throwable throwable) {
		this.throwable = throwable;
	}

	/**
	 * Returns the wrapped Throwable.
	 *
	 * @return the wrapped Throwable
	 */
	public Throwable getThrowable() {
		return this.throwable;
	}

	/**
	 * Displays this error in a human-readable format.
	 *
	 * @return a string representation of this error
	 */
	@Override
	public String display() {
		final StringBuilder sb = new StringBuilder();

		// Add the exception class and message
		sb.append(this.throwable.getClass().getSimpleName()); if (this.throwable.getMessage() != null) {
			sb.append(": ").append(this.throwable.getMessage());
		}

		// Add the stack trace (first element only for brevity)
		final StackTraceElement[] stackTrace = this.throwable.getStackTrace(); if (stackTrace.length > 0) {
			sb.append("\n  at ").append(stackTrace[0]);
		}

		// Add the cause if present
		if (this.throwable.getCause() != null) {
			sb.append("\nCaused by: "); sb.append(new ThrowableError(this.throwable.getCause()).display());
		}

		return sb.toString();
	}
}