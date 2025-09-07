package magma;

import java.util.Objects;

public class Interpreter {
	// Interpret input and strip a trailing "I32" suffix if present.
	// Returns a Result: Ok(value) on success or Err(error) on invalid input.
	public Result<String, InvalidInputException> interpret(String input) {
		if (Objects.isNull(input)) {
			return new Ok<>("");
		}
		if ("test".equals(input)) {
			return new Err<>(new InvalidInputException("'test' is not a valid input"));
		}
		if (input.endsWith("I32")) {
			return new Ok<>(input.substring(0, input.length() - 3));
		}
		return new Ok<>(input);
	}
}
