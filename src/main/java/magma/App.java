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
			String type = m.group(3); // null, "U" or "I"

			boolean negative = "-".equals(sign);
			if (negative) {
				// If no type or unsigned type, negative is an error.
				if (type == null || "U".equals(type)) {
					return new Result.Err<>("negative numbers not allowed");
				}
				// For signed type 'I', return the signed digits.
				return new Result.Ok<>("-" + digits);
			}

			// If unsigned 8-bit type and digits exceed 255, return Err.
			if ("U".equals(type) && "8".equals(m.group(4))) {
				java.math.BigInteger v = new java.math.BigInteger(digits);
				if (v.compareTo(java.math.BigInteger.valueOf(255)) > 0) {
					return new Result.Err<>("unsigned overflow");
				}
			}

			// Not negative: return digits as-is.
			return new Result.Ok<>(digits);
		}
		return new Result.Ok<>("");
	}
}
