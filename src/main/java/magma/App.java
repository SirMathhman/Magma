package magma;

/**
 * Core application class for Magma.
 *
 * This class intentionally contains only a single public API method:
 * `interpret`.
 */
public class App {
	/**
	 * Return the given string unchanged.
	 *
	 * @param input the input string
	 * @return the same string that was provided
	 */
	public static String interpret(String input) {
		if (input == null) return null;
		java.util.regex.Matcher m = java.util.regex.Pattern.compile("^\\d+").matcher(input);
		if (m.find()) {
			return m.group();
		}
		return "";
	}
}
