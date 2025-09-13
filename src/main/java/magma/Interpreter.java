package magma;

/**
 * Simple interpreter stub.
 * The interpret method is a placeholder and should be implemented later.
 */
public class Interpreter {

	/**
	 * Interpret the provided input and return a Result.
	 * Behavior (stub): returns Result.success("") if input is exactly empty ("").
	 * Otherwise returns Result.error(InterpreterError).
	 */
	public Result<String, InterpreterError> interpret(String input) {
		String normalized = java.util.Objects.toString(input, "");
		if ("".equals(normalized)) {
			return Result.success("");
		}
		// Support a simple addition expression like "1 + 2" using simple string
		// operations
		int plus = normalized.indexOf('+');
		if (plus > 0) {
			String left = normalized.substring(0, plus).trim();
			String right = normalized.substring(plus + 1).trim();
			try {
				int a = Integer.parseInt(left);
				int b = Integer.parseInt(right);
				return Result.success(Integer.toString(a + b));
			} catch (NumberFormatException ex) {
				// fall through to other checks
			}
		}

		// If input starts with digits, return the leading digit sequence using
		// simple String scanning (avoid java.util.regex per project policy)
		int i = 0;
		while (i < normalized.length() && Character.isDigit(normalized.charAt(i))) {
			i++;
		}
		if (i > 0) {
			return Result.success(normalized.substring(0, i));
		}
		return Result.error(new InterpreterError("Only empty input or numeric input is supported in this stub"));
	}
}
