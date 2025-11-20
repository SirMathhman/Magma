package magma;

public class App {
	private static final java.util.regex.Pattern leadingIntPattern = java.util.regex.Pattern.compile("^[-+]?\\d+");
	private static final java.util.regex.Pattern typedPattern = java.util.regex.Pattern
			.compile("^([+-]?\\d+)([UI])(8|16|32|64)$");

	public static String interpret(String input) {
		if (input == null) {
			return null;
		}

		// Handle lower precedence operators first: + and -
		int addSubOpIdx = findOperator(input, 1, new char[]{'+', '-'});
		if (addSubOpIdx > 0) {
			return processBinaryOp(input, addSubOpIdx, new char[]{'+', '-'});
		}

		// Handle higher precedence operators: * and /
		int mulDivOpIdx = findOperator(input, 1, new char[]{'*', '/'});
		if (mulDivOpIdx > 0) {
			return processBinaryOp(input, mulDivOpIdx, new char[]{'*', '/', '+', '-'});
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

	private static String processBinaryOp(String input, int opIdx, char[] nextOps) {
		char opChar = input.charAt(opIdx);
		String left = input.substring(0, opIdx).trim();
		String rightAndRest = input.substring(opIdx + 1).trim();

		TypedValue leftTV = parseTypedValue(left);
		ParsedOperands operands = parseOperandsBeforeNextOp(rightAndRest, nextOps);

		validateTypedOperands(leftTV, operands.rightTV, input);

		BinaryOp op = getBinaryOp(opChar);
		String resultStr = applyBinaryOp(leftTV, operands.rightTV, op, input);

		// If there's more to process, recursively evaluate result + rest
		if (!operands.rest.isEmpty()) {
			return interpret(resultStr + " " + operands.rest);
		}

		return resultStr;
	}

	private static BinaryOp getBinaryOp(char opChar) {
		switch (opChar) {
			case '+':
				return (a, b) -> a.add(b);
			case '-':
				return (a, b) -> a.subtract(b);
			case '*':
				return (a, b) -> a.multiply(b);
			case '/':
				return (a, b) -> a.divide(b);
			default:
				throw new IllegalArgumentException("Unknown operator: " + opChar);
		}
	}

	private static int findOperator(String input, int startIdx, char[] operators) {
		for (int i = startIdx; i < input.length(); i++) {
			char c = input.charAt(i);
			boolean isOperator = false;
			for (char op : operators) {
				if (c == op) {
					isOperator = true;
					break;
				}
			}
			if (!isOperator) continue;

			if (!Character.isWhitespace(input.charAt(i - 1))) {
				return i;
			} else if (Character.isWhitespace(input.charAt(i - 1))) {
				String leftPart = input.substring(0, i).trim();
				if (!leftPart.isEmpty()) {
					try {
						parseTypedValue(leftPart);
						return i;
					} catch (IllegalArgumentException e) {
						continue;
					}
				}
			}
		}
		return -1;
	}

	private static final class ParsedOperands {
		final TypedValue rightTV;
		final String rest;

		ParsedOperands(TypedValue rightTV, String rest) {
			this.rightTV = rightTV;
			this.rest = rest;
		}
	}

	private static ParsedOperands parseOperandsBeforeNextOp(String input, char[] nextOps) {
		int nextOpIdx = -1;
		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			boolean isNextOp = false;
			for (char op : nextOps) {
				if (c == op) {
					isNextOp = true;
					break;
				}
			}
			if (!isNextOp) continue;

			if (i == 0 || Character.isWhitespace(input.charAt(i - 1))) {
				String potentialRight = input.substring(0, i).trim();
				if (!potentialRight.isEmpty()) {
					try {
						parseTypedValue(potentialRight);
						nextOpIdx = i;
						break;
					} catch (IllegalArgumentException e) {
						continue;
					}
				}
			} else {
				nextOpIdx = i;
				break;
			}
		}

		String right, rest;
		if (nextOpIdx >= 0) {
			right = input.substring(0, nextOpIdx).trim();
			rest = input.substring(nextOpIdx).trim();
		} else {
			right = input.trim();
			rest = "";
		}

		TypedValue rightTV = parseTypedValue(right);
		return new ParsedOperands(rightTV, rest);
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
