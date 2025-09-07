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
		// try parsing multiplication expressions first (higher precedence)
		Optional<Result<String, InterpretError>> mul = tryParseMultiplication(input);
		if (mul.isPresent()) {
			return mul.get();
		}

		// if the input mixes '+' and '-' handle left-to-right evaluation
		Optional<Result<String, InterpretError>> mixed = tryParseMixedExpression(input);
		if (mixed.isPresent()) {
			return mixed.get();
		}

		// try parsing addition expressions first
		Optional<Result<String, InterpretError>> add = tryParseAddition(input);
		if (add.isPresent()) {
			return add.get();
		}

		// try parsing simple subtraction like "8 - 4"
		Optional<Result<String, InterpretError>> sub = tryParseSubtraction(input);
		if (sub.isPresent()) {
			return sub.get();
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
		OperationContext ctx = new OperationContext(parts, partStarts, commonSuffix);
		for (int idx = 0; idx < parts.size(); idx++) {
			ProcessResult pr = processPartForOp(input, ctx, idx);
			if (pr.error().isPresent()) {
				return pr.error();
			}
			sum += pr.value().get();
			ctx = new OperationContext(parts, partStarts, pr.commonSuffix());
		}

		return Optional.of(new Ok<>(String.valueOf(sum)));
	}

	private java.util.List<String> splitParts(String input) {
		return splitParts(input, '+');
	}

	private java.util.List<String> splitParts(String input, char delimiter) {
		java.util.List<String> parts = new java.util.ArrayList<>();
		int start = 0;
		for (int idx = 0; idx < input.length(); idx++) {
			if (input.charAt(idx) == delimiter) {
				parts.add(input.substring(start, idx).trim());
				start = idx + 1;
			}
		}
		parts.add(input.substring(start).trim());
		return parts;
	}

	private java.util.List<Integer> splitPartStarts(String input) {
		return splitPartStarts(input, '+');
	}

	private java.util.List<Integer> splitPartStarts(String input, char delimiter) {
		java.util.List<Integer> starts = new java.util.ArrayList<>();
		int start = 0;
		for (int idx = 0; idx < input.length(); idx++) {
			if (input.charAt(idx) == delimiter) {
				starts.add(start);
				start = idx + 1;
			}
		}
		starts.add(start);
		return starts;
	}

	private static record PartInfo(String core, Optional<String> suffix) {
	}

	private PartInfo parsePart(String part) {
		Optional<String> sfxOpt = findSuffix(part);
		String core = part;
		if (sfxOpt.isPresent()) {
			String sfx = sfxOpt.get();
			core = part.substring(0, part.length() - sfx.length()).trim();
		}
		return new PartInfo(core, sfxOpt);
	}

	private static record OperationContext(java.util.List<String> parts, java.util.List<Integer> partStarts,
			Optional<String> commonSuffix) {
	}

	private static record ProcessResult(Optional<Long> value, Optional<String> commonSuffix,
			Optional<Result<String, InterpretError>> error) {
	}

	private ProcessResult processPartForOp(String input, OperationContext ctx, int idx) {
		String part = ctx.parts().get(idx);
		if (part.isEmpty())
			return new ProcessResult(Optional.empty(), ctx.commonSuffix(),
					Optional.of(new Err<>(new InterpretError("Invalid operand"))));
		PartInfo info = parsePart(part);
		String core = info.core();
		Optional<String> sfxOpt = info.suffix();
		Optional<String> newCommon = ctx.commonSuffix();
		if (sfxOpt.isPresent()) {
			String sfx = sfxOpt.get();
			if (core.isEmpty())
				return new ProcessResult(Optional.empty(), newCommon,
						Optional.of(new Err<>(new InterpretError("Invalid operand"))));
			if (newCommon.isPresent() && !newCommon.get().equals(sfx)) {
				MismatchContext mctx = new MismatchContext(ctx.parts(), ctx.partStarts(), idx, sfx, newCommon.get());
				Optional<Result<String, InterpretError>> maybe = handleSuffixMismatch(input, mctx);
				if (maybe.isPresent()) {
					return new ProcessResult(Optional.empty(), newCommon, maybe);
				}
			}
			if (!newCommon.isPresent())
				newCommon = sfxOpt;
		}
		if (!isDigits(core))
			return new ProcessResult(Optional.empty(), newCommon,
					Optional.of(new Err<>(new InterpretError("Invalid operand"))));
		long v = Long.parseLong(core);
		return new ProcessResult(Optional.of(v), newCommon, Optional.empty());
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

	private Optional<Result<String, InterpretError>> handleSuffixMismatch(String input, MismatchContext ctx) {
		// If there's no existing common suffix, nothing to do.
		if (ctx.other().isEmpty()) {
			return Optional.empty();
		}
		// If the other suffix is present and different, build the detailed message
		Optional<String> msg = buildMismatchedSuffixMessage(input, ctx);
		if (msg.isPresent()) {
			return Optional.of(new Err<>(new InterpretError(msg.get())));
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

	private Optional<Result<String, InterpretError>> tryParseSubtraction(String input) {
		// split on '-' to support chained subtraction
		java.util.List<String> parts = splitParts(input, '-');
		java.util.List<Integer> partStarts = splitPartStarts(input, '-');

		if (parts.size() < 2)
			return Optional.empty();

		Optional<String> commonSuffix = Optional.empty();
		java.util.List<Long> values = new java.util.ArrayList<>();
		for (int idx = 0; idx < parts.size(); idx++) {
			String part = parts.get(idx);
			if (part.isEmpty())
				return Optional.of(new Err<>(new InterpretError("Invalid operand")));
			PartInfo info = parsePart(part);
			String core = info.core();
			Optional<String> sfxOpt = info.suffix();
			if (sfxOpt.isPresent()) {
				String sfx = sfxOpt.get();
				if (core.isEmpty())
					return Optional.of(new Err<>(new InterpretError("Invalid operand")));
				if (commonSuffix.isPresent() && !commonSuffix.get().equals(sfx)) {
					MismatchContext ctx = new MismatchContext(parts, partStarts, idx, sfx, commonSuffix.get());
					Optional<String> msg = buildMismatchedSuffixMessage(input, ctx);
					if (msg.isPresent()) {
						return Optional.of(new Err<>(new InterpretError(msg.get())));
					}
				}
				if (!commonSuffix.isPresent())
					commonSuffix = sfxOpt;
			}
			if (!isDigits(core))
				return Optional.of(new Err<>(new InterpretError("Invalid operand")));
			values.add(Long.parseLong(core));
		}

		// left-associative subtraction
		long acc = values.get(0);
		for (int i = 1; i < values.size(); i++) {
			acc -= values.get(i);
		}
		return Optional.of(new Ok<>(String.valueOf(acc)));
	}

	private Optional<Result<String, InterpretError>> tryParseMixedExpression(String input) {
		// quickly reject non-mixed inputs
		if (input.indexOf('+') < 0 && input.indexOf('-') < 0) {
			return Optional.empty();
		}

		// Determine if we have at least two different operator kinds (+, -, *)
		int kinds = countOperatorKinds(input);
		if (kinds < 2) {
			return Optional.empty();
		}

		Optional<MixedParts> mpOpt = tokenizeMixedParts(input);
		if (mpOpt.isEmpty())
			return Optional.empty();
		MixedParts mp = mpOpt.get();
		java.util.List<String> parts = mp.parts();
		java.util.List<Character> ops = mp.ops();
		java.util.List<Integer> partStarts = mp.partStarts();

		ParseOperandsResult por = parseOperands(input, parts, partStarts);
		if (por.error().isPresent()) {
			return por.error();
		}
		java.util.List<Long> values = por.operands().get().values();

		// Apply '*' precedence: collapse multiplications first into a new values/ops
		// list
		long acc = evaluateWithPrecedence(values, ops);

		return Optional.of(new Ok<>(String.valueOf(acc)));
	}

	private static record Operands(java.util.List<Long> values, java.util.List<Optional<String>> suffixes,
			Optional<String> commonSuffix) {
	}

	private static record ParseOperandsResult(java.util.Optional<Operands> operands,
			Optional<Result<String, InterpretError>> error) {
	}

	private ParseOperandsResult parseOperands(String input, java.util.List<String> parts,
			java.util.List<Integer> partStarts) {
		java.util.List<Long> values = new java.util.ArrayList<>();
		java.util.List<Optional<String>> suffixes = new java.util.ArrayList<>();
		Optional<String> commonSuffix = Optional.empty();
		OperationContext ctx = new OperationContext(parts, partStarts, commonSuffix);
		for (int i = 0; i < parts.size(); i++) {
			ProcessResult pr = processPartForOp(input, ctx, i);
			if (pr.error().isPresent()) {
				return new ParseOperandsResult(Optional.empty(), pr.error());
			}
			values.add(pr.value().get());
			suffixes.add(pr.commonSuffix());
			ctx = new OperationContext(parts, partStarts, pr.commonSuffix());
		}
		Operands ops = new Operands(values, suffixes, ctx.commonSuffix());
		return new ParseOperandsResult(Optional.of(ops), Optional.empty());
	}

	private long evaluateWithPrecedence(java.util.List<Long> values, java.util.List<Character> ops) {
		java.util.List<Long> collapsedValues = new java.util.ArrayList<>();
		java.util.List<Character> remainingOps = new java.util.ArrayList<>();
		long cur = values.get(0);
		for (int i = 0; i < ops.size(); i++) {
			char op = ops.get(i);
			long nextVal = values.get(i + 1);
			if (op == '*') {
				cur = cur * nextVal;
			} else {
				collapsedValues.add(cur);
				remainingOps.add(op);
				cur = nextVal;
			}
		}
		collapsedValues.add(cur);

		long acc = collapsedValues.get(0);
		for (int i = 0; i < remainingOps.size(); i++) {
			char op = remainingOps.get(i);
			long v = collapsedValues.get(i + 1);
			if (op == '+')
				acc += v;
			else
				acc -= v;
		}
		return acc;
	}

	private Optional<Result<String, InterpretError>> tryParseMultiplication(String input) {
		// only handle pure multiplication (no + or -); mixed cases handled by mixed
		// parser
		if (input.indexOf('*') < 0)
			return Optional.empty();
		if (input.indexOf('+') >= 0 || input.indexOf('-') >= 0)
			return Optional.empty();

		java.util.List<String> parts = splitParts(input, '*');
		java.util.List<Integer> partStarts = splitPartStarts(input, '*');
		if (parts.size() < 2)
			return Optional.empty();

		OperationContext ctx = new OperationContext(parts, partStarts, Optional.empty());
		long acc = 1L;
		for (int i = 0; i < parts.size(); i++) {
			ProcessResult pr = processPartForOp(input, ctx, i);
			if (pr.error().isPresent())
				return pr.error();
			acc *= pr.value().get();
			ctx = new OperationContext(parts, partStarts, pr.commonSuffix());
		}
		return Optional.of(new Ok<>(String.valueOf(acc)));
	}

	private static record MixedParts(java.util.List<String> parts, java.util.List<Character> ops,
			java.util.List<Integer> partStarts) {
	}

	// Tokenize input into parts (operands), operators, and part start indices.
	// Simpler implementation that finds operator indices and slices the input
	// to reduce branching and keep cyclomatic complexity low.
	private Optional<MixedParts> tokenizeMixedParts(String input) {
		int len = input.length();
		int kinds = countOperatorKinds(input);
		if (kinds < 2)
			return Optional.empty();

		java.util.List<String> parts = new java.util.ArrayList<>();
		java.util.List<Character> ops = new java.util.ArrayList<>();
		java.util.List<Integer> partStarts = new java.util.ArrayList<>();

		int pos = 0;
		while (pos < len) {
			int nextOp = findNextOperatorFrom(input, pos);
			if (nextOp < 0) {
				// last part
				String token = input.substring(pos).trim();
				if (!token.isEmpty()) {
					int firstNonSpace = firstNonSpaceIndex(input, pos);
					parts.add(token);
					partStarts.add(firstNonSpace);
				}
				break;
			} else {
				String token = input.substring(pos, nextOp).trim();
				if (!token.isEmpty()) {
					int firstNonSpace = firstNonSpaceIndex(input, pos);
					parts.add(token);
					partStarts.add(firstNonSpace);
				}
				ops.add(input.charAt(nextOp));
				pos = nextOp + 1;
			}
		}

		if (parts.size() < 2)
			return Optional.empty();
		return Optional.of(new MixedParts(parts, ops, partStarts));
	}

	private int countOperatorKinds(String input) {
		int kinds = 0;
		if (input.indexOf('+') >= 0)
			kinds++;
		if (input.indexOf('-') >= 0)
			kinds++;
		if (input.indexOf('*') >= 0)
			kinds++;
		return kinds;
	}

	private int findNextOperatorFrom(String input, int from) {
		int p = input.indexOf('+', from);
		int m = input.indexOf('-', from);
		int s = input.indexOf('*', from);
		int best = -1;
		if (p >= 0)
			best = p;
		if (m >= 0 && (best < 0 || m < best))
			best = m;
		if (s >= 0 && (best < 0 || s < best))
			best = s;
		return best;
	}

	private int firstNonSpaceIndex(String input, int from) {
		int i = from;
		while (i < input.length() && Character.isWhitespace(input.charAt(i)))
			i++;
		return i;
	}
}
