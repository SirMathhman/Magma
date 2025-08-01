package magma.error;

import magma.input.Input;

/**
 * Represents a context with an Input value.
 * This can be used to provide additional information about where an error occurred,
 * such as the input that caused the error.
 */
public record InputContext(Input value) implements Context {
	/**
	 * Creates a new InputContext with the specified value.
	 *
	 * @param value the input value
	 */
	public InputContext {
	}

	@Override
	public String display() {
		return value.prettyPrint();
	}
}