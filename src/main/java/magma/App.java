package magma;

public class App {
	private static final java.util.regex.Pattern leadingIntPattern = java.util.regex.Pattern.compile("^[-+]?\\d+");
	private static final java.util.regex.Pattern typedPattern = java.util.regex.Pattern
			.compile("^([+-]?\\d+)([UI])(8|16|32|64)$");

	public static String interpret(String input) {
		if (input == null) {
			return null;
		}

		// Check for addition: find the first + operator and split there
		if (input.contains("+")) {
			int plusIdx = input.indexOf("+");
			if (plusIdx > 0) {
				String left = input.substring(0, plusIdx).trim();
				String rightAndRest = input.substring(plusIdx + 1).trim();

				TypedValue leftTV = parseTypedValue(left);
				
				// Parse the first token in rightAndRest (stopping before the next operator if any)
				int nextOpIdx = -1;
				for (int i = 0; i < rightAndRest.length(); i++) {
					char c = rightAndRest.charAt(i);
					if (c == '+') {
						nextOpIdx = i;
						break;
					}
				}
				
				String right, rest;
				if (nextOpIdx >= 0) {
					right = rightAndRest.substring(0, nextOpIdx).trim();
					rest = rightAndRest.substring(nextOpIdx + 1).trim();
				} else {
					right = rightAndRest.trim();
					rest = "";
				}

				TypedValue rightTV = parseTypedValue(right);

				validateTypedOperands(leftTV, rightTV, input);

				String resultStr = applyBinaryOp(leftTV, rightTV, (a, b) -> a.add(b), input);

				// If there's more to process, recursively evaluate sum + rest
				if (!rest.isEmpty()) {
					return interpret(resultStr + " + " + rest);
				}

				return resultStr;
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

			validateTypedOperands(leftTV, rightTV, input);

			String resultStr = applyBinaryOp(leftTV, rightTV, (a, b) -> a.subtract(b), input);

			return resultStr;
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

	private static void validateTypedOperands(TypedValue leftTV, TypedValue rightTV, String input) {
		// If both operands are typed, their UI and bit width must match
		if (leftTV.typed && rightTV.typed) {
			if (!leftTV.ui.equals(rightTV.ui) || leftTV.bits != rightTV.bits) {
				throw new IllegalArgumentException("Mixed typed suffixes not allowed: " + input);
			}
		}
	}

	private static void validateResult(java.math.BigInteger result, TypedValue leftTV, TypedValue rightTV,
			String input) {
		// If either operand is typed, verify the result is within the typed range
		if (leftTV.typed || rightTV.typed) {
			TypedValue typedTV = leftTV.typed ? leftTV : rightTV;
			java.math.BigInteger[] range = rangeFor(typedTV.ui, typedTV.bits);
			if (result.compareTo(range[0]) < 0 || result.compareTo(range[1]) > 0) {
				throw new IllegalArgumentException(
						"Result out of range for " + typedTV.ui + typedTV.bits + " suffix: " + input);
			}
		}
	}

	private interface BinaryOp {
		java.math.BigInteger apply(java.math.BigInteger a, java.math.BigInteger b);
	}

	private static String applyBinaryOp(TypedValue leftTV, TypedValue rightTV, BinaryOp op, String input) {
		try {
			java.math.BigInteger a = leftTV.value;
			java.math.BigInteger b = rightTV.value;
			java.math.BigInteger result = op.apply(a, b);

			validateResult(result, leftTV, rightTV, input);
			return result.toString();
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException("Invalid operands for operation: " + input);
		}
	}
}
