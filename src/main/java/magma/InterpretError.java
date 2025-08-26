package magma;

public record InterpretError(String message, String input) {
	public String display() {
		return message + ": " + input;
	}
}
