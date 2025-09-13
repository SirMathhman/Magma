package magma;

import magma.Result.Err;
import magma.Result.Ok;

import java.util.List;
import java.util.Optional;

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
		String[] plusParts = src.split("\\+");
		if (plusParts.length == 2) {
			String left = plusParts[0].trim();
			String right = plusParts[1].trim();
			try {
				int a = Integer.parseInt(left);
				int b = Integer.parseInt(right);
				return new Ok<>(Integer.toString(a + b));
			} catch (NumberFormatException ignored) {
				// Try extracting leading numeric prefixes from operands (e.g. "1U8" -> "1")
				java.util.Optional<Integer> aOpt = tryParseOrPrefix(left);
				java.util.Optional<Integer> bOpt = tryParseOrPrefix(right);
				if (aOpt.isPresent() && bOpt.isPresent()) {
					return new Ok<>(Integer.toString(aOpt.get() + bOpt.get()));
				}
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
