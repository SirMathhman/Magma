package magma;

public class Interpreter {
	public Result<String, InterpretError> interpret(String input) {
		if (input.isEmpty())
			return new Ok<>("");
		// integer literal (decimal)
		// accept a leading decimal integer even if followed by other characters,
		// e.g. "5I32" should be interpreted as the integer literal "5".
		int i = 0;
		while (i < input.length() && Character.isDigit(input.charAt(i)))
			i++;
		if (i > 0)
			return new Ok<>(input.substring(0, i));
		return new Err<>(new InterpretError("Undefined identifier: " + input));
	}
}
