package magma.interpret;

import magma.Err;
import magma.Ok;
import magma.Result;
import java.util.Optional;

public class Interpreter {
	public Result<String, InterpretError> interpret(String input) {
		if (input.isEmpty())
			return new Ok<>("");
		// try simple addition like "2 + 3" (with optional spaces)
		Optional<Result<String, InterpretError>> addRes = tryParseBinary(input, '+');
		if (addRes.isPresent())
			return addRes.get();
		Optional<Result<String, InterpretError>> subRes = tryParseBinary(input, '-');
		if (subRes.isPresent())
			return subRes.get();
		Optional<Result<String, InterpretError>> mulRes = tryParseBinary(input, '*');
		if (mulRes.isPresent())
			return mulRes.get();

		// integer literal (decimal)
		// accept a leading decimal integer even if followed by other characters,
		// e.g. "5I32" should be interpreted as the integer literal "5".
		int i = 0;
		while (i < input.length() && Character.isDigit(input.charAt(i)))
			i++;
		if (i > 0)
			return new Ok<>(input.substring(0, i));
		return new Err<>(new InterpretError("Undefined identifier: " + input));
	}

	private Optional<Result<String, InterpretError>> tryParseBinary(String input, char op) {
		String trimmed = input.trim();
		java.util.List<String> parts = splitByOp(trimmed, op);

		if (parts.size() > 1) {
			if (op == '+')
				return handleAdditionChain(parts, input);
			// if there are exactly two parts (single binary occurrence), handle it
			if (parts.size() != 2)
				return Optional.empty();
			// else fall through to single-binary handling
		}

		return handleSingleBinary(trimmed, op, input);
	}

	private static java.util.List<String> splitByOp(String s, char op) {
		java.util.List<String> parts = new java.util.ArrayList<>();
		int start = 0;
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == op) {
				parts.add(s.substring(start, i));
				start = i + 1;
			}
		}
		parts.add(s.substring(start));
		return parts;
	}

	private Optional<Result<String, InterpretError>> handleAdditionChain(java.util.List<String> parts, String input) {
		long sum = 0L;
		Optional<Character> firstSuffixKind = Optional.empty();
		for (String rawPart : parts) {
			String part = rawPart.trim();
			if (part.isEmpty())
				return Optional.empty();
			int ld = leadingDigits(part);
			if (ld == 0)
				return Optional.empty();
			try {
				long v = Long.parseLong(part.substring(0, ld));
				String suffix = part.substring(ld).trim();
				if (hasSuffix(suffix)) {
					char kind = Character.toUpperCase(suffix.charAt(0));
					if (firstSuffixKind.isEmpty())
						firstSuffixKind = Optional.of(kind);
					else if (isSignednessChar(firstSuffixKind.get()) && isSignednessChar(kind) && firstSuffixKind.get() != kind)
						return Optional.of(new Err<>(new InterpretError("Mixed signedness in addition: " + input)));
				}
				sum = Math.addExact(sum, v);
			} catch (NumberFormatException | ArithmeticException e) {
				return Optional.empty();
			}
		}
		return Optional.of(new Ok<>(Long.toString(sum)));
	}

	private Optional<Result<String, InterpretError>> handleSingleBinary(String trimmed, char op, String input) {
		int idx = trimmed.indexOf(op);
		if (idx < 0)
			return Optional.empty();
		String left = trimmed.substring(0, idx).trim();
		String right = trimmed.substring(idx + 1).trim();
		if (left.isEmpty() || right.isEmpty())
			return Optional.empty();

		int li = leadingDigits(left);
		if (li == 0)
			return Optional.empty();
		int ri = leadingDigits(right);
		if (ri == 0)
			return Optional.empty();

		try {
			long a = Long.parseLong(left.substring(0, li));
			long b = Long.parseLong(right.substring(0, ri));
			String leftSuffix = left.substring(li).trim();
			String rightSuffix = right.substring(ri).trim();
			if (hasSuffix(leftSuffix) && hasSuffix(rightSuffix) && isMixedSignedness(leftSuffix, rightSuffix))
				return Optional.of(new Err<>(new InterpretError("Mixed signedness in operation: " + input)));
			long res;
			switch (op) {
				case '+':
					res = a + b;
					break;
				case '-':
					res = a - b;
					break;
				case '*':
					res = a * b;
					break;
				default:
					return Optional.empty();
			}
			return Optional.of(new Ok<>(Long.toString(res)));
		} catch (NumberFormatException e) {
			return Optional.empty();
		}
	}

	private static boolean isSignednessChar(char c) {
		char u = Character.toUpperCase(c);
		return u == 'U' || u == 'I';
	}

	private static int leadingDigits(String s) {
		int i = 0;
		while (i < s.length() && Character.isDigit(s.charAt(i)))
			i++;
		return i;
	}

	private static boolean hasSuffix(String s) {
		return !s.isEmpty();
	}

	private static boolean isMixedSignedness(String a, String b) {
		char la = Character.toUpperCase(a.charAt(0));
		char lb = Character.toUpperCase(b.charAt(0));
		return (la == 'U' && lb == 'I') || (la == 'I' && lb == 'U');
	}
}
