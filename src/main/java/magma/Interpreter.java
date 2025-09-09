package magma;

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
		// return it as the program output. Otherwise, keep the previous stub
		// behavior and return Err with the source.
		if (source != null) {
			String s = source.trim();
			// Manual parse to avoid using java.util.regex which is banned by project
			// Accept: optional '+' or '-' sign, followed by one or more digits, optionally
			// followed by an alphabetic suffix like I32 or U8.
			int i = 0;
			int len = s.length();
			boolean negative = false;
			if (i < len && (s.charAt(i) == '+' || s.charAt(i) == '-')) {
				if (s.charAt(i) == '-') negative = true;
				i++;
			}
			int digitsStart = i;
			while (i < len && Character.isDigit(s.charAt(i))) {
				i++;
			}
			if (i > digitsStart) {
				String integerPart = s.substring(0, i);
				String suffix = s.substring(i).trim();
				// If no suffix, accept the integer literal as before
				if (suffix.isEmpty()) {
					return new Result.Ok<>(integerPart);
				}
				// Validate suffix pattern: one letter (I or U) followed by digits (8/16/32/64)
				if (suffix.length() >= 2 && Character.isLetter(suffix.charAt(0))) {
					char kind = Character.toUpperCase(suffix.charAt(0));
					String widthStr = suffix.substring(1);
					boolean widthDigits = true;
					for (int j = 0; j < widthStr.length(); j++) {
						if (!Character.isDigit(widthStr.charAt(j))) {
							widthDigits = false;
							break;
						}
					}
					if (widthDigits) {
						try {
							int width = Integer.parseInt(widthStr);
							// Only support 8,16,32,64
							if (width == 8 || width == 16 || width == 32 || width == 64) {
								// Validate range using BigInteger to avoid overflow
								java.math.BigInteger val = new java.math.BigInteger(integerPart);
								if (kind == 'U') {
									// Unsigned must be non-negative
									if (val.signum() < 0) {
										return new Result.Err<>(source);
									}
									java.math.BigInteger max = java.math.BigInteger.ONE.shiftLeft(width).subtract(java.math.BigInteger.ONE);
									if (val.compareTo(max) <= 0) {
										return new Result.Ok<>(integerPart);
									} else {
										return new Result.Err<>(source);
									}
								} else if (kind == 'I') {
									// Signed range: -(2^(width-1)) .. 2^(width-1)-1
									java.math.BigInteger min = java.math.BigInteger.ONE.shiftLeft(width - 1).negate();
									java.math.BigInteger max = java.math.BigInteger.ONE.shiftLeft(width - 1).subtract(java.math.BigInteger.ONE);
									if (val.compareTo(min) >= 0 && val.compareTo(max) <= 0) {
										return new Result.Ok<>(integerPart);
									} else {
										return new Result.Err<>(source);
									}
								}
							}
						} catch (NumberFormatException | ArithmeticException e) {
							// fall through to Err
						}
					}
				}
			}
		}
		return new Result.Err<>(source);
	}
}
