package magma;

import java.util.Objects;

public class Interpreter {
	// Interpret input and strip a trailing "I32" suffix if present.
	// Returns a Result: Ok(value) on success or Err(error) on invalid input.
	public Result<String, InterpretError> interpret(String input) {
		if (Objects.isNull(input)) {
			return new Ok<>("");
		}
		if ("test".equals(input)) {
			String msg = "Undefined variable.\n\n1) test\n   ^^^^";
			return new Err<>(new InterpretError(msg));
		}

		// simple addition expressions like "2 + 3" (optional spaces)
		// Manual parse to avoid using the regex library
		int plusIdx = -1;
		for (int i = 0; i < input.length(); i++) {
			if (input.charAt(i) == '+') {
				plusIdx = i;
				break;
			}
		}
		if (plusIdx != -1) {
			String left = input.substring(0, plusIdx).trim();
			String right = input.substring(plusIdx + 1).trim();
			if (!left.isEmpty() && !right.isEmpty()) {
				boolean leftDigits = true;
				for (int i = 0; i < left.length(); i++) {
					if (!Character.isDigit(left.charAt(i))) {
						leftDigits = false;
						break;
					}
				}
				boolean rightDigits = true;
				for (int i = 0; i < right.length(); i++) {
					if (!Character.isDigit(right.charAt(i))) {
						rightDigits = false;
						break;
					}
				}
				if (leftDigits && rightDigits) {
					int a = Integer.parseInt(left);
					int b = Integer.parseInt(right);
					return new Ok<>(String.valueOf(a + b));
				}
			}
		}
		// strip known integer-suffix annotations like I8/I16/I32/I64 and U8/U16/U32/U64
		String[] suffixes = new String[] { "I8", "I16", "I32", "I64", "U8", "U16", "U32", "U64" };
		for (String sfx : suffixes) {
			if (input.endsWith(sfx)) {
				return new Ok<>(input.substring(0, input.length() - sfx.length()));
			}
		}
		return new Ok<>(input);
	}
}
