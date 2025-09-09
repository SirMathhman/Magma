package magma;

/**
 * Interpreter provides functionality to interpret source code with a given
 * input.
 */
public class Interpreter {

	/**
	 * Interpret the given source with the provided input and produce a result
	 * string.
	 *
	 * Currently this method is a stub and returns an error Result containing the
	 * offending source.
	 *
	 * @param source the source code to interpret
	 * @param input  the runtime input for the program
	 * @return the result of interpretation wrapped in a Result (Ok or Err)
	 */
	public Result<String, String> interpret(String source, String input) {
		// Minimal implementation: if the source is a simple integer literal,
		// return it as the program output. Otherwise, keep the previous stub
		// behavior and return Err with the source.
		if (source != null && source.matches("\\s*[+-]?\\d+\\s*")) {
			// normalize: trim whitespace, keep original text representation
			String out = source.trim();
			return new Result.Ok<>(out);
		}
		return new Result.Err<>(source);
	}
}
