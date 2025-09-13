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
				// fall through to next checks
			}
		}

		// If input starts with a numeric prefix, return that prefix (e.g. "5U8" -> "5")
		StringBuilder digits = new StringBuilder();
		for (int i = 0; i < src.length(); i++) {
			char c = src.charAt(i);
			if (c >= '0' && c <= '9') {
				digits.append(c);
			} else {
				break;
			}
		}
		if (digits.length() > 0) {
			return new Ok<>(digits.toString());
		}

		// For other non-empty inputs, return a generic not-implemented error
		return new Err<>(new InterpreterError("not implemented", src, List.of()));
	}
}
