package magma;

import magma.result.Ok;
import magma.result.Result;

public class Interpreter {
	public static Result<String, InterpretError> interpret(String input) {
		return new Ok<>(input);
	}
}
