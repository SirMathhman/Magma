package magma;

public class Interpreter {
	public Result<String, InterpretError> interpret(String input) {
		if (input.isEmpty()) return new Ok<>("");
		return new Err<>(new InterpretError("Undefined identifier: " + input));
	}
}
