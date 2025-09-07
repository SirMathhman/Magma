package magma.interpret;

import magma.Err;
import magma.Ok;
import magma.Result;
import java.util.Optional;

public class Interpreter {
	public Result<String, InterpretError> interpret(String input) {
		if (input.isEmpty())
			return new Ok<>("");
		// try let-binding like "let x : I32 = 10; x"
		Optional<Result<String, InterpretError>> letRes = tryParseLet(input);
		if (letRes.isPresent())
			return letRes.get();
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

	private Optional<Result<String, InterpretError>> tryParseLet(String input) {
		String s = input.trim();
		if (!s.startsWith("let "))
			return Optional.empty();
		// expected form: let <ident> : <type> = <int> ; <ident>
		int pos = 4; // after "let "
		int idEnd = parseIdentifierEnd(s, pos);
		if (idEnd < 0)
			return Optional.empty();
		String name1 = s.substring(pos, idEnd);
		pos = skipWhitespace(s, idEnd);
		// optional type annotation: ": <type>"
		if (pos < s.length() && s.charAt(pos) == ':') {
			pos = skipWhitespace(s, pos + 1);
			int typeEnd = parseAlnumEnd(s, pos);
			if (typeEnd < 0)
				return Optional.empty();
			pos = skipWhitespace(s, typeEnd);
		}
		if (pos >= s.length() || s.charAt(pos) != '=')
			return Optional.empty();
		pos = skipWhitespace(s, pos + 1);
		int valEnd = parseDigitsEnd(s, pos);
		if (valEnd < 0)
			return Optional.empty();
		String valTok = s.substring(pos, valEnd);
		pos = skipWhitespace(s, valEnd);
		if (pos >= s.length() || s.charAt(pos) != ';')
			return Optional.empty();
		pos = skipWhitespace(s, pos + 1);
		int id2End = parseIdentifierEnd(s, pos);
		if (id2End < 0)
			return Optional.empty();
		String name2 = s.substring(pos, id2End);
		pos = skipWhitespace(s, id2End);
		if (pos != s.length())
			return Optional.empty();
		if (!name1.equals(name2))
			return Optional.of(new Err<>(new InterpretError("Identifier mismatch in let: " + input)));
		return Optional.of(new Ok<>(valTok));
	}

	private static int skipWhitespace(String s, int pos) {
		while (pos < s.length() && Character.isWhitespace(s.charAt(pos)))
			pos++;
		return pos;
	}

	private static int parseIdentifierEnd(String s, int pos) {
		int start = pos;
		while (pos < s.length() && (Character.isLetterOrDigit(s.charAt(pos)) || s.charAt(pos) == '_'))
			pos++;
		return pos == start ? -1 : pos;
	}

	private static int parseAlnumEnd(String s, int pos) {
		int start = pos;
		while (pos < s.length() && Character.isLetterOrDigit(s.charAt(pos)))
			pos++;
		return pos == start ? -1 : pos;
	}

	private static int parseDigitsEnd(String s, int pos) {
		int start = pos;
		while (pos < s.length() && Character.isDigit(s.charAt(pos)))
			pos++;
		return pos == start ? -1 : pos;
	}
}
