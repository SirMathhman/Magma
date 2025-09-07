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
		return new Err<>(new InterpretError("Unbound identifier: " + input));
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
		return splitRaw(s, op);
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
						return Optional.of(new Err<>(new InterpretError("Signedness mismatch in addition: " + input)));
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
				return Optional.of(new Err<>(new InterpretError("Signedness mismatch in operation: " + input)));
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
		java.util.List<String> stmts = splitStatements(s);
		if (stmts.isEmpty())
			return Optional.empty();
		if (stmts.size() == 1)
			return handleSingleLet(stmts.get(0));
		return handleMultipleLets(stmts);
	}

	private static java.util.List<String> splitStatements(String s) {
		java.util.List<String> raw = splitRaw(s, ';');
		java.util.List<String> stmts = new java.util.ArrayList<>();
		for (String p : raw) {
			String t = p.trim();
			if (!t.isEmpty())
				stmts.add(t);
		}
		return stmts;
	}

	private static java.util.List<String> splitRaw(String s, char delim) {
		java.util.List<String> parts = new java.util.ArrayList<>();
		int start = 0;
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == delim) {
				parts.add(s.substring(start, i));
				start = i + 1;
			}
		}
		parts.add(s.substring(start));
		return parts;
	}

	private Optional<Result<String, InterpretError>> handleSingleLet(String stmt) {
		Optional<String> rhsOpt = parseLetRhs(stmt);
		if (rhsOpt.isEmpty())
			return Optional.empty();
		String rhs = rhsOpt.get();
		if (rhs.isEmpty())
			return Optional.empty();
		if (Character.isDigit(rhs.charAt(0)))
			return Optional.of(new Ok<>(""));
		return Optional.of(new Err<>(new InterpretError("Undefined identifier: " + rhs)));
	}

	private Optional<Result<String, InterpretError>> handleMultipleLets(java.util.List<String> stmts) {
		String finalPart = stmts.get(stmts.size() - 1);
		int fend = parseIdentifierEnd(finalPart, 0);
		if (fend < 0 || skipWhitespace(finalPart, fend) != finalPart.length())
			return Optional.empty();
		java.util.Map<String, String> env = new java.util.HashMap<>();
		for (int i = 0; i < stmts.size() - 1; i++) {
			String stmt = stmts.get(i);
			Optional<java.util.Map.Entry<String, String>> kvOpt = parseLetPart(stmt);
			if (kvOpt.isEmpty())
				return Optional.empty();
			java.util.Map.Entry<String, String> kv = kvOpt.get();
			String name = kv.getKey();
			String rhs = kv.getValue();
			if (rhs.isEmpty())
				return Optional.empty();
			if (Character.isDigit(rhs.charAt(0))) {
				env.put(name, rhs);
			} else {
				if (!env.containsKey(rhs))
					return Optional.of(new Err<>(new InterpretError("Unbound identifier: " + rhs)));
				env.put(name, env.get(rhs));
			}
		}
		String finalName = finalPart;
		if (!env.containsKey(finalName))
			return Optional.of(new Err<>(new InterpretError("Unbound identifier: " + finalName)));
		return Optional.of(new Ok<>(env.get(finalName)));
	}

	private Optional<java.util.Map.Entry<String, String>> parseLetPart(String stmt) {
		// stmt should start with "let "
		if (!stmt.startsWith("let "))
			return Optional.empty();
		int pos = 4;
		int idEnd = parseIdentifierEnd(stmt, pos);
		if (idEnd < 0)
			return Optional.empty();
		String name = stmt.substring(pos, idEnd);
		pos = skipWhitespace(stmt, idEnd);
		if (pos < stmt.length() && stmt.charAt(pos) == ':') {
			pos = skipWhitespace(stmt, pos + 1);
			int typeEnd = parseAlnumEnd(stmt, pos);
			if (typeEnd < 0)
				return Optional.empty();
			pos = skipWhitespace(stmt, typeEnd);
		}
		if (pos >= stmt.length() || stmt.charAt(pos) != '=')
			return Optional.empty();
		pos = skipWhitespace(stmt, pos + 1);
		if (pos >= stmt.length())
			return Optional.empty();
		// rhs is either digits or identifier
		if (Character.isDigit(stmt.charAt(pos))) {
			int vEnd = parseDigitsEnd(stmt, pos);
			if (vEnd < 0)
				return Optional.empty();
			String val = stmt.substring(pos, vEnd);
			pos = skipWhitespace(stmt, vEnd);
			if (pos != stmt.length())
				return Optional.empty();
			return Optional.of(new java.util.AbstractMap.SimpleEntry<>(name, val));
		} else {
			int rEnd = parseIdentifierEnd(stmt, pos);
			if (rEnd < 0)
				return Optional.empty();
			String rhs = stmt.substring(pos, rEnd);
			pos = skipWhitespace(stmt, rEnd);
			if (pos != stmt.length())
				return Optional.empty();
			return Optional.of(new java.util.AbstractMap.SimpleEntry<>(name, rhs));
		}
	}

	/**
	 * Convenience helper that returns only the RHS string for a let-part if
	 * present.
	 */
	private Optional<String> parseLetRhs(String stmt) {
		Optional<java.util.Map.Entry<String, String>> kvOpt = parseLetPart(stmt);
		if (kvOpt.isEmpty())
			return Optional.empty();
		return Optional.of(kvOpt.get().getValue());
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
