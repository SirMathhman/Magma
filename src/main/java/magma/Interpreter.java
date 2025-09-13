package magma;

public class Interpreter {
	public static String interpret(String source) throws InterpreterException {
		if (source.isEmpty()) return "";

		try {
			Integer.parseInt(source);
			return source;
		} catch (NumberFormatException e) {
			throw new InterpreterException("Invalid input", source);
		}
	}
}
