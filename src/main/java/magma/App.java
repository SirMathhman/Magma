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
		// If the input begins with a minus followed by digits, return an error.
		java.util.regex.Matcher negative = java.util.regex.Pattern.compile("^-\\d+").matcher(input);
		if (negative.find()) {
			return new Result.Err<>("negative numbers not allowed");
		}

		java.util.regex.Matcher m = java.util.regex.Pattern.compile("^\\d+").matcher(input);
		if (m.find()) {
			return new Result.Ok<>(m.group());
		}
		return new Result.Ok<>("");
	}
}
