package magma.compile.rule;

/**
 * Represents a single token in a token sequence.
 * Tokens are the atomic units that make up a TokenSequence.
 * This interface provides basic operations for token comparison and display.
 */
public interface Token {
	/**
	 * Returns the string representation of this token for display/output purposes.
	 *
	 * @return The token's display string
	 */
	String display();

	/**
	 * Checks if this token matches the given string value.
	 * This is useful for comparing tokens against string literals during parsing.
	 *
	 * @param value The string to match against
	 * @return true if the token matches the string, false otherwise
	 */
	boolean matches(String value);
}
