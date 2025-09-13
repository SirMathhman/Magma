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
		String src = java.util.Optional.ofNullable(input).orElse("");
		if (src.isEmpty()) {
			return Result.success("");
		}
		// For now, any non-empty input is not implemented; return a generic error
		return Result.error(new InterpreterError("not implemented", src, java.util.List.of()));
	}
}
