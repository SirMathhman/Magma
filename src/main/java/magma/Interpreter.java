package magma;

public class Interpreter {
	public Result<String, InterpretError> interpret(String input) {
		if (input.isEmpty()) return new Ok<>("");
		// integer literal (decimal)
		if (input.matches("[0-9]+")) return new Ok<>(input);
		return new Err<>(new InterpretError("Undefined identifier: " + input));
	}
}
