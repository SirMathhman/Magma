package magma;

public class Interpreter {
	// Interpret input and strip a trailing "I32" suffix if present.
	public String interpret(String input) {
		if (input == null) return null;
		if (input.endsWith("I32")) {
			return input.substring(0, input.length() - 3);
		}
		return input;
	}
}
