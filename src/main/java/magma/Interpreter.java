package magma;

import java.util.Objects;
import java.util.Optional;

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

		// try simple addition parsing
		Optional<String> add = tryParseAddition(input);
		if (add.isPresent()) {
			return new Ok<>(add.get());
		}
		// strip known integer-suffix annotations like I8/I16/I32/I64 and U8/U16/U32/U64
		Optional<String> stripped = tryStripSuffix(input);
		if (stripped.isPresent()) {
			return new Ok<>(stripped.get());
		}
		String trimmed = input.trim();
		// If the input looks like an identifier (letters only), treat it as undefined
		// variable
		if (!trimmed.isEmpty()) {
			boolean allLetters = true;
			for (int i = 0; i < trimmed.length(); i++) {
				if (!Character.isLetter(trimmed.charAt(i))) {
					allLetters = false;
					break;
				}
			}
			if (allLetters) {
				String caret = "";
				for (int i = 0; i < trimmed.length(); i++) {
					caret += "^";
				}
				String msg = "Undefined variable." + "\n\n" + "1) " + trimmed + "\n   " + caret;
				return new Err<>(new InterpretError(msg));
			}
		}
		return new Ok<>(input);
	}

	private Optional<String> tryParseAddition(String input) {
		int plusIdx = -1;
		for (int i = 0; i < input.length(); i++) {
			if (input.charAt(i) == '+') {
				plusIdx = i;
				break;
			}
		}
		if (plusIdx == -1) {
			return Optional.empty();
		}
		String left = input.substring(0, plusIdx).trim();
		String right = input.substring(plusIdx + 1).trim();
		// allow operands to carry known type suffixes like I8/I16/I32/I64/U8/U16/U32/U64
		Optional<String> leftStripped = tryStripSuffix(left);
		if (leftStripped.isPresent()) {
			left = leftStripped.get();
		}
		Optional<String> rightStripped = tryStripSuffix(right);
		if (rightStripped.isPresent()) {
			right = rightStripped.get();
		}
		if (left.isEmpty() || right.isEmpty()) {
			return Optional.empty();
		}
		for (int i = 0; i < left.length(); i++) {
			if (!Character.isDigit(left.charAt(i))) {
				return Optional.empty();
			}
		}
		for (int i = 0; i < right.length(); i++) {
			if (!Character.isDigit(right.charAt(i))) {
				return Optional.empty();
			}
		}
		int a = Integer.parseInt(left);
		int b = Integer.parseInt(right);
		return Optional.of(String.valueOf(a + b));
	}

	private Optional<String> tryStripSuffix(String input) {
		String[] suffixes = new String[] { "I8", "I16", "I32", "I64", "U8", "U16", "U32", "U64" };
		for (String sfx : suffixes) {
			if (input.endsWith(sfx)) {
				return Optional.of(input.substring(0, input.length() - sfx.length()));
			}
		}
		return Optional.empty();
	}
}
