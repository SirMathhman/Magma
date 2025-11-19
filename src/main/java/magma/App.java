package magma;

/**
 * Core application class for Magma.
 *
 * This class intentionally contains only a single public API method:
 * `interpret`.
 */
public class App {
	/**
	 * Return the leading decimal digit sequence from the provided non-null input.
	 *
	 * @param input the non-null input string
	 * @return the leading decimal digit sequence, or empty string when none exist
	 */
	public static Result<String, String> interpret(String input) {
		java.util.regex.Matcher m = java.util.regex.Pattern.compile("^\\d+").matcher(input);
		if (m.find()) {
			return new Result.Ok<>(m.group());
		}
		return new Result.Ok<>("");
	}
}
