package magma;

/**
 * Core application class for Magma.
 *
 * This class intentionally contains only a single public API method:
 * `interpret`.
 */
public class App {
	private static final java.util.Set<String> SUPPORTED_TYPES = java.util.Set.of("U6", "U8", "U32", "U64", "I8", "I16",
			"I32", "I64");
	private static final java.util.regex.Pattern OPERAND_PATTERN = java.util.regex.Pattern
			.compile("^\\s*([+-]?\\d+)(?:([UI])(\\d+))?\\s*");
	private static final java.util.regex.Pattern LEADING_NUMBER_PATTERN = java.util.regex.Pattern
			.compile("^(-?)(\\d+)(?:([UI])(\\d+))?");

	/**
	 * Return the leading decimal digit sequence from the provided non-null input.
	 *
	 * @param input the non-null input string
	 * @return the leading decimal digit sequence, or empty string when none exist
	 */
	public static Result<String, String> interpret(String input) {
		var arithmeticRes = handleArithmetic(input);
		if (arithmeticRes.isPresent()) {
			return arithmeticRes.get();
		}

		String trimmed = input.trim();
		if ("true".equals(trimmed) || "false".equals(trimmed)) {
			return new Result.Ok<>(trimmed);
		}

		// Parse an optional sign, digits, and optional type suffix like U8 or I8.
		java.util.regex.Matcher m = LEADING_NUMBER_PATTERN.matcher(input);
		if (m.find()) {
			String sign = m.group(1); // "" or "-"
			String digits = m.group(2);
			java.util.Optional<String> typeLetter = java.util.Optional.ofNullable(m.group(3));
			java.util.Optional<String> widthStr = java.util.Optional.ofNullable(m.group(4));

			boolean negative = "-".equals(sign);

			if (typeLetter.isPresent() && widthStr.isPresent()) {
				String fullType = typeLetter.get() + widthStr.get();
				if (SUPPORTED_TYPES.contains(fullType)) {
					String digitsWithSign = negative ? "-" + digits : digits;
					return parseOperandValue(digitsWithSign, typeLetter, widthStr)
							.map(java.math.BigInteger::toString);
				}
			}
			return new Result.Ok<>(digits);
		}
		return new Result.Ok<>("");
	}

	private static Result<java.math.BigInteger, String> parseOperandValue(String digitsWithSign,
			java.util.Optional<String> typeLetter, java.util.Optional<String> widthStr) {
		java.math.BigInteger val = new java.math.BigInteger(digitsWithSign);
		if (typeLetter.isPresent() && widthStr.isPresent()) {
			String fullType = typeLetter.get() + widthStr.get();
			if (!SUPPORTED_TYPES.contains(fullType))
				return new Result.Err<>("unsupported type");
			return enforceNumericBound(val, fullType);
		}
		return new Result.Ok<>(val);
	}

	private static Result<java.math.BigInteger, String> enforceNumericBound(java.math.BigInteger value, String typeFull) {
		int bits = Integer.parseInt(typeFull.substring(1));
		if (typeFull.startsWith("U")) {
			if (value.signum() < 0)
				return new Result.Err<>("negative numbers not allowed");
			java.math.BigInteger max = java.math.BigInteger.ONE.shiftLeft(bits).subtract(java.math.BigInteger.ONE);
			if (value.compareTo(max) > 0)
				return new Result.Err<>("unsigned overflow");
			return new Result.Ok<>(value);
		} else {
			java.math.BigInteger min = java.math.BigInteger.ONE.shiftLeft(bits - 1).negate();
			java.math.BigInteger max = java.math.BigInteger.ONE.shiftLeft(bits - 1).subtract(java.math.BigInteger.ONE);
			if (value.compareTo(min) < 0 || value.compareTo(max) > 0)
				return new Result.Err<>("signed overflow");
			return new Result.Ok<>(value);
		}
	}

	private static Result<String, String> enforceResultBound(java.math.BigInteger sum, String typeFull) {
		var boundCheck = enforceNumericBound(sum, typeFull);
		if (boundCheck instanceof Result.Err<java.math.BigInteger, String> err)
			return new Result.Err<>(err.error());
		return new Result.Ok<>(sum.toString());
	}

	private static int skipWhitespace(String input, int cursor, int length) {
		while (cursor < length && Character.isWhitespace(input.charAt(cursor))) {
			cursor++;
		}
		return cursor;
	}

	private static final record ArithmeticSequence(java.util.List<java.math.BigInteger> values,
			java.util.List<Character> operators, java.util.Optional<String> resolvedType) {
	}

	private static final record OperandResult(java.math.BigInteger value, java.util.Optional<String> type,
			int nextCursor) {
	}

	private static int findMatchingParenthesis(String input, int start) {
		int depth = 0;
		for (int i = start; i < input.length(); i++) {
			char current = input.charAt(i);
			if (current == '(') {
				depth++;
			} else if (current == ')') {
				depth--;
				if (depth == 0) {
					return i;
				}
			}
		}
		return -1;
	}

	private static int findTopLevelEquality(String input) {
		int depth = 0;
		for (int i = 0; i < input.length() - 1; i++) {
			char c = input.charAt(i);
			if (c == '(')
				depth++;
			else if (c == ')')
				depth--;
			else if (depth == 0 && c == '=' && input.charAt(i + 1) == '=') {
				return i;
			}
		}
		return -1;
	}

	private static java.util.Optional<Result<String, String>> handleEqualityComparison(String input) {
		int eqIndex = findTopLevelEquality(input);
		if (eqIndex < 0) {
			return java.util.Optional.empty();
		}
		String left = input.substring(0, eqIndex).trim();
		String right = input.substring(eqIndex + 2).trim();
		// boolean equality
		if (("true".equals(left) || "false".equals(left)) && ("true".equals(right) || "false".equals(right))) {
			return java.util.Optional.of(new Result.Ok<>(Boolean.toString(left.equals(right))));
		}
		// numeric equality: parse both sides as arithmetic sequences
		var leftParsed = parseArithmeticSequence(left);
		var rightParsed = parseArithmeticSequence(right);
		if (leftParsed.isEmpty() || rightParsed.isEmpty()) {
			return java.util.Optional.empty();
		}
		var leftRes = leftParsed.get();
		var rightRes = rightParsed.get();
		if (leftRes instanceof Result.Err<ArithmeticSequence, String> lerr)
			return java.util.Optional.of(new Result.Err<>(lerr.error()));
		if (rightRes instanceof Result.Err<ArithmeticSequence, String> rerr)
			return java.util.Optional.of(new Result.Err<>(rerr.error()));
		ArithmeticSequence lSeq = ((Result.Ok<ArithmeticSequence, String>) leftRes).value();
		ArithmeticSequence rSeq = ((Result.Ok<ArithmeticSequence, String>) rightRes).value();
		var lEval = evaluateSequence(lSeq.values(), lSeq.operators());
		var rEval = evaluateSequence(rSeq.values(), rSeq.operators());
		if (lEval instanceof Result.Err<java.math.BigInteger, String> lerr2)
			return java.util.Optional.of(new Result.Err<>(lerr2.error()));
		if (rEval instanceof Result.Err<java.math.BigInteger, String> rerr2)
			return java.util.Optional.of(new Result.Err<>(rerr2.error()));
		java.math.BigInteger lVal = ((Result.Ok<java.math.BigInteger, String>) lEval).value();
		java.math.BigInteger rVal = ((Result.Ok<java.math.BigInteger, String>) rEval).value();
		// If both sides have resolved types, they must match
		if (lSeq.resolvedType().isPresent() && rSeq.resolvedType().isPresent()
				&& !lSeq.resolvedType().get().equals(rSeq.resolvedType().get())) {
			return java.util.Optional.of(new Result.Err<>("operand types differ"));
		}
		return java.util.Optional.of(new Result.Ok<>(Boolean.toString(lVal.equals(rVal))));
	}

	private static java.util.Optional<Result<String, String>> handleIfExpression(String input) {
		int start = skipWhitespace(input, 0, input.length());
		if (start >= input.length())
			return java.util.Optional.empty();
		if (!input.startsWith("if", start)) {
			return java.util.Optional.empty();
		}
		int afterIf = start + 2;
		int idx = skipWhitespace(input, afterIf, input.length());
		if (idx >= input.length() || input.charAt(idx) != '(') {
			return java.util.Optional.empty();
		}
		int closing = findMatchingParenthesis(input, idx);
		if (closing == -1) {
			return java.util.Optional.empty();
		}
		String cond = input.substring(idx + 1, closing).trim();
		int elseIndex = findTopLevelElse(input, closing + 1);
		if (elseIndex == -1) {
			return java.util.Optional.empty();
		}
		String thenExpr = input.substring(closing + 1, elseIndex).trim();
		String elseExpr = input.substring(elseIndex + 4).trim();
		var condEval = evaluateConditionBoolean(cond);
		if (condEval instanceof Result.Err<Boolean, String> err)
			return java.util.Optional.of(new Result.Err<>(err.error()));
		boolean condBool = ((Result.Ok<Boolean, String>) condEval).value();
		var chosenRes = interpret(condBool ? thenExpr : elseExpr);
		if (chosenRes instanceof Result.Err<String, String> cerr)
			return java.util.Optional.of(new Result.Err<>(cerr.error()));
		return java.util.Optional.of(chosenRes);
	}

	private static int findTopLevelElse(String input, int from) {
		int depth = 0;
		for (int i = from; i + 4 <= input.length(); i++) {
			char c = input.charAt(i);
			if (c == '(')
				depth++;
			else if (c == ')')
				depth--;
			else if (depth == 0 && input.startsWith("else", i)) {
				return i;
			}
		}
		return -1;
	}

	private static Result<Boolean, String> evaluateConditionBoolean(String cond) {
		var condRes = interpret(cond);
		if (condRes instanceof Result.Err<String, String> err)
			return new Result.Err<>(err.error());
		String condVal = ((Result.Ok<String, String>) condRes).value();
		if ("true".equals(condVal))
			return new Result.Ok<>(true);
		if ("false".equals(condVal))
			return new Result.Ok<>(false);
		try {
			boolean b = new java.math.BigInteger(condVal).signum() != 0;
			return new Result.Ok<>(b);
		} catch (NumberFormatException nfe) {
			return new Result.Err<>("invalid condition");
		}
	}

	private static boolean isOperatorChar(char op) {
		return op == '+' || op == '-' || op == '*' || op == '/';
	}

	private static java.util.Optional<Result<ArithmeticSequence, String>> parseArithmeticSequence(String input) {
		int length = input.length();
		java.util.regex.Matcher matcher = OPERAND_PATTERN.matcher(input);
		int cursor = 0;
		java.util.List<java.math.BigInteger> values = new java.util.ArrayList<>();
		java.util.List<Character> operators = new java.util.ArrayList<>();
		java.util.Optional<String> resolvedType = java.util.Optional.empty();
		while (true) {
			cursor = skipWhitespace(input, cursor, length);
			if (cursor >= length) {
				break;
			}
			Result<java.util.Optional<OperandResult>, String> operandResult = parseOperandAt(input, cursor, matcher);
			if (operandResult instanceof Result.Err<java.util.Optional<OperandResult>, String> err) {
				return java.util.Optional.of(new Result.Err<>(err.error()));
			}
			java.util.Optional<OperandResult> operand = ((Result.Ok<java.util.Optional<OperandResult>, String>) operandResult)
					.value();
			if (operand.isEmpty()) {
				return java.util.Optional.empty();
			}
			OperandResult result = operand.get();
			cursor = result.nextCursor();
			values.add(result.value());
			if (result.type().isPresent()) {
				if (resolvedType.isEmpty()) {
					resolvedType = result.type();
				} else if (!resolvedType.get().equals(result.type().get())) {
					return java.util.Optional.of(new Result.Err<>("operand types differ"));
				}
			}
			cursor = skipWhitespace(input, cursor, length);
			if (cursor >= length) {
				break;
			}
			char op = input.charAt(cursor);
			if (!isOperatorChar(op)) {
				return java.util.Optional.empty();
			}
			operators.add(op);
			cursor++;
		}
		cursor = skipWhitespace(input, cursor, length);
		if (cursor != length || values.isEmpty() || values.size() - 1 != operators.size()) {
			return java.util.Optional.empty();
		}
		return java.util.Optional.of(new Result.Ok<>(new ArithmeticSequence(values, operators, resolvedType)));
	}

	private static Result<java.util.Optional<OperandResult>, String> parseOperandAt(String input, int cursor,
			java.util.regex.Matcher matcher) {
		int length = input.length();
		char current = input.charAt(cursor);
		if (current == '(') {
			int closing = findMatchingParenthesis(input, cursor);
			if (closing == -1) {
				return new Result.Ok<>(java.util.Optional.empty());
			}
			String inner = input.substring(cursor + 1, closing);
			var innerSeq = parseArithmeticSequence(inner);
			if (innerSeq.isEmpty()) {
				return new Result.Ok<>(java.util.Optional.empty());
			}
			var innerResult = innerSeq.get();
			if (innerResult instanceof Result.Err<ArithmeticSequence, String> err) {
				return new Result.Err<>(err.error());
			}
			ArithmeticSequence sequence = ((Result.Ok<ArithmeticSequence, String>) innerResult).value();
			var eval = evaluateSequence(sequence.values(), sequence.operators());
			if (eval instanceof Result.Err<java.math.BigInteger, String> err) {
				return new Result.Err<>(err.error());
			}
			java.math.BigInteger value = ((Result.Ok<java.math.BigInteger, String>) eval).value();
			return new Result.Ok<>(java.util.Optional.of(new OperandResult(value, sequence.resolvedType(), closing + 1)));
		}
		matcher.region(cursor, length);
		if (!matcher.lookingAt()) {
			return new Result.Ok<>(java.util.Optional.empty());
		}
		String digitsWithSign = matcher.group(1);
		var typeLetter = java.util.Optional.ofNullable(matcher.group(2));
		var width = java.util.Optional.ofNullable(matcher.group(3));
		java.util.Optional<String> operandType = typeLetter.flatMap(t -> width.map(w -> t + w));
		return parseOperandValue(digitsWithSign, typeLetter, width).map(value -> java.util.Optional
				.of(new OperandResult(value, operandType, matcher.end())));
	}

	private static Result<java.math.BigInteger, String> evaluateSequence(java.util.List<java.math.BigInteger> values,
			java.util.List<Character> operators) {
		java.math.BigInteger result = values.get(0);
		for (int i = 1; i < values.size(); i++) {
			char op = operators.get(i - 1);
			java.math.BigInteger operand = values.get(i);
			if (op == '+') {
				result = result.add(operand);
			} else if (op == '-') {
				result = result.subtract(operand);
			} else if (op == '*') {
				result = result.multiply(operand);
			} else if (op == '/') {
				if (operand.signum() == 0) {
					return new Result.Err<>("division by zero");
				}
				result = result.divide(operand);
			} else {
				return new Result.Err<>("unsupported operator");
			}
		}
		return new Result.Ok<>(result);
	}

	private static java.util.Optional<Result<String, String>> handleArithmetic(String input) {
		var ifRes = handleIfExpression(input);
		if (ifRes.isPresent()) {
			return ifRes;
		}
		var equality = handleEqualityComparison(input);
		if (equality.isPresent()) {
			return equality;
		}

		var parsed = parseArithmeticSequence(input);
		if (parsed.isEmpty()) {
			return java.util.Optional.empty();
		}
		return parsed.map(sequenceRes -> sequenceRes
				.flatMap(sequence -> evaluateSequence(sequence.values(), sequence.operators()).flatMap(total -> {
					if (sequence.resolvedType().isPresent()) {
						return enforceResultBound(total, sequence.resolvedType().get());
					}
					return new Result.Ok<>(total.toString());
				})));
	}
}