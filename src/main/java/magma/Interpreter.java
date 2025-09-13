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
		// Support expressions containing +, - and * . We tokenise into operands and
		// operators and evaluate with multiplication having higher precedence.
		if (normalized.contains("+") || normalized.contains("-") || normalized.contains("*") || normalized.contains("/")
				|| normalized.contains("%")) {
			// Tokenize: split on operator characters to be permissive with spacing.
			java.util.List<String> tokens = new java.util.ArrayList<>();
			int idx = 0;
			while (idx < normalized.length()) {
				char c = normalized.charAt(idx);
				if (c == '+' || c == '-' || c == '*' || c == '/' || c == '%') {
					tokens.add(Character.toString(c));
					idx++;
					continue;
				}
				// collect until next operator
				int start = idx;
				while (idx < normalized.length() && normalized.charAt(idx) != '+' && normalized.charAt(idx) != '-'
						&& normalized.charAt(idx) != '*' && normalized.charAt(idx) != '/' && normalized.charAt(idx) != '%') {
					idx++;
				}
				tokens.add(normalized.substring(start, idx).trim());
			}

			if (tokens.isEmpty()) {
				return Result.error(new InterpreterError("Invalid expression", normalized, java.util.List.of()));
			}

			// Build operands and operators lists
			java.util.List<Operand> operands = new java.util.ArrayList<>();
			java.util.List<String> operators = new java.util.ArrayList<>();

			java.util.Optional<Operand> firstOpOpt = parseOperand(tokens.get(0));
			if (firstOpOpt.isEmpty()) {
				return Result.error(new InterpreterError("Invalid operand", normalized, java.util.List.of()));
			}
			operands.add(firstOpOpt.get());
			for (int t = 1; t < tokens.size(); t += 2) {
				String opTok = tokens.get(t);
				if (!("+".equals(opTok) || "-".equals(opTok) || "*".equals(opTok) || "/".equals(opTok) || "%".equals(opTok))) {
					return Result
							.error(new InterpreterError("Expected operator but found: " + opTok, normalized, java.util.List.of()));
				}
				if (t + 1 >= tokens.size()) {
					return Result
							.error(new InterpreterError("Trailing operator without operand", normalized, java.util.List.of()));
				}
				operators.add(opTok);
				java.util.Optional<Operand> nextOp = parseOperand(tokens.get(t + 1));
				if (nextOp.isEmpty()) {
					return Result.error(new InterpreterError("Invalid operand", normalized, java.util.List.of()));
				}
				operands.add(nextOp.get());
			}

			// Common suffix reference (preserve previous behaviour: prefer first operand's
			// suffix)
			java.util.concurrent.atomic.AtomicReference<java.util.Optional<String>> commonSuffixRef = new java.util.concurrent.atomic.AtomicReference<>(
					java.util.Optional.empty());
			if (!operands.get(0).suffix().isEmpty()) {
				commonSuffixRef.set(java.util.Optional.of(operands.get(0).suffix()));
			}

			// First pass: collapse multiplications
			for (int j = 0; j < operators.size();) {
				String op = operators.get(j);
				if ("*".equals(op) || "/".equals(op) || "%".equals(op)) {
					Operand a = operands.get(j);
					Operand b = operands.get(j + 1);
					java.util.Optional<String> mergeErr = mergeSuffix(commonSuffixRef, b.suffix());
					if (mergeErr.isPresent()) {
						return Result
								.error(new InterpreterError("Conflicting suffixes in expression", normalized, java.util.List.of()));
					}
					int newVal;
					if ("*".equals(op)) {
						newVal = a.value() * b.value();
					} else {
						if ("/".equals(op)) {
							if (b.value() == 0) {
								return Result.error(new InterpreterError("Division by zero", normalized, java.util.List.of()));
							}
							newVal = a.value() / b.value();
						} else {
							// modulus
							if (b.value() == 0) {
								return Result.error(new InterpreterError("Division by zero", normalized, java.util.List.of()));
							}
							newVal = a.value() % b.value();
						}
					}
					String newSfx = commonSuffixRef.get().orElse("");
					Operand merged = new Operand(newVal, newSfx);
					operands.set(j, merged);
					operands.remove(j + 1);
					operators.remove(j);
					// don't increment j; check again at same position
				} else {
					j++;
				}
			}

			// Second pass: evaluate + and - left-to-right
			int acc = operands.get(0).value();
			for (int k = 0; k < operators.size(); k++) {
				String op = operators.get(k);
				Operand right = operands.get(k + 1);
				java.util.Optional<String> mergeErr = mergeSuffix(commonSuffixRef, right.suffix());
				if (mergeErr.isPresent()) {
					return Result
							.error(new InterpreterError("Conflicting suffixes in expression", normalized, java.util.List.of()));
				}
				if ("+".equals(op)) {
					acc = acc + right.value();
				} else if ("-".equals(op)) {
					acc = acc - right.value();
				} else {
					return Result.error(new InterpreterError("Unsupported operator after precedence pass: " + op, normalized,
							java.util.List.of()));
				}
			}

			return Result.success(Integer.toString(acc));
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
	private java.util.Optional<String> mergeSuffix(
			java.util.concurrent.atomic.AtomicReference<java.util.Optional<String>> commonRef,
			String suffix) {
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
			return java.util.Optional.of("conflict");
		}
		return java.util.Optional.empty();
	}
}
