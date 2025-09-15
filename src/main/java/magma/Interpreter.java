package magma;

public class Interpreter {
	public String interpret(String input) {
		if (input == null || input.isEmpty()) {
			return "";
		} else {
			throw new InterpretException("Non-empty input is not allowed");
		}
	}
}
