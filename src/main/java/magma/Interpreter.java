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
		// If input is digits only, or starts with digits, return the leading digit
		// sequence
		java.util.regex.Matcher m = java.util.regex.Pattern.compile("^(\\d+)").matcher(normalized);
		if (m.find()) {
			return Result.success(m.group(1));
		}
		return Result.error(new InterpreterError("Only empty input or numeric input is supported in this stub"));
	}
}
