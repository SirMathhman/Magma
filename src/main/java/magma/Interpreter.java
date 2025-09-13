package magma;

/**
 * Simple interpreter stub.
 * The interpret method is a placeholder and should be implemented later.
 */
public class Interpreter {

	/**
	 * Interpret the provided input and return a result string.
	 * Behavior (stub): returns empty string if input is exactly empty ("").
	 * Otherwise throws InterpreterError.
	 */
	public String interpret(String input) {
		if ("".equals(input)) {
			return "";
		}
		throw new InterpreterError("Only empty input is supported in this stub");
	}
}
