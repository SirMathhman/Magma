package magma;

public class Interpreter {
	public static Result<String, InterpretError> interpret(String input) {
		try {
			return new Ok<>(input);
		} catch (NumberFormatException e) {
			return new Err<>(new InterpretError("Undefined value", input));
		}
	}
}
