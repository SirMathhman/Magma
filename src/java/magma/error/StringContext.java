package magma.error;

/**
 * Represents a context with a string value.
 * This can be used to provide additional information about where an error occurred,
 * such as the input string that caused the error.
 */
public record StringContext(String value) implements Context {
	/**
	 * Creates a new StringContext with the specified value.
	 *
	 * @param value the string value
	 */
	public StringContext {
	}

	@Override
	public String display() {
		return "\"" + this.value + "\"";
	}
}