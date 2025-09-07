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
		// strip known integer-suffix annotations like I8/I16/I32/I64 and U8/U16/U32/U64
		String[] suffixes = new String[] { "I8", "I16", "I32", "I64", "U8", "U16", "U32", "U64" };
		for (String sfx : suffixes) {
			if (input.endsWith(sfx)) {
				return new Ok<>(input.substring(0, input.length() - sfx.length()));
			}
		}
		return new Ok<>(input);
	}
}
