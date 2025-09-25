package magma;

public class Compiler {
	/**
	 * For now, returns the input string unchanged.
	 *
	 * @param input the input string
	 * @return an Ok Result holding the same input string
	 */
	public Result<String, String> compile(String input) {
		return Result.ok(input);
	}
}
