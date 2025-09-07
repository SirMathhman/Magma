package magma;

import java.util.Objects;
import java.util.Optional;

public class Interpreter {
	// Interpret input and strip a trailing integer suffix if present.
	// Returns a Result: Ok(value) on success or Err(error) on invalid input.
	public Result<String, InterpretError> interpret(String input) {
		if (Objects.isNull(input)) {
			return new Ok<>("");
		}
		if ("test".equals(input)) {
			String msg = "Undefined variable." + "\n\n" + "1) test" + "\n   " + "^^^^";
			return new Err<>(new InterpretError(msg));
		}

		// try parsing addition expressions first
		Optional<Result<String, InterpretError>> add = tryParseAddition(input);
		if (add.isPresent()) {
			return add.get();
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
				String caret = "^".repeat(trimmed.length());
				String msg = "Undefined variable." + "\n\n" + "1) " + trimmed + "\n   " + caret;
				return new Err<>(new InterpretError(msg));
			}
		}

		return new Ok<>(trimmed);
	}

	// Try to parse additions like "1 + 2" or "1 + 2 + 3". Returns Optional.empty()
	// when the input isn't an addition expression. Returns Optional.of(Ok(...)) or
	// Optional.of(Err(...)) when it is an addition expression but either succeeded
	// or failed (e.g. mismatched suffixes).
	private Optional<Result<String, InterpretError>> tryParseAddition(String input) {
		if (input.indexOf('+') < 0) {
			return Optional.empty();
		}

		// split on '+' manually to avoid regex
		java.util.List<String> parts = new java.util.ArrayList<>();
		int start = 0;
		for (int idx = 0; idx < input.length(); idx++) {
			if (input.charAt(idx) == '+') {
				parts.add(input.substring(start, idx).trim());
				start = idx + 1;
			}
		}
		parts.add(input.substring(start).trim());

		if (parts.size() < 2) {
			return Optional.empty();
		}

		long sum = 0L;
		Optional<String> commonSuffix = Optional.empty();
		for (String part : parts) {
			if (part.isEmpty()) {
				return Optional.of(new Err<>(new InterpretError("Invalid operand")));
			}
			Optional<String> sfxOpt = findSuffix(part);
			String core = part;
			if (sfxOpt.isPresent()) {
				String sfx = sfxOpt.get();
				core = part.substring(0, part.length() - sfx.length()).trim();
				if (core.isEmpty()) {
					return Optional.of(new Err<>(new InterpretError("Invalid operand")));
				}
				if (commonSuffix.isPresent() && !commonSuffix.get().equals(sfx)) {
					return Optional.of(new Err<>(new InterpretError("Mismatched operand types")));
				}
				if (!commonSuffix.isPresent()) {
					commonSuffix = sfxOpt;
				}
			}
			for (int j = 0; j < core.length(); j++) {
				if (!Character.isDigit(core.charAt(j))) {
					return Optional.of(new Err<>(new InterpretError("Invalid operand")));
				}
			}
			long v = Long.parseLong(core);
			sum += v;
		}

		return Optional.of(new Ok<>(String.valueOf(sum)));
	}

	private Optional<String> findSuffix(String s) {
		String[] suffixes = new String[] { "I8", "I16", "I32", "I64", "U8", "U16", "U32", "U64" };
		for (String sfx : suffixes) {
			if (s.endsWith(sfx)) {
				return Optional.of(sfx);
			}
		}
		return Optional.empty();
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
