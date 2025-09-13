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
		// Treat missing input as empty by using Optional
		String normalized = java.util.Optional.ofNullable(input).orElse("");
		// trim surrounding whitespace for easier matching
		normalized = normalized.trim();
		if (normalized.isEmpty()) {
			return new Result.Ok<>("");
		}

		// Recognize integer literals (e.g. "2") and return them as-is
		if (normalized.matches("-?\\d+")) {
			try {
				int n = Integer.parseInt(normalized);
				return new Result.Ok<>(String.valueOf(n));
			} catch (NumberFormatException e) {
				// fallthrough to try parsing as an expression
			}
		}

		// Recognize simple addition expressions like "3 + 5" (integers only)
		java.util.regex.Pattern addPat = java.util.regex.Pattern.compile("^(-?\\d+)\\s*\\+\\s*(-?\\d+)$");
		java.util.regex.Matcher m = addPat.matcher(normalized);
		if (m.matches()) {
			try {
				int a = Integer.parseInt(m.group(1));
				int b = Integer.parseInt(m.group(2));
				return new Result.Ok<>(String.valueOf(a + b));
			} catch (NumberFormatException e) {
				// if parsing fails unexpectedly, return an error below
			}
		}

		// For any non-empty input, return an error (this keeps the interpreter simple
		// and allows tests that expect invalid input to receive an Err.)
		return new Result.Err<>(new InterpreterError("invalid input", normalized, java.util.List.of()));
	}
}
