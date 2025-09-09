package magma;

import java.util.Objects;

/**
 * Interpreter provides functionality to interpret source code with a given
 * input.
 */
public class Interpreter {

	/**
	 * Interpret the given source with the provided input and produce a result
	 * string.
	 *
	 * Currently this method is a stub and returns an error Result containing the
	 * offending source.
	 *
	 * @param source the source code to interpret
	 * @param input  the runtime input for the program
	 * @return the result of interpretation wrapped in a Result (Ok or Err)
	 */
	public Result<String, String> interpret(String source, String input) {
		// Minimal implementation: if the source is a simple integer literal,
		// return it as the program output. Otherwise return Err with the source.
		if (Objects.isNull(source))
			return new Result.Err<>("<missing source>");

		String s = source.trim();
		ParseResult pr = parseSignAndDigits(s);
		if (!pr.valid)
			return new Result.Err<>(source);

		// No suffix -> accept as-is
		if (pr.suffix.isEmpty())
			return new Result.Ok<>(pr.integerPart);

		if (!isValidSuffix(pr.suffix))
			return new Result.Err<>(source);

		char kind = Character.toUpperCase(pr.suffix.charAt(0));
		String widthStr = pr.suffix.substring(1);
		try {
			int width = Integer.parseInt(widthStr);
			if (!(width == 8 || width == 16 || width == 32 || width == 64))
				return new Result.Err<>(source);

			java.math.BigInteger val = new java.math.BigInteger(pr.integerPart);
			if (kind == 'U') {
				if (val.signum() < 0)
					return new Result.Err<>(source);
				if (fitsUnsigned(val, width))
					return new Result.Ok<>(pr.integerPart);
				return new Result.Err<>(source);
			} else if (kind == 'I') {
				if (fitsSigned(val, width))
					return new Result.Ok<>(pr.integerPart);
				return new Result.Err<>(source);
			}
		} catch (NumberFormatException | ArithmeticException e) {
			return new Result.Err<>(source);
		}

		return new Result.Err<>(source);
	}

	// Small helper struct to hold parse results
	private static final class ParseResult {
		final boolean valid;
		final String integerPart;
		final String suffix;

		private static final ParseResult INVALID = new ParseResult(false, "", "");

		ParseResult(boolean valid, String integerPart, String suffix) {
			this.valid = valid;
			this.integerPart = integerPart;
			this.suffix = suffix;
		}

		static ParseResult invalid() {
			return INVALID;
		}
	}

	// Parse optional sign and digits and return integer part and suffix
	private ParseResult parseSignAndDigits(String s) {
		int i = 0;
		int len = s.length();
		if (i < len && (s.charAt(i) == '+' || s.charAt(i) == '-'))
			i++;
		int digitsStart = i;
		while (i < len && Character.isDigit(s.charAt(i)))
			i++;
		if (i <= digitsStart)
			return ParseResult.invalid();
		String integerPart = s.substring(0, i);
		String suffix = s.substring(i).trim();
		return new ParseResult(true, integerPart, suffix);
	}

	private boolean isValidSuffix(String suffix) {
		if (suffix.length() < 2)
			return false;
		if (!Character.isLetter(suffix.charAt(0)))
			return false;
		String widthStr = suffix.substring(1);
		for (int j = 0; j < widthStr.length(); j++) {
			if (!Character.isDigit(widthStr.charAt(j)))
				return false;
		}
		return true;
	}

	private boolean fitsUnsigned(java.math.BigInteger val, int width) {
		java.math.BigInteger max = java.math.BigInteger.ONE.shiftLeft(width).subtract(java.math.BigInteger.ONE);
		return val.compareTo(max) <= 0;
	}

	private boolean fitsSigned(java.math.BigInteger val, int width) {
		java.math.BigInteger min = java.math.BigInteger.ONE.shiftLeft(width - 1).negate();
		java.math.BigInteger max = java.math.BigInteger.ONE.shiftLeft(width - 1).subtract(java.math.BigInteger.ONE);
		return val.compareTo(min) >= 0 && val.compareTo(max) <= 0;
	}
}
