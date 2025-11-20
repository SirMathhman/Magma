package magma;

public class App {
	private static final class Patterns {
		private static final java.util.regex.Pattern leadingInt;
		private static final java.util.regex.Pattern typed;
		private static final java.util.regex.Pattern let;
		private static final java.util.regex.Pattern letUntyped;
		private static final java.util.regex.Pattern letUntypedUninitialized;
		private static final java.util.regex.Pattern letUninitialized;
		private static final java.util.regex.Pattern assignment;
		private static final java.util.regex.Pattern max;

		static {
			leadingInt = java.util.regex.Pattern.compile("^[-+]?\\d+");
			typed = java.util.regex.Pattern.compile("^([+-]?\\d+)([UI])(8|16|32|64)$");
			let = java.util.regex.Pattern.compile("^let\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*:\\s*([UI])(8|16|32|64)\\s*=\\s*(.+);\\s*$");
			letUntyped = java.util.regex.Pattern.compile("^let\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*(.+);\\s*$");
			letUntypedUninitialized = java.util.regex.Pattern.compile("^let\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*;\\s*$");
			letUninitialized = java.util.regex.Pattern.compile("^let\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*:\\s*([UI])(8|16|32|64)\\s*;\\s*$");
			assignment = java.util.regex.Pattern.compile("^([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*(.+);\\s*$");
			max = java.util.regex.Pattern.compile("^max<([UI])(8|16|32|64)>\\(\\)$");
		}
	}

	private static final java.util.Map<String, TypedValue> variables = new java.util.HashMap<>();

	private static final class TypedValue {
		final java.math.BigInteger value; // null if uninitialized
		final boolean typed;
		final String ui;
		final int bits;
		final boolean initialized;

		TypedValue(java.math.BigInteger value, boolean typed, String ui, int bits) {
			this.value = value;
			this.typed = typed;
			this.ui = ui;
			this.bits = bits;
			this.initialized = value != null;
		}

		// Constructor for uninitialized typed variables
		private TypedValue(String ui, int bits, boolean isTyped) {
			this.value = null;
			this.typed = isTyped;
			this.ui = isTyped ? ui : null;
			this.bits = isTyped ? bits : 0;
			this.initialized = false;
		}

		// Factory method for uninitialized typed variables
		static TypedValue uninitializedTyped(String ui, int bits) {
			return new TypedValue(ui, bits, true);
		}

		// Factory method for uninitialized untyped variables
		static TypedValue uninitializedUntyped() {
			return new TypedValue(null, 0, false);
		}
	}

	public static String interpret(String input) {
		if (input == null) {
			return null;
		}

		input = input.trim();

		// Handle let bindings
		java.util.regex.Matcher letMatcher = Patterns.let.matcher(input);
		if (letMatcher.matches()) {
			String varName = letMatcher.group(1);
			String ui = letMatcher.group(2);
			int bits = Integer.parseInt(letMatcher.group(3));
			String valueExpr = letMatcher.group(4);

			// Check for duplicate variable
			if (variables.containsKey(varName)) {
				throw new IllegalArgumentException("Variable " + varName + " is already defined");
			}

			// Parse and validate the value
			TypedValue typedVal = parseTypedValue(valueExpr);

			// Verify the value matches the declared type
			if (typedVal.typed && (!typedVal.ui.equals(ui) || typedVal.bits != bits)) {
				throw new IllegalArgumentException("Type mismatch for variable " + varName + ": expected " + ui + bits
						+ " but got " + typedVal.ui + typedVal.bits);
			}

			// If untyped value, validate it fits in the declared type
			if (!typedVal.typed) {
				java.math.BigInteger[] range = rangeFor(ui, bits);
				if (typedVal.value.compareTo(range[0]) < 0 || typedVal.value.compareTo(range[1]) > 0) {
					throw new IllegalArgumentException("Value out of range for " + ui + bits + " suffix: " + valueExpr);
				}
				typedVal = new TypedValue(typedVal.value, true, ui, bits);
			}

			// Store the variable
			variables.put(varName, typedVal);
			return "";
		}

		// Handle untyped let bindings
		java.util.regex.Matcher letUntypedMatcher = Patterns.letUntyped.matcher(input);
		if (letUntypedMatcher.matches()) {
			String varName = letUntypedMatcher.group(1);
			String valueExpr = letUntypedMatcher.group(2);

			// Check for duplicate variable
			if (variables.containsKey(varName)) {
				throw new IllegalArgumentException("Variable " + varName + " is already defined");
			}

			// Parse the value (will be untyped)
			TypedValue typedVal = parseTypedValue(valueExpr);

			// Store the variable
			variables.put(varName, typedVal);
			return "";
		}

		// Handle uninitialized untyped let declarations (let y;)
		java.util.regex.Matcher letUntypedUninitializedMatcher = Patterns.letUntypedUninitialized.matcher(input);
		if (letUntypedUninitializedMatcher.matches()) {
			String varName = letUntypedUninitializedMatcher.group(1);

			// Check for duplicate variable
			if (variables.containsKey(varName)) {
				throw new IllegalArgumentException("Variable " + varName + " is already defined");
			}

			// Store uninitialized untyped variable
			variables.put(varName, TypedValue.uninitializedUntyped());
			return "";
		}

		// Handle uninitialized typed let declarations (let x : I32;)
		java.util.regex.Matcher letUninitializedMatcher = Patterns.letUninitialized.matcher(input);
		if (letUninitializedMatcher.matches()) {
			String varName = letUninitializedMatcher.group(1);
			String ui = letUninitializedMatcher.group(2);
			int bits = Integer.parseInt(letUninitializedMatcher.group(3));

			// Check for duplicate variable
			if (variables.containsKey(varName)) {
				throw new IllegalArgumentException("Variable " + varName + " is already defined");
			}

			// Store uninitialized variable
			variables.put(varName, TypedValue.uninitializedTyped(ui, bits));
			return "";
		}

		// Handle assignment statements (x = value;)
		java.util.regex.Matcher assignmentMatcher = Patterns.assignment.matcher(input);
		if (assignmentMatcher.matches()) {
			String varName = assignmentMatcher.group(1);
			String valueExpr = assignmentMatcher.group(2);

			// Check if variable exists
			if (!variables.containsKey(varName)) {
				throw new IllegalArgumentException("Variable " + varName + " is not defined");
			}

			TypedValue existingVar = variables.get(varName);
			TypedValue newVal = parseTypedValue(valueExpr);

			// If variable is typed, validate assignment value matches type
			if (existingVar.typed) {
				if (newVal.typed && (!newVal.ui.equals(existingVar.ui) || newVal.bits != existingVar.bits)) {
					throw new IllegalArgumentException("Type mismatch for assignment to " + varName + ": expected "
							+ existingVar.ui + existingVar.bits + " but got " + newVal.ui + newVal.bits);
				}
				if (!newVal.typed) {
					java.math.BigInteger[] range = rangeFor(existingVar.ui, existingVar.bits);
					if (newVal.value.compareTo(range[0]) < 0 || newVal.value.compareTo(range[1]) > 0) {
						throw new IllegalArgumentException(
								"Value out of range for " + existingVar.ui + existingVar.bits + " suffix: " + valueExpr);
					}
					newVal = new TypedValue(newVal.value, true, existingVar.ui, existingVar.bits);
				}
			}

			// Update the variable
			variables.put(varName, newVal);
			return "";
		}

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

		// Handle built-in max<TYPE>() function
		java.util.regex.Matcher maxMatcher = Patterns.max.matcher(input);
		if (maxMatcher.matches()) {
			String ui = maxMatcher.group(1);
			int bits = Integer.parseInt(maxMatcher.group(2));
			java.math.BigInteger[] range = rangeFor(ui, bits);
			java.math.BigInteger maxVal = range[1];
			return maxVal.toString() + ui + bits;
		}

		// Check if input is a variable reference
		if (variables.containsKey(input)) {
			TypedValue var = variables.get(input);
			if (!var.initialized) {
				throw new IllegalArgumentException("Variable " + input + " is not initialized");
			}
			return var.value.toString();
		}

		// If input is a typed or plain integer, delegate to parseTypedValue
		if (Patterns.typed.matcher(input).matches() || Patterns.leadingInt.matcher(input).find()) {
			return parseTypedValue(input).value.toString();
		}

		java.util.regex.Matcher m = Patterns.leadingInt.matcher(input);
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

		// Handle variable references
		if (variables.containsKey(input)) {
			return variables.get(input);
		}

		java.util.regex.Matcher typedM = Patterns.typed.matcher(input);
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
		java.util.regex.Matcher m = Patterns.leadingInt.matcher(input);
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

	// Package-private method for testing - clears all variables
	static void clearVariables() {
		variables.clear();
	}
}
