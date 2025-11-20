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

			// If both operands are typed, their UI and bit width must match
			if (leftTV.typed && rightTV.typed) {
				if (!leftTV.ui.equals(rightTV.ui) || leftTV.bits != rightTV.bits) {
					throw new IllegalArgumentException("Mixed typed suffixes not allowed: " + input);
				}
			}

			try {
				java.math.BigInteger a = leftTV.value;
				java.math.BigInteger b = rightTV.value;
				java.math.BigInteger sum = a.add(b);

				// If either operand is typed, verify the result is within the typed range
				if (leftTV.typed || rightTV.typed) {
					TypedValue typedTV = leftTV.typed ? leftTV : rightTV;
					java.math.BigInteger[] range = rangeFor(typedTV.ui, typedTV.bits);
					if (sum.compareTo(range[0]) < 0 || sum.compareTo(range[1]) > 0) {
						throw new IllegalArgumentException(
								"Result out of range for " + typedTV.ui + typedTV.bits + " suffix: " + input);
					}
				}

				return sum.toString();
			} catch (NumberFormatException ex) {
				throw new IllegalArgumentException("Invalid operands for addition: " + input);
			}
		}

		// Handle subtraction
		java.util.regex.Pattern subPattern = java.util.regex.Pattern.compile("^\\s*(.+?)\\s*-\\s*(.+?)\\s*$");
		java.util.regex.Matcher subM = subPattern.matcher(input);
		if (subM.matches()) {
			String left = subM.group(1).trim();
			String right = subM.group(2).trim();
			TypedValue leftTV = parseTypedValue(left);
			TypedValue rightTV = parseTypedValue(right);

			// If both operands are typed, their UI and bit width must match
			if (leftTV.typed && rightTV.typed) {
				if (!leftTV.ui.equals(rightTV.ui) || leftTV.bits != rightTV.bits) {
					throw new IllegalArgumentException("Mixed typed suffixes not allowed: " + input);
				}
			}

			try {
				java.math.BigInteger a = leftTV.value;
				java.math.BigInteger b = rightTV.value;
				java.math.BigInteger diff = a.subtract(b);

				// If either operand is typed, verify the result is within the typed range
				if (leftTV.typed || rightTV.typed) {
					TypedValue typedTV = leftTV.typed ? leftTV : rightTV;
					java.math.BigInteger[] range = rangeFor(typedTV.ui, typedTV.bits);
					if (diff.compareTo(range[0]) < 0 || diff.compareTo(range[1]) > 0) {
						throw new IllegalArgumentException(
								"Result out of range for " + typedTV.ui + typedTV.bits + " suffix: " + input);
					}
				}

				return diff.toString();
			} catch (NumberFormatException ex) {
				throw new IllegalArgumentException("Invalid operands for subtraction: " + input);
			}
		}

		// If input is a typed or plain integer, delegate to parseTypedValue
		if (typedPattern.matcher(input).matches() || leadingIntPattern.matcher(input).find()) {
			return parseTypedValue(input).value.toString();
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

			java.math.BigInteger[] range = rangeFor(ui, bits);
			java.math.BigInteger min = range[0];
			java.math.BigInteger max = range[1];
			if ("U".equals(ui) && val.signum() < 0) {
				throw new IllegalArgumentException(
						"Negative value not allowed with unsigned " + ui + bits + " suffix: " + input);
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

	private static java.math.BigInteger[] rangeFor(String ui, int bits) {
		java.math.BigInteger min;
		java.math.BigInteger max;
		if ("U".equals(ui)) {
			min = java.math.BigInteger.ZERO;
			max = java.math.BigInteger.valueOf(2).pow(bits).subtract(java.math.BigInteger.ONE);
		} else {
			min = java.math.BigInteger.valueOf(2).pow(bits - 1).negate();
			max = java.math.BigInteger.valueOf(2).pow(bits - 1).subtract(java.math.BigInteger.ONE);
		}
		return new java.math.BigInteger[] { min, max };
	}
}
