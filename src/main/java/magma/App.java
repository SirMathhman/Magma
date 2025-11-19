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
 *
 * This class intentionally contains only a single public API method:
 * `interpret`.
 */
public class App {
	private record ArithmeticSequence(List<BigInteger> values, List<Character> operators,
																		Optional<String> resolvedType) {}

	private record OperandResult(BigInteger value, Optional<String> type, int nextCursor) {}

	private record IfParts(String cond, String thenExpr, String elseExpr) {}

	private record ValueWithType(BigInteger value, Optional<String> type) {}

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
					return parseOperandValue(digitsWithSign, typeLetter, widthStr).mapValue(BigInteger::toString);
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
			return new Ok<BigInteger, String>(value);
		} else {
			var min = BigInteger.ONE.shiftLeft(bits - 1).negate();
			var max = BigInteger.ONE.shiftLeft(bits - 1).subtract(BigInteger.ONE);
			if (value.compareTo(min) < 0 || value.compareTo(max) > 0) {
				return new Err<BigInteger, String>("signed overflow");
			}
			return new Ok<BigInteger, String>(value);
		}
	}

	private static Result<String, String> enforceResultBound(BigInteger sum, String typeFull) {
		return enforceNumericBound(sum, typeFull).mapValue(BigInteger::toString);
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
		if (leftRes instanceof Err<ArithmeticSequence, String>) {
			return Optional.of(new Err<String, String>(leftRes.asErr().get()));
		}
		if (rightRes instanceof Err<ArithmeticSequence, String>) {
			return Optional.of(new Err<String, String>(rightRes.asErr().get()));
		}
		var lSeq = leftRes.asOk().get();
		var rSeq = rightRes.asOk().get();
		var lEval = evaluateSequence(lSeq.values(), lSeq.operators());
		var rEval = evaluateSequence(rSeq.values(), rSeq.operators());
		if (lEval instanceof Err<BigInteger, String>) {
			return Optional.of(new Err<String, String>(lEval.asErr().get()));
		}
		if (rEval instanceof Err<BigInteger, String>) {
			return Optional.of(new Err<String, String>(rEval.asErr().get()));
		}
		var lVal = lEval.asOk().get();
		var rVal = rEval.asOk().get();
		// If both sides have resolved types, they must match
		if (lSeq.resolvedType().isPresent() && rSeq.resolvedType().isPresent() &&
				!lSeq.resolvedType().get().equals(rSeq.resolvedType().get())) {
			return Optional.of(new Err<String, String>("operand types differ"));
		}
		return Optional.of(new Ok<String, String>(Boolean.toString(lVal.equals(rVal))));
	}

	private static Optional<Result<String, String>> handleIfExpression(String input) {
		var ifParts = parseIfParts(input, 0);
		if (ifParts.isEmpty()) {
			return Optional.empty();
		}
		var parts = ifParts.get();
		var chosenRes = chooseIfBranch(parts);
		if (chosenRes instanceof Err<String, String>) {
			return Optional.of(new Err<String, String>(chosenRes.asErr().get()));
		}
		var chosen = chosenRes.asOk().get();
		var valRes = evaluateExpressionAsValue(chosen);
		if (valRes instanceof Ok<ValueWithType, String>) {
			var valOk = valRes.asOk().get();
			return Optional.of(new Ok<String, String>(valOk.value().toString()));
		}
		if (valRes instanceof Err<ValueWithType, String>) {
			var errMsg = valRes.asErr().get();
			if ("non-numeric expression".equals(errMsg)) {
				var chosenInterp = interpret(chosen);
				if (chosenInterp instanceof Err<String, String>) {
					return Optional.of(new Err<String, String>(chosenInterp.asErr().get()));
				}
				return Optional.of(chosenInterp);
			}
			return Optional.of(new Err<String, String>(errMsg));
		}
		return Optional.empty();
	}

	private static Optional<IfParts> parseIfParts(String input, int start) {
		var length = input.length();
		var pos = skipWhitespace(input, start, length);
		if (pos >= length || !input.startsWith("if", pos)) {
			return Optional.empty();
		}
		var afterIf = skipWhitespace(input, pos + 2, length);
		if (afterIf >= length || input.charAt(afterIf) != '(') {
			return Optional.empty();
		}
		var condClosing = findMatchingParenthesis(input, afterIf);
		if (condClosing == -1) {
			return Optional.empty();
		}
		var elseIndex = findTopLevelElse(input, condClosing + 1);
		if (elseIndex == -1) {
			return Optional.empty();
		}
		var cond = input.substring(afterIf + 1, condClosing).trim();
		var thenExpr = input.substring(condClosing + 1, elseIndex).trim();
		var elseExpr = input.substring(elseIndex + 4).trim();
		return Optional.of(new IfParts(cond, thenExpr, elseExpr));
	}

	private static Result<String, String> chooseIfBranch(IfParts parts) {
		var condEval = evaluateConditionBoolean(parts.cond());
		if (condEval instanceof Err<Boolean, String>) {
			return new Err<String, String>(condEval.asErr().get());
		}
		boolean condBool = condEval.asOk().get();
		if (condBool) {
			return new Ok<String, String>(parts.thenExpr());
		}
		return new Ok<String, String>(parts.elseExpr());
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
		var numeric = evaluateExpressionAsValue(cond);
		if (numeric instanceof Ok<ValueWithType, String>) {
			var v = numeric.asOk().get();
			return new Ok<Boolean, String>(v.value().signum() != 0);
		}
		if (numeric instanceof Err<ValueWithType, String>) {
			var msg = numeric.asErr().get();
			if ("non-numeric expression".equals(msg)) {
				var strRes = interpretAsString(cond);
				if (strRes instanceof Err<String, String>) {
					return new Err<Boolean, String>(strRes.asErr().get());
				}
				var sval = strRes.asOk().get();
				if ("true".equals(sval)) {
					return new Ok<Boolean, String>(true);
				}
				if ("false".equals(sval)) {
					return new Ok<Boolean, String>(false);
				}
				return new Err<Boolean, String>("invalid condition");
			}
			return new Err<Boolean, String>(msg);
		}
		return new Err<Boolean, String>("invalid condition");
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
			if (operandResult instanceof Err<Optional<OperandResult>, String>) {
				return Optional.of(new Err<ArithmeticSequence, String>(operandResult.asErr().get()));
			}
			var operand = operandResult.asOk().get();
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
			var innerTrim = inner.trim();
			// If the inner expression begins with 'if', parse it as an operand
			if (innerTrim.startsWith("if")) {
				return parseIfOperandFromInner(inner, closing);
			}
			var innerSeq = parseArithmeticSequence(inner);
			if (innerSeq.isEmpty()) {
				return new Ok<Optional<OperandResult>, String>(Optional.empty());
			}
			var innerResult = innerSeq.get();
			if (innerResult instanceof Err<ArithmeticSequence, String>) {
				return new Err<Optional<OperandResult>, String>(innerResult.asErr().get());
			}
			var sequence = innerResult.asOk().get();
			var eval = evaluateSequence(sequence.values(), sequence.operators());
			if (eval instanceof Err<BigInteger, String>) {
				return new Err<Optional<OperandResult>, String>(eval.asErr().get());
			}
			var value = eval.asOk().get();
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
		return parseOperandValue(digitsWithSign, typeLetter, width).mapValue(value -> Optional.of(new OperandResult(value,
																																																								operandType,
																																																								matcher.end())));
	}

	private static Result<Optional<OperandResult>, String> parseIfOperandFromInner(String inner, int closing) {
		var parsed = parseIfParts(inner, 0);
		if (parsed.isEmpty()) {
			return new Ok<Optional<OperandResult>, String>(Optional.empty());
		}
		var parts = parsed.get();
		var chosenRes = chooseIfBranch(parts);
		if (chosenRes instanceof Err<String, String>) {
			return new Err<Optional<OperandResult>, String>(chosenRes.asErr().get());
		}
		var chosen = chosenRes.asOk().get();
		var chosenVal = evaluateExpressionAsValue(chosen);
		if (chosenVal instanceof Err<ValueWithType, String>) {
			return new Err<Optional<OperandResult>, String>(chosenVal.asErr().get());
		}
		var vwt = chosenVal.asOk().get();
		return new Ok<Optional<OperandResult>, String>(Optional.of(new OperandResult(vwt.value(),
																																								 vwt.type(),
																																								 closing + 1)));
	}

	private static Result<ValueWithType, String> evaluateExpressionAsValue(String expr) {
		var parsed = parseArithmeticSequence(expr);
		if (parsed.isEmpty()) {
			var interpreted = interpret(expr);
			if (interpreted instanceof Err<String, String>) {
				return new Err<ValueWithType, String>(interpreted.asErr().get());
			}
			var val = interpreted.asOk().get();
			if ("true".equals(val) || "false".equals(val)) {
				return new Err<ValueWithType, String>("non-numeric expression");
			}
			try {
				var v = new BigInteger(val);
				return new Ok<ValueWithType, String>(new ValueWithType(v, Optional.empty()));
			} catch (NumberFormatException nfe) {
				return new Err<ValueWithType, String>("invalid numeric expression");
			}
		}
		var res = parsed.get();
		if (res instanceof Err<ArithmeticSequence, String>) {
			return new Err<ValueWithType, String>(res.asErr().get());
		}
		var seq = res.asOk().get();
		var eval = evaluateSequence(seq.values(), seq.operators());
		if (eval instanceof Err<BigInteger, String>) {
			return new Err<ValueWithType, String>(eval.asErr().get());
		}
		var value = eval.asOk().get();
		return new Ok<ValueWithType, String>(new ValueWithType(value, seq.resolvedType()));
	}

	private static Result<String, String> interpretAsString(String expr) {
		return interpret(expr);
	}

	private static Result<BigInteger, String> evaluateSequence(List<BigInteger> values, List<Character> operators) {
		var result = values.get(0);
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