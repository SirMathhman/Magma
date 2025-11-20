package magma;

public class App {
	public static String interpret(String input) {
		if (input == null) {
			return null;
		}
		java.util.regex.Pattern addPattern = java.util.regex.Pattern.compile("^\\s*(.+?)\\s*\\+\\s*(.+?)\\s*$");
		java.util.regex.Matcher addM = addPattern.matcher(input);
		if (addM.matches()) {
			// Interpret both operands (handles typed suffixes) then add
			String left = addM.group(1).trim();
			String right = addM.group(2).trim();
			String leftInterpreted = interpret(left);
			String rightInterpreted = interpret(right);
			try {
				java.math.BigInteger a = new java.math.BigInteger(leftInterpreted);
				java.math.BigInteger b = new java.math.BigInteger(rightInterpreted);
				return a.add(b).toString();
			} catch (NumberFormatException ex) {
				throw new IllegalArgumentException("Invalid operands for addition: " + input);
			}
		}

		java.util.regex.Pattern leadingInt = java.util.regex.Pattern.compile("^[-+]?\\d+");
		java.util.regex.Pattern typedPattern = java.util.regex.Pattern.compile("^([+-]?\\d+)([UI])(8|16|32|64)$");

		java.util.regex.Matcher typedM = typedPattern.matcher(input);
		if (typedM.matches()) {
			String numStr = typedM.group(1);
			String ui = typedM.group(2); // U or I
			int bits = Integer.parseInt(typedM.group(3));
			java.math.BigInteger val;
			try {
				val = new java.math.BigInteger(numStr);
			} catch (NumberFormatException ex) {
				throw new IllegalArgumentException("Invalid numeric value for " + ui + bits + " suffix: " + input);
			}

			java.math.BigInteger min;
			java.math.BigInteger max;
			if (ui.equals("U")) {
				min = java.math.BigInteger.ZERO;
				max = java.math.BigInteger.valueOf(2).pow(bits).subtract(java.math.BigInteger.ONE);
				if (val.signum() < 0) {
					throw new IllegalArgumentException(
							"Negative value not allowed with unsigned " + ui + bits + " suffix: " + input);
				}
			} else { // signed
				min = java.math.BigInteger.valueOf(2).pow(bits - 1).negate();
				max = java.math.BigInteger.valueOf(2).pow(bits - 1).subtract(java.math.BigInteger.ONE);
			}

			if (val.compareTo(min) < 0 || val.compareTo(max) > 0) {
				throw new IllegalArgumentException("Value out of range for " + ui + bits + " suffix: " + input);
			}
			return val.toString();
		}

		java.util.regex.Matcher m = leadingInt.matcher(input);
		if (m.find()) {
			return m.group();
		}
		return input;
	}
}
