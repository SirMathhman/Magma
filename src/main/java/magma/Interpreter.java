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
		// Support N-ary additions: split on '+' and process each term
		String[] plusParts = src.split("\\+");
		if (plusParts.length >= 2) {
			// sum will be obtained from accumulateValue
			java.util.Optional<String> commonSuffix = java.util.Optional.empty(); // when present, suffix required to match
																																						// for suffixed operands
			AccContext addCtx = createContext(commonSuffix, 0);
			java.util.OptionalInt sumOpt = accumulateValue(java.util.Arrays.asList(plusParts), addCtx, +1);
			if (sumOpt.isEmpty())
				return new Err<>(new InterpreterError("invalid operands", src, List.of()));
			return new Ok<>(Integer.toString(sumOpt.getAsInt()));
		}

		// Support simple binary subtraction: "a - b"
		String[] minusParts = src.split("\\-");
		if (minusParts.length >= 2) {
			// left-associative subtraction: (((a - b) - c) - ...)
			java.util.List<String> parts = new java.util.ArrayList<>();
			for (String p : minusParts)
				parts.add(p.trim());
			// parse first term
			java.util.Optional<TermVal> t0 = safeParseTerm(parts.get(0));
			if (t0.isEmpty())
				return new Err<>(new InterpreterError("invalid operands", src, List.of()));
			int acc = t0.get().value();
			java.util.Optional<String> commonSuffix = java.util.Optional.empty();
			if (!t0.get().suffix().isEmpty())
				commonSuffix = java.util.Optional.of(t0.get().suffix());
			AccContext ctx = createContext(commonSuffix, acc);
			java.util.OptionalInt accOpt = accumulateValue(parts.subList(1, parts.size()), ctx, -1);
			if (accOpt.isEmpty())
				return new Err<>(new InterpreterError("invalid operands", src, List.of()));
			acc = accOpt.getAsInt();
			commonSuffix = ctx.commonRef().get();
			return new Ok<>(Integer.toString(acc));
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

	private static record AccContext(java.util.concurrent.atomic.AtomicReference<java.util.Optional<String>> commonRef,
			java.util.concurrent.atomic.AtomicInteger accRef) {
	}

	private AccContext createContext(java.util.Optional<String> common, int initial) {
		java.util.concurrent.atomic.AtomicReference<java.util.Optional<String>> commonRef = new java.util.concurrent.atomic.AtomicReference<>(
				common);
		java.util.concurrent.atomic.AtomicInteger accRef = new java.util.concurrent.atomic.AtomicInteger(initial);
		return new AccContext(commonRef, accRef);
	}

	/**
	 * Parse a term and return an Optional TermVal; exists to avoid duplicating the
	 * tryParseOrPrefix + leadingDigits pattern.
	 */
	private java.util.Optional<TermVal> safeParseTerm(String term) {
		return parseTerm(term);
	}

	private boolean processAndAccumulate(String part, AccContext ctx, int sign) {
		java.util.Optional<TermResult> trOpt = processTerm(part, ctx.commonRef().get());
		if (trOpt.isEmpty())
			return false;
		TermResult tr = trOpt.get();
		ctx.commonRef().set(tr.updatedCommon());
		if (sign >= 0)
			ctx.accRef().addAndGet(tr.value());
		else
			ctx.accRef().addAndGet(-tr.value());
		return true;
	}

	/**
	 * Accumulate numeric values from parts into a context and return the computed
	 * integer as a String if successful, or Optional.empty() on error.
	 */

	private java.util.OptionalInt accumulateValue(java.util.List<String> parts, AccContext ctx, int sign) {
		for (String rawPart : parts) {
			String part = rawPart.trim();
			if (!processAndAccumulate(part, ctx, sign))
				return java.util.OptionalInt.empty();
		}
		return java.util.OptionalInt.of(ctx.accRef().get());
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
