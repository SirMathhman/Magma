package magma.node;

/**
 * A node that represents a placeholder or comment in generated code.
 * 
 * This class is used to insert comments or placeholders in the generated code.
 * It wraps the provided value in C-style comment delimiters.
 * 
 * Example usage:
 * <pre>
 * JavaParameter placeholder = new Placeholder("TODO: Implement this method");
 * String comment = placeholder.generate();  // "/*TODO: Implement this method*&#47;"
 * </pre>
 * 
 * @param value The text to be included in the comment
 */
public record Placeholder(String value) implements JavaParameter {
	/**
	 * Wraps the input string in C-style comment delimiters.
	 * 
	 * This static utility method adds "/*" before and "*&#47;" after the input string.
	 *
	 * @param input The string to wrap in comment delimiters
	 * @return The input string wrapped in comment delimiters
	 */
	public static String wrap(final String input) {
		return "/*" + input + "*/";
	}

	/**
	 * Generates the string representation of this placeholder.
	 * 
	 * This method wraps the value in C-style comment delimiters using the {@link #wrap} method.
	 *
	 * @return The value wrapped in comment delimiters
	 */
	@Override
	public String generate() {
		return Placeholder.wrap(this.value);
	}
}
