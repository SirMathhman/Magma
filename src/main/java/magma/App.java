package magma;

public class App {
	private static final java.util.regex.Pattern leadingIntPattern = java.util.regex.Pattern.compile("^[-+]?\\d+");
	private static final java.util.regex.Pattern typedPattern = java.util.regex.Pattern
			.compile("^([+-]?\\d+)([UI])(8|16|32|64)$");

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
			TypedValue leftTV = parseTypedValue(left);
			TypedValue rightTV = parseTypedValue(right);

			// If both operands are typed and their bit width differs, disallow mixing
			if (leftTV.typed && rightTV.typed) {
				if (leftTV.bits != rightTV.bits) {
					throw new IllegalArgumentException("Mixed typed suffixes with different bit width not allowed: " + input);
				}
				// If bits match, signed/unsigned difference is allowed (e.g., I16 + U16)
			}

			try {
				java.math.BigInteger a = leftTV.value;
				java.math.BigInteger b = rightTV.value;
				return a.add(b).toString();
			} catch (NumberFormatException ex) {
				throw new IllegalArgumentException("Invalid operands for addition: " + input);
			}
		}

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

		java.util.regex.Matcher m = leadingIntPattern.matcher(input);
		if (m.find()) {
			return m.group();
		}
		return input;
	}

	private static final class TypedValue {
		final java.math.BigInteger value;
		final boolean typed;
		final String ui;
		final int bits;

		TypedValue(java.math.BigInteger value, boolean typed, String ui, int bits) {
			this.value = value;
			this.typed = typed;
			this.ui = ui;
			this.bits = bits;
		}
	}

	private static TypedValue parseTypedValue(String input) {
		java.util.regex.Matcher typedM = typedPattern.matcher(input);
		if (typedM.matches()) {
			String numStr = typedM.group(1);
			String ui = typedM.group(2);
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
			} else {
				min = java.math.BigInteger.valueOf(2).pow(bits - 1).negate();
				max = java.math.BigInteger.valueOf(2).pow(bits - 1).subtract(java.math.BigInteger.ONE);
			}
			if (val.compareTo(min) < 0 || val.compareTo(max) > 0) {
				throw new IllegalArgumentException("Value out of range for " + ui + bits + " suffix: " + input);
			}
			return new TypedValue(val, true, ui, bits);
		}
		java.util.regex.Matcher m = leadingIntPattern.matcher(input);
		if (m.find()) {
			java.math.BigInteger val = new java.math.BigInteger(m.group());
			return new TypedValue(val, false, null, 0);
		}
		throw new IllegalArgumentException("Invalid numeric value: " + input);
	}
}
