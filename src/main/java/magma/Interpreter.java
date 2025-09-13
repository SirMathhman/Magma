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
		if (normalized.isEmpty()) {
			return new Result.Ok<>("");
		}

		// For any non-empty input, return an error (this keeps the interpreter simple
		// and allows tests that expect invalid input to receive an Err.)
		return new Result.Err<>(new InterpreterError("invalid input", normalized, java.util.List.of()));
	}
}
