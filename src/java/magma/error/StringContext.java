package magma.error;

/**
 * Represents a context with a string value.
 * This can be used to provide additional information about where an error occurred,
 * such as the input string that caused the error.
 */
public class StringContext implements Context {
	private final String value;

	/**
	 * Creates a new StringContext with the specified value.
	 *
	 * @param value the string value
	 */
	public StringContext(final String value) {
		this.value = value;
	}

	/**
	 * Returns the string value of this context.
	 *
	 * @return the string value
	 */
	public String getValue() {
		return this.value;
	}

	@Override
	public String display() {
		return "\"" + this.value + "\"";
	}
}