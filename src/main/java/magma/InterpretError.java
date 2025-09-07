package magma;

/**
 * Checked exception for invalid interpreter input.
 */
public record InterpretError(String message) {
	public String display() {
		return message;
	}
}
