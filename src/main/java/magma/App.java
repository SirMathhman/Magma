package magma;

public class App {
	public static String interpret(String input) {
		if (input == null) {
			return null;
		}
		java.util.regex.Pattern leadingInt = java.util.regex.Pattern.compile("^[-+]?\\d+");
		java.util.regex.Pattern negativeU8 = java.util.regex.Pattern.compile("^-\\d+U8$");

		if (negativeU8.matcher(input).matches()) {
			throw new IllegalArgumentException("Negative value not allowed with unsigned U8 suffix: " + input);
		}

		java.util.regex.Matcher m = leadingInt.matcher(input);
		if (m.find()) {
			return m.group();
		}
		return input;
	}
}
