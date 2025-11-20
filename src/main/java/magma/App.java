package magma;

public class App {
	private static final java.util.regex.Pattern leadingIntPattern = java.util.regex.Pattern.compile("^[-+]?\\d+");
	private static final java.util.regex.Pattern typedPattern = java.util.regex.Pattern
			.compile("^([+-]?\\d+)([UI])(8|16|32|64)$");

	public static String interpret(String input) {
		if (input == null) {
			return null;
		}

		input = input.trim();

		// Handle parentheses
		if (input.startsWith("(") && input.endsWith(")")) {
			String inner = input.substring(1, input.length() - 1);
			// Check if parentheses are matched at this level
			if (isBalancedParentheses(inner)) {
				return interpret(inner);
			}
		}

		// Find the rightmost lower precedence operator at depth 0
		// This ensures higher precedence operators are evaluated first
		int addSubOpIdx = findRightmostOperator(input, 1, new char[] { '+', '-' });
		if (addSubOpIdx > 0) {
			return processBinaryOp(input, addSubOpIdx, new char[] { '+', '-' });
		}

		// Find the rightmost higher precedence operator at depth 0
		int mulDivOpIdx = findRightmostOperator(input, 1, new char[] { '*', '/' });
		if (mulDivOpIdx > 0) {
			return processBinaryOp(input, mulDivOpIdx, new char[] { '*', '/' });
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

	private static boolean isBalancedParentheses(String str) {
		int depth = 0;
		for (char c : str.toCharArray()) {
			if (c == '(') {
				depth++;
			} else if (c == ')') {
				depth--;
				if (depth < 0) {
					return false;
				}
			}
		}
		return depth == 0;
	}

	private static String processBinaryOp(String input, int opIdx, char[] operators) {
		char opChar = input.charAt(opIdx);
		String left = input.substring(0, opIdx).trim();
		String right = input.substring(opIdx + 1).trim();

		// Check if left side contains operators (and thus needs recursive
		// interpretation)
		TypedValue leftTV;
		int leftOpIdx = findRightmostOperator(left, 1, new char[] { '+', '-', '*', '/' });
		if (leftOpIdx > 0) {
			String interpretedLeft = interpret(left);
			leftTV = parseTypedValue(interpretedLeft);
		} else {
			leftTV = parseTypedValue(left);
		}

		// Check if right side contains operators (and thus needs recursive
		// interpretation)
		TypedValue rightTV;
		int rightOpIdx = findRightmostOperator(right, 1, new char[] { '+', '-', '*', '/' });
		if (rightOpIdx > 0) {
			String interpretedRight = interpret(right);
			rightTV = parseTypedValue(interpretedRight);
		} else {
			rightTV = parseTypedValue(right);
		}

		validateTypedOperands(leftTV, rightTV, input);

		BinaryOp op = getBinaryOp(opChar);
		String resultStr = applyBinaryOp(leftTV, rightTV, op, input);

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

	private static int findRightmostOperator(String input, int startIdx, char[] operators) {
		int depth = 0;
		int lastOpIdx = -1;
		for (int i = startIdx; i < input.length(); i++) {
			char c = input.charAt(i);

			// Track parentheses depth
			if (c == '(') {
				depth++;
				continue;
			} else if (c == ')') {
				depth--;
				continue;
			}

			// Skip operators inside parentheses
			if (depth > 0) {
				continue;
			}

			boolean isOperator = false;
			for (char op : operators) {
				if (c == op) {
					isOperator = true;
					break;
				}
			}
			if (!isOperator)
				continue;

			if (!Character.isWhitespace(input.charAt(i - 1))) {
				lastOpIdx = i;
			} else if (Character.isWhitespace(input.charAt(i - 1))) {
				String leftPart = input.substring(0, i).trim();
				if (!leftPart.isEmpty()) {
					try {
						parseTypedValue(leftPart);
						lastOpIdx = i;
					} catch (IllegalArgumentException e) {
						continue;
					}
				}
			}
		}
		return lastOpIdx;
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
		input = input.trim();

		// Handle parentheses
		if (input.startsWith("(") && input.endsWith(")")) {
			String inner = input.substring(1, input.length() - 1);
			// Check if parentheses are matched at this level
			if (isBalancedParentheses(inner)) {
				String evaluated = interpret(inner);
				return parseTypedValue(evaluated);
			}
		}

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
		} catch (ArithmeticException ex) {
			throw new IllegalArgumentException("Division by zero: " + input);
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException("Invalid operands for operation: " + input);
		}
	}
}
