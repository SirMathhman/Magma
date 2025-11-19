package magma;

/**
 * Core application class for Magma.
 *
 * This class intentionally contains only a single public API method:
 * `interpret`.
 */
public class App {
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

		// Parse an optional sign, digits, and optional type suffix like U8 or I8.
		java.util.regex.Pattern p = java.util.regex.Pattern.compile("^(-?)(\\d+)(?:([UI])(\\d+))?");
		java.util.regex.Matcher m = p.matcher(input);
		if (m.find()) {
			String sign = m.group(1); // "" or "-"
			String digits = m.group(2);
			java.util.Optional<String> typeLetter = java.util.Optional.ofNullable(m.group(3));
			java.util.Optional<String> widthStr = java.util.Optional.ofNullable(m.group(4));

			boolean negative = "-".equals(sign);

			// If there is a type suffix, enforce bounds.
			if (typeLetter.isPresent() && widthStr.isPresent()) {
				String fullType = typeLetter.get() + widthStr.get(); // e.g., U8, I16
				java.util.Set<String> supported = java.util.Set.of("U6", "U8", "U32", "U64", "I8", "I16", "I32", "I64");
				if (supported.contains(fullType)) {
					int bits = Integer.parseInt(widthStr.get());
					java.math.BigInteger value = new java.math.BigInteger(digits);

					if ("U".equals(typeLetter.get())) {
						// Unsigned: negative not allowed and upper bound is 2^bits - 1.
						if (negative) {
							return new Result.Err<>("negative numbers not allowed");
						}
						java.math.BigInteger max = java.math.BigInteger.ONE.shiftLeft(bits).subtract(java.math.BigInteger.ONE);
						if (value.compareTo(max) > 0) {
							return new Result.Err<>("unsigned overflow");
						}
						return new Result.Ok<>(value.toString());
					} else { // Signed
						java.math.BigInteger min = java.math.BigInteger.ONE.shiftLeft(bits - 1).negate();
						java.math.BigInteger max = java.math.BigInteger.ONE.shiftLeft(bits - 1).subtract(java.math.BigInteger.ONE);
						java.math.BigInteger signedValue = negative ? value.negate() : value;
						if (signedValue.compareTo(min) < 0 || signedValue.compareTo(max) > 0) {
							return new Result.Err<>("signed overflow");
						}
						return new Result.Ok<>(signedValue.toString());
					}
				}
				// Unsupported type suffix: fall back to returning the digits as-is.
			}
			// Not negative: return digits as-is.
			return new Result.Ok<>(digits);
		}
		return new Result.Ok<>("");
	}

	private static Result<java.math.BigInteger, String> parseOperandValue(String digitsWithSign,
			java.util.Optional<String> typeLetter, java.util.Optional<String> widthStr) {
		java.math.BigInteger val = new java.math.BigInteger(digitsWithSign);
		if (typeLetter.isPresent() && widthStr.isPresent()) {
			String fullType = typeLetter.get() + widthStr.get();
			java.util.Set<String> supported = java.util.Set.of("U6", "U8", "U32", "U64", "I8", "I16", "I32", "I64");
			if (!supported.contains(fullType))
				return new Result.Err<>("unsupported type");
			int bits = Integer.parseInt(widthStr.get());
			if ("U".equals(typeLetter.get())) {
				if (val.signum() < 0)
					return new Result.Err<>("negative numbers not allowed");
				java.math.BigInteger max = java.math.BigInteger.ONE.shiftLeft(bits).subtract(java.math.BigInteger.ONE);
				if (val.compareTo(max) > 0)
					return new Result.Err<>("unsigned overflow");
			} else {
				java.math.BigInteger min = java.math.BigInteger.ONE.shiftLeft(bits - 1).negate();
				java.math.BigInteger max = java.math.BigInteger.ONE.shiftLeft(bits - 1).subtract(java.math.BigInteger.ONE);
				if (val.compareTo(min) < 0 || val.compareTo(max) > 0)
					return new Result.Err<>("signed overflow");
			}
		}
		return new Result.Ok<>(val);
	}

	private static Result<String, String> enforceResultBound(java.math.BigInteger sum, String typeFull) {
		int bits = Integer.parseInt(typeFull.substring(1));
		if (typeFull.startsWith("U")) {
			java.math.BigInteger max = java.math.BigInteger.ONE.shiftLeft(bits).subtract(java.math.BigInteger.ONE);
			if (sum.compareTo(max) > 0)
				return new Result.Err<>("unsigned overflow");
			return new Result.Ok<>(sum.toString());
		} else {
			java.math.BigInteger min = java.math.BigInteger.ONE.shiftLeft(bits - 1).negate();
			java.math.BigInteger max = java.math.BigInteger.ONE.shiftLeft(bits - 1).subtract(java.math.BigInteger.ONE);
			if (sum.compareTo(min) < 0 || sum.compareTo(max) > 0)
				return new Result.Err<>("signed overflow");
			return new Result.Ok<>(sum.toString());
		}

	}

	private static java.util.Optional<Result<String, String>> handleAddition(String input) {
		java.util.regex.Pattern addPattern = java.util.regex.Pattern
				.compile("^\\s*([+-]?\\d+)(?:([UI])(\\d+))?\\s*\\+\\s*([+-]?\\d+)(?:([UI])(\\d+))?\\s*$");
		java.util.regex.Matcher addMatcher = addPattern.matcher(input);
		if (addMatcher.find()) {
			String aStr = addMatcher.group(1);
			var aType = java.util.Optional.ofNullable(addMatcher.group(2));
			var aWidth = java.util.Optional.ofNullable(addMatcher.group(3));
			String bStr = addMatcher.group(4);
			var bType = java.util.Optional.ofNullable(addMatcher.group(5));
			var bWidth = java.util.Optional.ofNullable(addMatcher.group(6));

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

			java.math.BigInteger sum = aVal.add(bVal);

			var aTypeFull = aType.flatMap(t -> aWidth.map(w -> t + w));
			var bTypeFull = bType.flatMap(t -> bWidth.map(w -> t + w));
			if (aTypeFull.isPresent() && bTypeFull.isPresent() && aTypeFull.get().equals(bTypeFull.get())) {
				var boundRes = enforceResultBound(sum, aTypeFull.get());
				return java.util.Optional.of(boundRes);
			}
			return java.util.Optional.of(new Result.Ok<>(sum.toString()));
		}
		return java.util.Optional.empty();
	}
}