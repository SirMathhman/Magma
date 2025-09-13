package magma;

/**
 * Simple interpreter stub.
 * The interpret method is a placeholder and should be implemented later.
 */
public class Interpreter {

	/**
	 * Interpret the provided input and return a Result.
	 * Behavior (stub): returns Result.success("") if input is exactly empty ("").
	 * Otherwise returns Result.error(InterpreterError).
	 */
	public Result<String, InterpreterError> interpret(String input) {
		String normalized = java.util.Objects.toString(input, "");
		if ("".equals(normalized)) {
			return Result.success("");
		}
		// Support chained addition expressions like "1 + 2 + 3" (arbitrary length)
		if (normalized.contains("+")) {
			String[] parts = normalized.split("\\+");
			int sum = 0;
			java.util.Optional<String> commonSuffix = java.util.Optional.empty();
			for (String part : parts) {
				String operand = part.trim();
				java.util.OptionalInt opt = parseLeadingInt(operand);
				if (!opt.isPresent()) {
					// if any operand doesn't have a leading int, fail the addition parsing
					sum = Integer.MIN_VALUE;
					break;
				}
				sum += opt.getAsInt();
				String suffix = operand.substring(getLeadingDigitsLength(operand));
				if (!suffix.isEmpty()) {
					if (!commonSuffix.isPresent()) {
						commonSuffix = java.util.Optional.of(suffix);
					} else if (!commonSuffix.get().equals(suffix)) {
						// conflicting suffixes -> invalid
						sum = Integer.MIN_VALUE;
						break;
					}
				}
			}
			if (sum != Integer.MIN_VALUE) {
				return Result.success(Integer.toString(sum));
			} else {
				// Addition parsing failed (conflicting suffixes or non-numeric operand).
				// Do not fall back to the leading-digit rule for the whole expression;
				// return an explicit interpreter error instead.
				return Result.error(new InterpreterError("Invalid addition expression", normalized, java.util.List.of()));
			}
		}

		// If input starts with digits, return the leading digit sequence using
		// simple String scanning (avoid java.util.regex per project policy)
		int i = 0;
		while (i < normalized.length() && Character.isDigit(normalized.charAt(i))) {
			i++;
		}
		if (i > 0) {
			return Result.success(normalized.substring(0, i));
		}
		return Result.error(new InterpreterError("Only empty input or numeric input is supported in this stub"));
	}

	private int getLeadingDigitsLength(String s) {
		int idx = 0;
		while (idx < s.length() && Character.isDigit(s.charAt(idx))) {
			idx++;
		}
		return idx;
	}

	// Helper: parse leading integer prefix from a string (e.g. "1U8" -> 1)
	// Returns OptionalInt.empty() if no leading digits are present or integer
	// parsing fails
	private java.util.OptionalInt parseLeadingInt(String s) {
		int idx = 0;
		while (idx < s.length() && Character.isDigit(s.charAt(idx))) {
			idx++;
		}
		if (idx == 0) {
			return java.util.OptionalInt.empty();
		}
		try {
			return java.util.OptionalInt.of(Integer.parseInt(s.substring(0, idx)));
		} catch (NumberFormatException ex) {
			return java.util.OptionalInt.empty();
		}
	}

	private boolean hasSuffixAfterLeadingDigits(String s) {
		int idx = 0;
		while (idx < s.length() && Character.isDigit(s.charAt(idx))) {
			idx++;
		}
		return idx < s.length();
	}
}
