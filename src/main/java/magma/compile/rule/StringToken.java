package magma.compile.rule;

/**
 * A token that wraps a string value.
 * This is the simplest and most common token type, representing
 * a sequence of characters as a single token.
 */
public record StringToken(String value) implements Token {
	@Override
	public String display() {
		return value;
	}

	@Override
	public boolean matches(String matchValue) {
		return value.equals(matchValue);
	}
}
