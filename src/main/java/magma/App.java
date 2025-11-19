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
		// Parse an optional sign, digits, and optional type suffix like U8 or I8.
		java.util.regex.Pattern p = java.util.regex.Pattern.compile("^(-?)(\\d+)(?:([UI])(\\d+))?");
		java.util.regex.Matcher m = p.matcher(input);
		if (m.find()) {
			String sign = m.group(1); // "" or "-"
			String digits = m.group(2);
			String typeLetter = m.group(3); // null, "U" or "I"
			String widthStr = m.group(4); // number of bits like 8, 16, 32

			boolean negative = "-".equals(sign);

			// If there is a type suffix, enforce bounds.
			if (typeLetter != null && widthStr != null) {
				String fullType = typeLetter + widthStr; // e.g., U8, I16
				java.util.Set<String> supported = java.util.Set.of("U6","U8","U32","U64","I8","I16","I32","I64");
				if (supported.contains(fullType)) {
					int bits = Integer.parseInt(widthStr);
					java.math.BigInteger value = new java.math.BigInteger(digits);

					if ("U".equals(typeLetter)) {
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
}
