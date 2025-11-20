package magma;

public class App {
	public static String interpret(String input) {
		if (input == null) {
			return null;
		}
		java.util.regex.Pattern leadingInt = java.util.regex.Pattern.compile("^[-+]?\\d+");
		java.util.regex.Pattern u8Pattern = java.util.regex.Pattern.compile("^([+-]?\\d+)U8$");

		java.util.regex.Matcher u8m = u8Pattern.matcher(input);
		if (u8m.matches()) {
			String numStr = u8m.group(1);
			try {
				long val = Long.parseLong(numStr);
				if (val < 0) {
					throw new IllegalArgumentException("Negative value not allowed with unsigned U8 suffix: " + input);
				}
				if (val > 255) {
					throw new IllegalArgumentException("Value out of range for U8 suffix: " + input);
				}
				return Long.toString(val);
			} catch (NumberFormatException ex) {
				throw new IllegalArgumentException("Invalid numeric value for U8 suffix: " + input);
			}
		}

		java.util.regex.Matcher m = leadingInt.matcher(input);
		if (m.find()) {
			return m.group();
		}
		return input;
	}
}
