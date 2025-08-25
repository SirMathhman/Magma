package magma;

import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

public class Interpreter {
	public static Result<String, InterpretError> interpret(String input) {
		try {
			if (input != null) {
				String trimmed = input.trim();
				int plusIdx = trimmed.indexOf('+');
				// treat as binary addition only if '+' is not the first or last char
				if (plusIdx > 0 && plusIdx < trimmed.length() - 1) {
					String left = trimmed.substring(0, plusIdx).trim();
					String right = trimmed.substring(plusIdx + 1).trim();

					if (!left.isEmpty() && !right.isEmpty()) {
						var leftRes = interpret(left);
						if (leftRes.isErr())
							return leftRes;
						var rightRes = interpret(right);
						if (rightRes.isErr())
							return rightRes;

						try {
							int l = Integer.parseInt(((magma.result.Ok<String, InterpretError>) leftRes).value());
							int r = Integer.parseInt(((magma.result.Ok<String, InterpretError>) rightRes).value());
							return new Ok<>(String.valueOf(l + r));
						} catch (NumberFormatException ex) {
							return new Err<>(new InterpretError("Invalid numeric literal", input));
						}
					}
				}
			}
			Integer.parseInt(input);
			return new Ok<>(input);
		} catch (NumberFormatException e) {
			// If input has a numeric prefix followed by a type-suffix (e.g. "5I32"),
			// accept the leading integer portion as the value.
			if (input == null || input.isEmpty()) {
				return new Err<>(new InterpretError("Invalid numeric literal", input));
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
					// negative values with unsigned suffixes are invalid (e.g. "-5U8")
					if (prefix.startsWith("-") && suffix.startsWith("U")) {
						return new Err<>(new InterpretError("Negative value for unsigned type", input));
					}
					return new Ok<>(prefix);
				} else {
					return new Err<>(new InterpretError("Unsupported numeric suffix", input));
				}
			}

			return new Err<>(new InterpretError("Invalid numeric literal", input));
		}
	}
}
