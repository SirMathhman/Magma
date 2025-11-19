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
					var parsed = parseOperandValue(digitsWithSign, typeLetter, widthStr);
					if (parsed instanceof Result.Err<java.math.BigInteger, String> err) {
						return new Result.Err<>(err.error());
					}
					return new Result.Ok<>(((Result.Ok<java.math.BigInteger, String>) parsed).value().toString());
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

	private static java.util.Optional<Result<ArithmeticSequence, String>> parseArithmeticSequence(String input) {
		int length = input.length();
		java.util.regex.Matcher matcher = OPERAND_PATTERN.matcher(input);
		int cursor = 0;
		java.util.List<java.math.BigInteger> values = new java.util.ArrayList<>();
		java.util.List<Character> operators = new java.util.ArrayList<>();
		java.util.Optional<String> resolvedType = java.util.Optional.empty();
		boolean matched = false;
		while (true) {
			matcher.region(cursor, length);
			if (!matcher.lookingAt()) {
				break;
			}
			matched = true;
			String digitsWithSign = matcher.group(1);
			var typeLetter = java.util.Optional.ofNullable(matcher.group(2));
			var width = java.util.Optional.ofNullable(matcher.group(3));
			var operandRes = parseOperandValue(digitsWithSign, typeLetter, width);
			if (operandRes instanceof Result.Err<java.math.BigInteger, String> err) {
				return java.util.Optional.of(new Result.Err<>(err.error()));
			}
			java.math.BigInteger value = ((Result.Ok<java.math.BigInteger, String>) operandRes).value();
			values.add(value);
			var typeFull = typeLetter.flatMap(t -> width.map(w -> t + w));
			if (typeFull.isPresent()) {
				if (resolvedType.isPresent()) {
					if (!resolvedType.get().equals(typeFull.get())) {
						return java.util.Optional.of(new Result.Err<>("operand types differ"));
					}
				} else {
					resolvedType = typeFull;
				}
			}
			cursor = matcher.end();
			cursor = skipWhitespace(input, cursor, length);
			if (cursor >= length) {
				break;
			}
			char op = input.charAt(cursor);
			if (op != '+' && op != '-' && op != '*') {
				return java.util.Optional.empty();
			}
			operators.add(op);
			cursor++;
		}
		if (!matched || values.isEmpty()) {
			return java.util.Optional.empty();
		}
		cursor = skipWhitespace(input, cursor, length);
		if (cursor != length || values.size() - 1 != operators.size()) {
			return java.util.Optional.empty();
		}
		return java.util.Optional.of(new Result.Ok<>(new ArithmeticSequence(values, operators, resolvedType)));
	}

	private static java.math.BigInteger evaluateSequence(java.util.List<java.math.BigInteger> values,
			java.util.List<Character> operators) {
		java.math.BigInteger result = values.get(0);
		for (int i = 1; i < values.size(); i++) {
			char op = operators.get(i - 1);
			java.math.BigInteger operand = values.get(i);
			if (op == '+') {
				result = result.add(operand);
			} else if (op == '-') {
				result = result.subtract(operand);
			} else {
				result = result.multiply(operand);
			}
		}
		return result;
	}

	private static java.util.Optional<Result<String, String>> handleArithmetic(String input) {
		var parsed = parseArithmeticSequence(input);
		if (parsed.isEmpty()) {
			return java.util.Optional.empty();
		}
		return parsed.map(sequenceRes -> sequenceRes.flatMap(sequence -> {
			java.math.BigInteger total = evaluateSequence(sequence.values(), sequence.operators());
			if (sequence.resolvedType().isPresent()) {
				return enforceResultBound(total, sequence.resolvedType().get());
			}
			return new Result.Ok<>(total.toString());
		}));
	}
}