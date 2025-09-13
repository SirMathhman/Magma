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
		// Support a simple addition expression like "1 + 2" using simple string
		// operations
		int plus = normalized.indexOf('+');
		if (plus > 0) {
			String left = normalized.substring(0, plus).trim();
			String right = normalized.substring(plus + 1).trim();
			java.util.OptionalInt optA = parseLeadingInt(left);
			java.util.OptionalInt optB = parseLeadingInt(right);
			// If both operands had extra suffixes after their numeric prefix, treat as
			// invalid
			boolean leftHasSuffix = hasSuffixAfterLeadingDigits(left);
			boolean rightHasSuffix = hasSuffixAfterLeadingDigits(right);
			if (leftHasSuffix && rightHasSuffix) {
				return Result.error(new InterpreterError("Invalid operands with conflicting suffixes"));
			}
			if (optA.isPresent() && optB.isPresent()) {
				return Result.success(Integer.toString(optA.getAsInt() + optB.getAsInt()));
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
