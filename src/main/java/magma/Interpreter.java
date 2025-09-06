package magma;

import java.util.Objects;

public class Interpreter {
	public Result<String, InterpretError> interpret(String input) {
		if (Objects.isNull(input)) {
			return new Err<>(new InterpretError("input is absent"));
		}
		if (input.equals("")) {
			return new Ok<>("");
		}
		// simple behaviour: echo input
		return new Ok<>(input);
	}
}
