package magma;

public class Interpreter {
	public String interpret(String input) throws InterpretException {
		if (input == null || input.isEmpty()) {
			return "";
		} else {
			// If input is a valid integer, return it as string
			try {
				Integer.parseInt(input);
				return input;
			} catch (NumberFormatException e) {
				throw new InterpretException("Non-empty input is not allowed");
			}
		}
	}
}
