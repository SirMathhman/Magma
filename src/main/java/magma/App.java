package magma;

public class App {
	public static String interpret(String input) {
		if (input == null) {
			return null;
		}
		java.util.regex.Matcher m = java.util.regex.Pattern.compile("^[-+]?\\d+").matcher(input);
		if (m.find()) {
			return m.group();
		}
		return input;
	}
}
