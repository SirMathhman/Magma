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
		java.util.regex.Pattern p = java.util.regex.Pattern.compile("^(-?)(\\d+)(?:([UI])(\\d+))?");
		java.util.regex.Matcher m = p.matcher(input);
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

	private static java.util.Optional<Result<String, String>> handleAddition(String input) {
		if (!input.contains("+")) {
			return java.util.Optional.empty();
		}
		String[] operands = input.split("\\+");
		if (operands.length < 2) {
			return java.util.Optional.empty();
		}
		java.math.BigInteger sum = java.math.BigInteger.ZERO;
		java.util.Optional<String> resolvedType = java.util.Optional.empty();
		for (String raw : operands) {
			java.util.regex.Matcher matcher = OPERAND_PATTERN.matcher(raw);
			if (!matcher.matches()) {
				return java.util.Optional.empty();
			}
			String digitsWithSign = matcher.group(1);
			var typeLetter = java.util.Optional.ofNullable(matcher.group(2));
			var width = java.util.Optional.ofNullable(matcher.group(3));
			var operandRes = parseOperandValue(digitsWithSign, typeLetter, width);
			if (operandRes instanceof Result.Err<java.math.BigInteger, String> err) {
				return java.util.Optional.of(new Result.Err<>(err.error()));
			}
			sum = sum.add(((Result.Ok<java.math.BigInteger, String>) operandRes).value());
			if (typeLetter.isPresent() && width.isPresent()) {
				String typeFull = typeLetter.get() + width.get();
				if (resolvedType.isPresent()) {
					if (!resolvedType.get().equals(typeFull)) {
						return java.util.Optional.of(new Result.Err<>("operand types differ"));
					}
				} else {
					resolvedType = java.util.Optional.of(typeFull);
				}
			}
		}
		if (resolvedType.isPresent()) {
			return java.util.Optional.of(enforceResultBound(sum, resolvedType.get()));
		}
		return java.util.Optional.of(new Result.Ok<>(sum.toString()));
	}

	private static java.util.Optional<Result<String, String>> handleSubtraction(String input) {
		java.util.regex.Pattern subtractPattern = java.util.regex.Pattern
				.compile("^\\s*([+-]?\\d+)(?:([UI])(\\d+))?\\s*-\\s*([+-]?\\d+)(?:([UI])(\\d+))?\\s*$");
		java.util.regex.Matcher subtractMatcher = subtractPattern.matcher(input);
		if (subtractMatcher.find()) {
			String aStr = subtractMatcher.group(1);
			var aType = java.util.Optional.ofNullable(subtractMatcher.group(2));
			var aWidth = java.util.Optional.ofNullable(subtractMatcher.group(3));
			String bStr = subtractMatcher.group(4);
			var bType = java.util.Optional.ofNullable(subtractMatcher.group(5));
			var bWidth = java.util.Optional.ofNullable(subtractMatcher.group(6));

			var aRes = parseOperandValue(aStr, aType, aWidth);
			if (aRes instanceof Result.Err<java.math.BigInteger, String> errA) {
				return java.util.Optional.of(new Result.Err<>(errA.error()));
			}
			var bRes = parseOperandValue(bStr, bType, bWidth);
			if (bRes instanceof Result.Err<java.math.BigInteger, String> errB) {
				return java.util.Optional.of(new Result.Err<>(errB.error()));
			}

			java.math.BigInteger aVal = ((Result.Ok<java.math.BigInteger, String>) aRes).value();
			java.math.BigInteger bVal = ((Result.Ok<java.math.BigInteger, String>) bRes).value();
			java.math.BigInteger result = aVal.subtract(bVal);

			var aTypeFull = aType.flatMap(t -> aWidth.map(w -> t + w));
			var bTypeFull = bType.flatMap(t -> bWidth.map(w -> t + w));
			if (aTypeFull.isPresent() && bTypeFull.isPresent()) {
				if (!aTypeFull.get().equals(bTypeFull.get())) {
					return java.util.Optional.of(new Result.Err<>("operand types differ"));
				}
				var boundRes = enforceResultBound(result, aTypeFull.get());
				return java.util.Optional.of(boundRes);
			}
			return java.util.Optional.of(new Result.Ok<>(result.toString()));
		}
		return java.util.Optional.empty();
	}
}