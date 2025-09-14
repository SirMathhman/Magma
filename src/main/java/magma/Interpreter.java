package magma;

public class Interpreter {
	public String interpret(String input) throws InterpretException {
		if (input == null || input.isEmpty()) {
			return "";
		} else {
			throw new InterpretException("Non-empty input is not allowed");
		}
	}
}
