package magma;

import magma.Result.Err;
import magma.Result.Ok;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Core application class for Magma.
 * <p>
 * This class intentionally contains only a single public API method:
 * `interpret`.
 */
public class App {
	private record ArithmeticSequence(List<BigInteger> values, List<Character> operators,
																		Optional<String> resolvedType) {}

	private record OperandResult(BigInteger value, Optional<String> type, int nextCursor) {}

	private static final Set<String> SUPPORTED_TYPES = Set.of("U6", "U8", "U32", "U64", "I8", "I16", "I32", "I64");
	private static final Pattern OPERAND_PATTERN = Pattern.compile("^\\s*([+-]?\\d+)(?:([UI])(\\d+))?\\s*");
	private static final Pattern LEADING_NUMBER_PATTERN = Pattern.compile("^(-?)(\\d+)(?:([UI])(\\d+))?");

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

		var trimmed = input.trim();
		if ("true".equals(trimmed) || "false".equals(trimmed)) {
			return new Ok<String, String>(trimmed);
		}

		// Parse an optional sign, digits, and optional type suffix like U8 or I8.
		var m = LEADING_NUMBER_PATTERN.matcher(input);
		if (m.find()) {
			var sign = m.group(1); // "" or "-"
			var digits = m.group(2);
			var typeLetter = Optional.ofNullable(m.group(3));
			var widthStr = Optional.ofNullable(m.group(4));

			var negative = "-".equals(sign);

			if (typeLetter.isPresent() && widthStr.isPresent()) {
				var fullType = typeLetter.get() + widthStr.get();
				if (SUPPORTED_TYPES.contains(fullType)) {
					String digitsWithSign;
					if (negative) {
						digitsWithSign = "-" + digits;
					} else {
						digitsWithSign = digits;
					}
					return parseOperandValue(digitsWithSign, typeLetter, widthStr).map(BigInteger::toString);
				}
			}
			return new Ok<String, String>(digits);
		}
		return new Ok<String, String>("");
	}

	private static Result<BigInteger, String> parseOperandValue(String digitsWithSign,
																															Optional<String> typeLetter,
																															Optional<String> widthStr) {
		var val = new BigInteger(digitsWithSign);
		if (typeLetter.isPresent() && widthStr.isPresent()) {
			var fullType = typeLetter.get() + widthStr.get();
			if (!SUPPORTED_TYPES.contains(fullType)) {
				return new Err<BigInteger, String>("unsupported type");
			}
			return enforceNumericBound(val, fullType);
		}
		return new Ok<BigInteger, String>(val);
	}

	private static Result<BigInteger, String> enforceNumericBound(BigInteger value, String typeFull) {
		var bits = Integer.parseInt(typeFull.substring(1));
		if (typeFull.startsWith("U")) {
			if (value.signum() < 0) {
				return new Err<BigInteger, String>("negative numbers not allowed");
			}
			var max = BigInteger.ONE.shiftLeft(bits).subtract(BigInteger.ONE);
			if (value.compareTo(max) > 0) {
				return new Err<BigInteger, String>("unsigned overflow");
			}
		} else {
			var min = BigInteger.ONE.shiftLeft(bits - 1).negate();
			var max = BigInteger.ONE.shiftLeft(bits - 1).subtract(BigInteger.ONE);
			if (value.compareTo(min) < 0 || value.compareTo(max) > 0) {
				return new Err<BigInteger, String>("signed overflow");
			}
		}
		return new Ok<BigInteger, String>(value);
	}

	private static Result<String, String> enforceResultBound(BigInteger sum, String typeFull) {
		var boundCheck = enforceNumericBound(sum, typeFull);
		if (boundCheck instanceof Err<BigInteger, String>(var error)) {
			return new Err<String, String>(error);
		}
		return new Ok<String, String>(sum.toString());
	}

	private static int skipWhitespace(String input, int cursor, int length) {
		while (cursor < length && Character.isWhitespace(input.charAt(cursor))) {
			cursor++;
		}
		return cursor;
	}

	private static int findMatchingParenthesis(String input, int start) {
		var depth = 0;
		for (var i = start; i < input.length(); i++) {
			var current = input.charAt(i);
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
		var depth = 0;
		for (var i = 0; i < input.length() - 1; i++) {
			var c = input.charAt(i);
			if (c == '(') {
				depth++;
			} else if (c == ')') {
				depth--;
			} else if (depth == 0 && c == '=' && input.charAt(i + 1) == '=') {
				return i;
			}
		}
		return -1;
	}

	private static Optional<Result<String, String>> handleEqualityComparison(String input) {
		var eqIndex = findTopLevelEquality(input);
		if (eqIndex < 0) {
			return Optional.empty();
		}
		var left = input.substring(0, eqIndex).trim();
		var right = input.substring(eqIndex + 2).trim();
		// boolean equality
		if (("true".equals(left) || "false".equals(left)) && ("true".equals(right) || "false".equals(right))) {
			return Optional.of(new Ok<String, String>(Boolean.toString(left.equals(right))));
		}
		// numeric equality: parse both sides as arithmetic sequences
		var leftParsed = parseArithmeticSequence(left);
		var rightParsed = parseArithmeticSequence(right);
		if (leftParsed.isEmpty() || rightParsed.isEmpty()) {
			return Optional.empty();
		}
		var leftRes = leftParsed.get();
		var rightRes = rightParsed.get();
		if (leftRes instanceof Err<ArithmeticSequence, String>(var error)) {
			return Optional.of(new Err<String, String>(error));
		}
		if (rightRes instanceof Err<ArithmeticSequence, String>(var error)) {
			return Optional.of(new Err<String, String>(error));
		}
		var lSeq = ((Ok<ArithmeticSequence, String>) leftRes).value();
		var rSeq = ((Ok<ArithmeticSequence, String>) rightRes).value();
		var lEval = evaluateSequence(lSeq.values(), lSeq.operators());
		var rEval = evaluateSequence(rSeq.values(), rSeq.operators());
		if (lEval instanceof Err<BigInteger, String>(var error)) {
			return Optional.of(new Err<String, String>(error));
		}
		if (rEval instanceof Err<BigInteger, String>(var error)) {
			return Optional.of(new Err<String, String>(error));
		}
		var lVal = ((Ok<BigInteger, String>) lEval).value();
		var rVal = ((Ok<BigInteger, String>) rEval).value();
		// If both sides have resolved types, they must match
		if (lSeq.resolvedType().isPresent() && rSeq.resolvedType().isPresent() &&
				!lSeq.resolvedType().get().equals(rSeq.resolvedType().get())) {
			return Optional.of(new Err<String, String>("operand types differ"));
		}
		return Optional.of(new Ok<String, String>(Boolean.toString(lVal.equals(rVal))));
	}

	private static Optional<Result<String, String>> handleIfExpression(String input) {
		var start = skipWhitespace(input, 0, input.length());
		if (start >= input.length()) {
			return Optional.empty();
		}
		if (!input.startsWith("if", start)) {
			return Optional.empty();
		}
		var afterIf = start + 2;
		var idx = skipWhitespace(input, afterIf, input.length());
		if (idx >= input.length() || input.charAt(idx) != '(') {
			return Optional.empty();
		}
		var closing = findMatchingParenthesis(input, idx);
		if (closing == -1) {
			return Optional.empty();
		}
		var cond = input.substring(idx + 1, closing).trim();
		var elseIndex = findTopLevelElse(input, closing + 1);
		if (elseIndex == -1) {
			return Optional.empty();
		}
		var thenExpr = input.substring(closing + 1, elseIndex).trim();
		var elseExpr = input.substring(elseIndex + 4).trim();
		var condEval = evaluateConditionBoolean(cond);
		if (condEval instanceof Err<Boolean, String>(var error)) {
			return Optional.of(new Err<String, String>(error));
		}
		boolean condBool = ((Ok<Boolean, String>) condEval).value();
		Result<String, String> chosenRes;
		if (condBool) {
			chosenRes = interpret(thenExpr);
		} else {
			chosenRes = interpret(elseExpr);
		}
		if (chosenRes instanceof Err<String, String>(var error)) {
			return Optional.of(new Err<String, String>(error));
		}
		return Optional.of(chosenRes);
	}

	private static int findTopLevelElse(String input, int from) {
		var depth = 0;
		for (var i = from; i + 4 <= input.length(); i++) {
			var c = input.charAt(i);
			if (c == '(') {
				depth++;
			} else if (c == ')') {
				depth--;
			} else if (depth == 0 && input.startsWith("else", i)) {
				return i;
			}
		}
		return -1;
	}

	private static Result<Boolean, String> evaluateConditionBoolean(String cond) {
		var condRes = interpret(cond);
		if (condRes instanceof Err<String, String>(var error)) {
			return new Err<Boolean, String>(error);
		}
		var condVal = ((Ok<String, String>) condRes).value();
		if ("true".equals(condVal)) {
			return new Ok<Boolean, String>(true);
		}
		if ("false".equals(condVal)) {
			return new Ok<Boolean, String>(false);
		}
		try {
			var b = new BigInteger(condVal).signum() != 0;
			return new Ok<Boolean, String>(b);
		} catch (NumberFormatException nfe) {
			return new Err<Boolean, String>("invalid condition");
		}
	}

	private static boolean isOperatorChar(char op) {
		return op == '+' || op == '-' || op == '*' || op == '/';
	}

	private static Optional<Result<ArithmeticSequence, String>> parseArithmeticSequence(String input) {
		var length = input.length();
		var matcher = OPERAND_PATTERN.matcher(input);
		var cursor = 0;
		List<BigInteger> values = new ArrayList<BigInteger>();
		List<Character> operators = new ArrayList<Character>();
		Optional<String> resolvedType = Optional.empty();
		while (true) {
			cursor = skipWhitespace(input, cursor, length);
			if (cursor >= length) {
				break;
			}
			var operandResult = parseOperandAt(input, cursor, matcher);
			if (operandResult instanceof Err<Optional<OperandResult>, String>(var error)) {
				return Optional.of(new Err<ArithmeticSequence, String>(error));
			}
			var operand = ((Ok<Optional<OperandResult>, String>) operandResult).value();
			if (operand.isEmpty()) {
				return Optional.empty();
			}
			var result = operand.get();
			cursor = result.nextCursor();
			values.add(result.value());
			if (result.type().isPresent()) {
				if (resolvedType.isEmpty()) {
					resolvedType = result.type();
				} else if (!resolvedType.get().equals(result.type().get())) {
					return Optional.of(new Err<ArithmeticSequence, String>("operand types differ"));
				}
			}
			cursor = skipWhitespace(input, cursor, length);
			if (cursor >= length) {
				break;
			}
			var op = input.charAt(cursor);
			if (!isOperatorChar(op)) {
				return Optional.empty();
			}
			operators.add(op);
			cursor++;
		}
		cursor = skipWhitespace(input, cursor, length);
		if (cursor != length || values.isEmpty() || values.size() - 1 != operators.size()) {
			return Optional.empty();
		}
		return Optional.of(new Ok<ArithmeticSequence, String>(new ArithmeticSequence(values, operators, resolvedType)));
	}

	private static Result<Optional<OperandResult>, String> parseOperandAt(String input, int cursor, Matcher matcher) {
		var length = input.length();
		var current = input.charAt(cursor);
		if (current == '(') {
			var closing = findMatchingParenthesis(input, cursor);
			if (closing == -1) {
				return new Ok<Optional<OperandResult>, String>(Optional.empty());
			}
			var inner = input.substring(cursor + 1, closing);
			var innerSeq = parseArithmeticSequence(inner);
			if (innerSeq.isEmpty()) {
				return new Ok<Optional<OperandResult>, String>(Optional.empty());
			}
			var innerResult = innerSeq.get();
			if (innerResult instanceof Err<ArithmeticSequence, String>(var error)) {
				return new Err<Optional<OperandResult>, String>(error);
			}
			var sequence = ((Ok<ArithmeticSequence, String>) innerResult).value();
			var eval = evaluateSequence(sequence.values(), sequence.operators());
			if (eval instanceof Err<BigInteger, String>(var error)) {
				return new Err<Optional<OperandResult>, String>(error);
			}
			var value = ((Ok<BigInteger, String>) eval).value();
			return new Ok<Optional<OperandResult>, String>(Optional.of(new OperandResult(value,
																																									 sequence.resolvedType(),
																																									 closing + 1)));
		}
		matcher.region(cursor, length);
		if (!matcher.lookingAt()) {
			return new Ok<Optional<OperandResult>, String>(Optional.empty());
		}
		var digitsWithSign = matcher.group(1);
		var typeLetter = Optional.ofNullable(matcher.group(2));
		var width = Optional.ofNullable(matcher.group(3));
		var operandType = typeLetter.flatMap(t -> width.map(w -> t + w));
		return parseOperandValue(digitsWithSign, typeLetter, width).map(value -> Optional.of(new OperandResult(value,
																																																					 operandType,
																																																					 matcher.end())));
	}

	private static Result<BigInteger, String> evaluateSequence(List<BigInteger> values, List<Character> operators) {
		var result = values.getFirst();
		for (var i = 1; i < values.size(); i++) {
			char op = operators.get(i - 1);
			var operand = values.get(i);
			if (op == '+') {
				result = result.add(operand);
			} else if (op == '-') {
				result = result.subtract(operand);
			} else if (op == '*') {
				result = result.multiply(operand);
			} else if (op == '/') {
				if (operand.signum() == 0) {
					return new Err<BigInteger, String>("division by zero");
				}
				result = result.divide(operand);
			} else {
				return new Err<BigInteger, String>("unsupported operator");
			}
		}
		return new Ok<BigInteger, String>(result);
	}

	private static Optional<Result<String, String>> handleArithmetic(String input) {
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
			return Optional.empty();
		}
		return parsed.map(sequenceRes -> sequenceRes.flatMap(sequence -> evaluateSequence(sequence.values(),
																																											sequence.operators()).flatMap(
				total -> {
					if (sequence.resolvedType().isPresent()) {
						return enforceResultBound(total, sequence.resolvedType().get());
					}
					return new Ok<String, String>(total.toString());
				})));
	}
}