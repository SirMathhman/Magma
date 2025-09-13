package magma;

import java.util.List;
import java.util.Optional;

import magma.Result.Err;
import magma.Result.Ok;

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
		String src = Optional.ofNullable(input).orElse("");
		if (src.isEmpty()) {
			return new Ok<>("");
		}
		// If the input is a simple integer literal, return it as the result
		if (src.matches("^[0-9]+$")) {
			return new Ok<>(src);
		}

		// If input starts with a numeric prefix, return that prefix (e.g. "5U8" -> "5")
		// Very small interpreter feature: handle simple addition expressions like "1 +
		// 2"
		// Accept forms with spaces around '+'; do not use regex to comply with
		// Checkstyle.
		// Support '+', '-', and '*' operators. Implement standard precedence:
		// evaluate '*' before '+' and '-', with left-to-right associativity.
		if (src.indexOf('+') >= 0 || src.indexOf('-') >= 0 || src.indexOf('*') >= 0) {
			java.util.List<String> terms = new java.util.ArrayList<>();
			java.util.List<Character> ops = new java.util.ArrayList<>();
			// use indexOf-based splitting to avoid duplicating character-iteration loops
			int pos = 0;
			while (pos < src.length()) {
				int nextPlus = src.indexOf('+', pos);
				int nextMinus = src.indexOf('-', pos);
				int nextMul = src.indexOf('*', pos);
				if (nextPlus == -1 && nextMinus == -1 && nextMul == -1) {
					terms.add(src.substring(pos).trim());
					break;
				}
				int nextOp = Integer.MAX_VALUE;
				char opChar = '?';
				if (nextPlus != -1 && nextPlus < nextOp) {
					nextOp = nextPlus;
					opChar = '+';
				}
				if (nextMinus != -1 && nextMinus < nextOp) {
					nextOp = nextMinus;
					opChar = '-';
				}
				if (nextMul != -1 && nextMul < nextOp) {
					nextOp = nextMul;
					opChar = '*';
				}
				terms.add(src.substring(pos, nextOp).trim());
				ops.add(opChar);
				pos = nextOp + 1;
			}
			if (terms.size() >= 2) {
				// First, parse all terms and enforce common suffix consistency across the whole
				// expression
				java.util.List<TermVal> tvals = new java.util.ArrayList<>();
				java.util.Optional<String> commonSuffix = java.util.Optional.empty();
				for (String t : terms) {
					java.util.Optional<TermVal> tvOpt = parseTerm(t);
					if (tvOpt.isEmpty())
						return new Err<>(new InterpreterError("invalid operands", src, List.of()));
					TermVal tv = tvOpt.get();
					tvals.add(tv);
					if (!tv.suffix().isEmpty()) {
						if (commonSuffix.isEmpty())
							commonSuffix = java.util.Optional.of(tv.suffix());
						else if (!commonSuffix.get().equals(tv.suffix()))
							return new Err<>(new InterpreterError("invalid operands", src, List.of()));
					}
				}

				// Convert to mutable lists of integer values and operators, then apply
				// precedence
				java.util.List<Integer> values = new java.util.ArrayList<>();
				for (TermVal tv : tvals)
					values.add(tv.value());

				// Evaluate all '*' first (left-to-right)
				int i = 0;
				while (i < ops.size()) {
					if (ops.get(i) == '*') {
						int a = values.get(i);
						int b = values.get(i + 1);
						values.set(i, a * b);
						values.remove(i + 1);
						ops.remove(i);
					} else {
						i++;
					}
				}

				// Then evaluate remaining + and - left-to-right
				int acc = values.get(0);
				for (i = 0; i < ops.size(); i++) {
					char op = ops.get(i);
					int v = values.get(i + 1);
					if (op == '+')
						acc = acc + v;
					else
						acc = acc - v;
				}
				return new Ok<>(Integer.toString(acc));
			}
		}

		// Helper-like inline: try to parse an int, or use leading digit prefix if
		// present
		// (kept local to avoid adding new methods or imports)

		// If input starts with a numeric prefix, return that prefix (e.g. "5U8" -> "5")
		java.util.Optional<String> leading = leadingDigits(src);
		if (leading.isPresent()) {
			return new Ok<>(leading.get());
		}

		// For other non-empty inputs, return a generic not-implemented error
		return new Err<>(new InterpreterError("not implemented", src, List.of()));
	}

	private static record TermVal(int value, String suffix) {
	}

	private static record TermResult(int value, java.util.Optional<String> updatedCommon) {
	}

	private java.util.Optional<TermResult> processTerm(String term, java.util.Optional<String> commonSuffix) {
		return parseTerm(term).flatMap(tv -> {
			java.util.Optional<java.util.Optional<String>> updatedOuter = updateCommonSuffix(commonSuffix, tv.suffix());
			return updatedOuter.map(u -> new TermResult(tv.value(), u));
		});
	}

	/**
	 * Parse a term into its integer value (using prefix rules) and its suffix
	 * string.
	 * Returns Optional.empty() if no numeric prefix could be found.
	 */
	private java.util.Optional<TermVal> parseTerm(String term) {
		java.util.Optional<Integer> v = tryParseOrPrefix(term);
		if (v.isEmpty())
			return java.util.Optional.empty();
		java.util.Optional<String> d = leadingDigits(term);
		String suffix = "";
		if (d.isPresent())
			suffix = term.substring(d.get().length());
		return java.util.Optional.of(new TermVal(v.get(), suffix));
	}

	/**
	 * Given the current commonSuffix (may be empty) and a new term suffix,
	 * return the updated commonSuffix (wrapped in Optional) or Optional.empty()
	 * if the suffixes conflict (invalid).
	 */
	private java.util.Optional<java.util.Optional<String>> updateCommonSuffix(java.util.Optional<String> common,
			String suffix) {
		suffix = java.util.Optional.ofNullable(suffix).orElse("");
		if (suffix.isEmpty())
			return java.util.Optional.of(common);
		if (common.isEmpty())
			return java.util.Optional.of(java.util.Optional.of(suffix));
		if (common.get().equals(suffix))
			return java.util.Optional.of(common);
		return java.util.Optional.empty();
	}

	/**
	 * Try to parse an integer from the given token. If parsing fails, extract a
	 * leading
	 * numeric prefix and return it as an Integer. Returns an empty Optional if no
	 * numeric
	 * prefix is found.
	 */
	private java.util.Optional<Integer> tryParseOrPrefix(String token) {
		try {
			return java.util.Optional.of(Integer.parseInt(token));
		} catch (NumberFormatException ignored) {
			java.util.Optional<String> digits = leadingDigits(token);
			if (digits.isEmpty()) {
				return java.util.Optional.empty();
			}
			try {
				return java.util.Optional.of(Integer.parseInt(digits.get()));
			} catch (NumberFormatException ex) {
				return java.util.Optional.empty();
			}
		}
	}

	/**
	 * Return the leading contiguous digit sequence of the input as an Optional
	 * string,
	 * or Optional.empty() if the input does not start with a digit.
	 */
	private java.util.Optional<String> leadingDigits(String s) {
		if (java.util.Optional.ofNullable(s).orElse("").isEmpty()) {
			return java.util.Optional.empty();
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c >= '0' && c <= '9') {
				sb.append(c);
			} else {
				break;
			}
		}
		if (sb.length() == 0) {
			return java.util.Optional.empty();
		}
		return java.util.Optional.of(sb.toString());
	}
}
