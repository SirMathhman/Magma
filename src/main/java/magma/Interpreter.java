package magma;

import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

public class Interpreter {
	public static Result<String, InterpretError> interpret(String input) {
		try {
			Integer.parseInt(input);
			return new Ok<>(input);
		} catch (NumberFormatException e) {
			return new Err<>(new InterpretError("Not a number", input));
		}
	}
}
