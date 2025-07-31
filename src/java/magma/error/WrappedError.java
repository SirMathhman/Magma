package magma.error;

/**
 * Wraps an instance of our custom error type.
 * This allows for composition of errors and consistent error handling.
 */
public class WrappedError implements Error {
	private final Error error;

	/**
	 * Creates a new WrappedError containing the given error.
	 *
	 * @param error the error to wrap
	 */
	public WrappedError(final Error error) {
		this.error = error;
	}

	/**
	 * Returns the wrapped error.
	 *
	 * @return the wrapped error
	 */
	public Error getError() {
		return this.error;
	}

	/**
	 * Displays this error in a human-readable format by delegating to the wrapped error.
	 *
	 * @return a string representation of this error
	 */
	@Override
	public String display() {
		return "Wrapped error: " + this.error.display();
	}
}