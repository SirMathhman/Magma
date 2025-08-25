package magma;

import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

public class Interpreter {
	public static Result<String, InterpretError> interpret(String input) {
		try {
			Integer.parseInt(input);
			return new Ok<>(input);
		} catch (NumberFormatException e) {
			// If input has a numeric prefix followed by a type-suffix (e.g. "5I32"),
			// accept the leading integer portion as the value.
			if (input == null || input.isEmpty()) {
				return new Err<>(new InterpretError("Not a number", input));
			}

			int len = input.length();
			int idx = 0;

			// optional sign
			if (idx < len) {
				char c = input.charAt(idx);
				if (c == '+' || c == '-') {
					idx++;
				}
			}

			int digitsStart = idx;
			while (idx < len && Character.isDigit(input.charAt(idx))) {
				idx++;
			}

			if (idx > digitsStart) {
				// include sign if present
				String prefix = input.substring(0, idx);
				String suffix = input.substring(idx);

				if (suffix.isEmpty()) {
					return new Ok<>(prefix);
				}

				var allowed = java.util.Set.of(
						"U8", "U16", "U32", "U64",
						"I8", "I16", "I32", "I64");

				if (allowed.contains(suffix)) {
					return new Ok<>(prefix);
				}
			}

			return new Err<>(new InterpretError("Not a number", input));
		}
	}
}
