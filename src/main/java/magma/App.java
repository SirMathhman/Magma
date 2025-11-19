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
			.compile("^\\s*([+-]?\\d+)(?:([UI])(\\d+))?\\s*$");
	private static final java.util.regex.Pattern LEADING_NUMBER_PATTERN = java.util.regex.Pattern
			.compile("^(-?)(\\d+)(?:([UI])(\\d+))?");

	private static final record OperandInfo(java.math.BigInteger value, java.util.Optional<String> typeFull) {
	}

	private static final record OperandSequence(java.util.List<java.math.BigInteger> values,
		java.util.Optional<String> typeFull) {
	}

	/**
	 * Return the leading decimal digit sequence from the provided non-null input.
	 *
	 * @param input the non-null input string
	 * @return the leading decimal digit sequence, or empty string when none exist
	 */
	public static Result<String, String> interpret(String input) {
		var addRes = handleAddition(input);
		if (addRes.isPresent()) {
			return addRes.get();
		}
		var subRes = handleSubtraction(input);
		if (subRes.isPresent()) {
			return subRes.get();
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

	private static Result<String, String> wrapNumericResult(java.math.BigInteger value,
			java.util.Optional<String> typeFull) {
		if (typeFull.isPresent()) {
			return enforceResultBound(value, typeFull.get());
		}
		return new Result.Ok<>(value.toString());
	}

	private static java.util.Optional<Result<OperandInfo, String>> parseOperandToken(String raw) {
		java.util.regex.Matcher matcher = OPERAND_PATTERN.matcher(raw);
		if (!matcher.matches()) {
			return java.util.Optional.empty();
		}
		String digitsWithSign = matcher.group(1);
		var typeLetter = java.util.Optional.ofNullable(matcher.group(2));
		var width = java.util.Optional.ofNullable(matcher.group(3));
		var typeFull = typeLetter.flatMap(t -> width.map(w -> t + w));
		return java.util.Optional.of(parseOperandValue(digitsWithSign, typeLetter, width)
				.map(value -> new OperandInfo(value, typeFull)));
	}

	private static java.util.Optional<Result<OperandSequence, String>> parseOperandSequence(
			String[] rawOperands) {
		java.util.List<java.math.BigInteger> values = new java.util.ArrayList<>();
		java.util.Optional<String> resolvedType = java.util.Optional.empty();
		for (String raw : rawOperands) {
			var operandOpt = parseOperandToken(raw);
			if (operandOpt.isEmpty()) {
				return java.util.Optional.empty();
			}
			var operandRes = operandOpt.get();
			if (operandRes instanceof Result.Err<OperandInfo, String> err) {
				return java.util.Optional.of(new Result.Err<>(err.error()));
			}
			OperandInfo operand = ((Result.Ok<OperandInfo, String>) operandRes).value();
			values.add(operand.value());
			var operandType = operand.typeFull();
			if (operandType.isPresent()) {
				if (resolvedType.isPresent()) {
					if (!resolvedType.get().equals(operandType.get())) {
						return java.util.Optional.of(new Result.Err<>("operand types differ"));
					}
				} else {
					resolvedType = operandType;
				}
			}
		}
		return java.util.Optional.of(new Result.Ok<>(new OperandSequence(values, resolvedType)));
	}

	private static java.util.Optional<Result<String, String>> accumulateOperands(String[] operands,
			java.util.function.Function<OperandSequence, Result<String, String>> combiner) {
		var sequenceOpt = parseOperandSequence(operands);
		if (sequenceOpt.isEmpty()) {
			return java.util.Optional.empty();
		}
		return sequenceOpt.map(sequenceRes -> sequenceRes.flatMap(combiner));
	}

	private static java.util.Optional<Result<String, String>> handleAddition(String input) {
		if (!input.contains("+")) {
			return java.util.Optional.empty();
		}
		String[] operands = input.split("\\+");
		if (operands.length < 2) {
			return java.util.Optional.empty();
		}
		return accumulateOperands(operands, sequence -> {
			java.math.BigInteger sum = java.math.BigInteger.ZERO;
			for (java.math.BigInteger value : sequence.values()) {
				sum = sum.add(value);
			}
			return wrapNumericResult(sum, sequence.typeFull());
		});
	}

	private static java.util.Optional<Result<String, String>> handleSubtraction(String input) {
		java.util.regex.Pattern subtractPattern = java.util.regex.Pattern
				.compile("^\\s*([+-]?\\d+)(?:([UI])(\\d+))?\\s*-\\s*([+-]?\\d+)(?:([UI])(\\d+))?\\s*$");
		java.util.regex.Matcher subtractMatcher = subtractPattern.matcher(input);
		if (subtractMatcher.find()) {
			String[] operands = { subtractMatcher.group(1), subtractMatcher.group(4) };
			return accumulateOperands(operands, sequence -> {
				java.math.BigInteger result = sequence.values().get(0).subtract(sequence.values().get(1));
				return wrapNumericResult(result, sequence.typeFull());
			});
		}
		return java.util.Optional.empty();
	}
}