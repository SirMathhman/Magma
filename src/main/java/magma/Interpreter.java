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
			java.util.concurrent.atomic.AtomicReference<java.util.Optional<String>> commonSuffixRef = new java.util.concurrent.atomic.AtomicReference<>(
					java.util.Optional.empty());
			for (String part : parts) {
				java.util.Optional<Operand> op = parseOperand(part.trim());
				if (op.isEmpty()) {
					return Result.error(new InterpreterError("Invalid addition expression", normalized, java.util.List.of()));
				}
				Operand operand = op.get();
				sum += operand.value();
				java.util.Optional<InterpreterError> mergeErr = mergeSuffix(commonSuffixRef, operand.suffix(), normalized,
						"Conflicting suffixes in addition");
				if (mergeErr.isPresent()) {
					return Result.error(mergeErr.get());
				}
			}
			return Result.success(Integer.toString(sum));
		}

		// Support simple subtraction expressions like "10 - 4"
		if (normalized.contains("-")) {
			// For now, do not mix plus and minus in a single expression.
			if (normalized.contains("+")) {
				return Result.error(
						new InterpreterError("Mixed addition and subtraction not supported", normalized, java.util.List.of()));
			}
			String[] parts = normalized.split("\\-");
			if (parts.length == 0) {
				return Result.error(new InterpreterError("Invalid subtraction expression", normalized, java.util.List.of()));
			}
			java.util.Optional<Operand> firstOp = parseOperand(parts[0].trim());
			if (firstOp.isEmpty()) {
				return Result.error(new InterpreterError("Invalid subtraction operand", normalized, java.util.List.of()));
			}
			int value = firstOp.get().value();
			java.util.concurrent.atomic.AtomicReference<java.util.Optional<String>> commonSuffixRef = new java.util.concurrent.atomic.AtomicReference<>(
					java.util.Optional.empty());
			if (!firstOp.get().suffix().isEmpty()) {
				commonSuffixRef.set(java.util.Optional.of(firstOp.get().suffix()));
			}
			for (int k = 1; k < parts.length; k++) {
				java.util.Optional<Operand> op = parseOperand(parts[k].trim());
				if (op.isEmpty()) {
					return Result.error(new InterpreterError("Invalid subtraction operand", normalized, java.util.List.of()));
				}
				Operand operand = op.get();
				value -= operand.value();
				java.util.Optional<InterpreterError> mergeErr = mergeSuffix(commonSuffixRef, operand.suffix(), normalized,
						"Conflicting suffixes in subtraction");
				if (mergeErr.isPresent()) {
					return Result.error(mergeErr.get());
				}
			}
			return Result.success(Integer.toString(value));
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

	// Small holder for an operand's parsed integer value and optional suffix.
	private static record Operand(int value, String suffix) {
	}

	private java.util.Optional<Operand> parseOperand(String s) {
		java.util.OptionalInt opt = parseLeadingInt(s);
		if (!opt.isPresent()) {
			return java.util.Optional.empty();
		}
		int v = opt.getAsInt();
		String suffix = s.substring(getLeadingDigitsLength(s));
		return java.util.Optional.of(new Operand(v, suffix));
	}

	/**
	 * Merge a candidate suffix into the common-suffix holder. Returns an Optional
	 * containing
	 * an InterpreterError if the suffix conflicts with the existing common suffix.
	 */
	private java.util.Optional<InterpreterError> mergeSuffix(
			java.util.concurrent.atomic.AtomicReference<java.util.Optional<String>> commonRef,
			String suffix, String normalized, String conflictMessage) {
		String sfx = java.util.Objects.toString(suffix, "");
		if (sfx.isEmpty()) {
			return java.util.Optional.empty();
		}
		java.util.Optional<String> cur = commonRef.get();
		if (!cur.isPresent()) {
			commonRef.set(java.util.Optional.of(sfx));
			return java.util.Optional.empty();
		}
		if (!cur.get().equals(sfx)) {
			return java.util.Optional.of(new InterpreterError(conflictMessage, normalized, java.util.List.of()));
		}
		return java.util.Optional.empty();
	}
}
