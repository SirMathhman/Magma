package magma;

import java.util.Objects;

public class Interpreter {
	// Interpret input and strip a trailing "I32" suffix if present.
	// Always returns a non-null String (empty string when input is absent).
	public String interpret(String input) throws InvalidInputException {
		if (Objects.isNull(input)) {
			return "";
		}
		if ("test".equals(input)) {
			throw new InvalidInputException("'test' is not a valid input");
		}
		if (input.endsWith("I32")) {
			return input.substring(0, input.length() - 3);
		}
		return input;
	}
}
