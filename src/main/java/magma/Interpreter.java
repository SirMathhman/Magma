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

		java.util.List<String> parts = splitParts(input);
		java.util.List<Integer> partStarts = splitPartStarts(input);

		if (parts.size() < 2) {
			return Optional.empty();
		}

		long sum = 0L;
		Optional<String> commonSuffix = Optional.empty();
		for (int idx = 0; idx < parts.size(); idx++) {
			String part = parts.get(idx);
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
					MismatchContext ctx = new MismatchContext(parts, partStarts, idx, sfx, commonSuffix.get());
					Optional<String> msg = buildMismatchedSuffixMessage(input, ctx);
					if (msg.isPresent()) {
						return Optional.of(new Err<>(new InterpretError(msg.get())));
					}
				}
				if (!commonSuffix.isPresent()) {
					commonSuffix = sfxOpt;
				}
			}
			if (!isDigits(core)) {
				return Optional.of(new Err<>(new InterpretError("Invalid operand")));
			}
			long v = Long.parseLong(core);
			sum += v;
		}

		return Optional.of(new Ok<>(String.valueOf(sum)));
	}

	private java.util.List<String> splitParts(String input) {
		java.util.List<String> parts = new java.util.ArrayList<>();
		int start = 0;
		for (int idx = 0; idx < input.length(); idx++) {
			if (input.charAt(idx) == '+') {
				parts.add(input.substring(start, idx).trim());
				start = idx + 1;
			}
		}
		parts.add(input.substring(start).trim());
		return parts;
	}

	private java.util.List<Integer> splitPartStarts(String input) {
		java.util.List<Integer> starts = new java.util.ArrayList<>();
		int start = 0;
		for (int idx = 0; idx < input.length(); idx++) {
			if (input.charAt(idx) == '+') {
				starts.add(start);
				start = idx + 1;
			}
		}
		starts.add(start);
		return starts;
	}

	private boolean isDigits(String s) {
		if (s.isEmpty())
			return false;
		for (int i = 0; i < s.length(); i++) {
			if (!Character.isDigit(s.charAt(i)))
				return false;
		}
		return true;
	}

	private Optional<String> buildMismatchedSuffixMessage(String input, MismatchContext ctx) {
		String header = "Mismatched operand types.";
		StringBuilder sb = new StringBuilder();
		sb.append(header).append("\n\n");
		sb.append("1) ").append(input).append('\n');

		int lineLen = 3 + input.length(); // length of "1) " + input
		char[] caretLine = new char[lineLen];
		for (int ci = 0; ci < lineLen; ci++) {
			caretLine[ci] = ' ';
		}

		String part = ctx.parts().get(ctx.idx());
		int partStart = ctx.partStarts().get(ctx.idx());
		int leadingSpaces = 0;
		while (partStart + leadingSpaces < input.length()
				&& Character.isWhitespace(input.charAt(partStart + leadingSpaces))) {
			leadingSpaces++;
		}
		int suffixPosInPart = part.indexOf(ctx.sfx());
		int suffixStartInInput = partStart + leadingSpaces + suffixPosInPart;
		for (int k = 0; k < ctx.sfx().length(); k++) {
			int pos = 3 + suffixStartInInput + k;
			if (pos >= 0 && pos < caretLine.length)
				caretLine[pos] = '^';
		}

		int otherSearchFrom = 0;
		int foundAt = -1;
		while (true) {
			foundAt = input.indexOf(ctx.other(), otherSearchFrom);
			if (foundAt < 0)
				break;
			if (foundAt != suffixStartInInput) {
				for (int k = 0; k < ctx.other().length(); k++) {
					int pos = 3 + foundAt + k;
					if (pos >= 0 && pos < caretLine.length)
						caretLine[pos] = '^';
				}
				break;
			}
			otherSearchFrom = foundAt + 1;
		}

		sb.append(new String(caretLine)).append('\n');
		return Optional.of(sb.toString());
	}

	private static record MismatchContext(java.util.List<String> parts, java.util.List<Integer> partStarts, int idx,
			String sfx,
			String other) {
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
