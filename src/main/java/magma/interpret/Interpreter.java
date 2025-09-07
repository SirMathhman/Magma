package magma.interpret;

import magma.Err;
import magma.Ok;
import magma.Result;
import java.util.Optional;

public class Interpreter {

	/**
	 * Small generic tuple used instead of AbstractMap.SimpleEntry to avoid
	 * duplication and make code more expressive.
	 */
	private static record Tuple2<A, B>(A first, B second) {
	}

	public Result<String, InterpretError> interpret(String input) {
		if (input.isEmpty())
			return new Ok<>("");
		// try let-binding like "let x : I32 = 10; x"
		Optional<Result<String, InterpretError>> declRes = tryParseDeclarations(input);
		if (declRes.isPresent())
			return declRes.get();
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
		// boolean literals
		if (input.equals("true") || input.equals("false"))
			return new Ok<>(input);

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

	private Optional<Result<String, InterpretError>> tryParseDeclarations(String input) {
		String s = input.trim();
		if (!s.startsWith("let ") && !s.startsWith("fn "))
			return Optional.empty();
		java.util.List<String> stmts = splitStatements(s);
		if (stmts.isEmpty())
			return Optional.empty();
		// If there is any fn declaration present, handle as declarations; else fall back to let
		boolean hasFn = stmts.stream().anyMatch(p -> p.startsWith("fn "));
		if (!hasFn)
			return tryParseLet(input);
		return handleFunctionDeclarations(stmts);
	}

	private Optional<Result<String, InterpretError>> handleFunctionDeclarations(java.util.List<String> stmts) {
		String finalPart = stmts.get(stmts.size() - 1);
		// final should be a call like name()
		int p = parseIdentifierEnd(finalPart, 0);
		if (p < 0)
			return Optional.empty();
		String callName = finalPart.substring(0, p);
		p = skipWhitespace(finalPart, p);
		if (p >= finalPart.length() || finalPart.charAt(p) != '(')
			return Optional.empty();
		p = skipWhitespace(finalPart, p + 1);
		if (p >= finalPart.length() || finalPart.charAt(p) != ')')
			return Optional.empty();
		p = skipWhitespace(finalPart, p + 1);
		if (p != finalPart.length())
			return Optional.empty();
		java.util.Map<String, String> fns = new java.util.HashMap<>();
		for (int i = 0; i < stmts.size() - 1; i++) {
			String stmt = stmts.get(i);
			Optional<Tuple2<String, String>> fnOpt = parseFnPart(stmt);
			if (fnOpt.isEmpty())
				return Optional.empty();
			Tuple2<String, String> fn = fnOpt.get();
			fns.put(fn.first(), fn.second());
		}
		if (!fns.containsKey(callName))
			return Optional.of(new Err<>(new InterpretError("Unbound identifier: " + callName)));
		return Optional.of(new Ok<>(fns.get(callName)));
	}

	/**
	 * Parse function declaration like: fn name() : Type => <numeric-literal>
	 * Returns (name, returnValue)
	 */
	private Optional<Tuple2<String, String>> parseFnPart(String stmt) {
		if (!stmt.startsWith("fn "))
			return Optional.empty();
		int pos = 3;
		int idEnd = parseIdentifierEnd(stmt, pos);
		if (idEnd < 0)
			return Optional.empty();
		String name = stmt.substring(pos, idEnd);
		pos = skipWhitespace(stmt, idEnd);
		if (pos >= stmt.length() || stmt.charAt(pos) != '(')
			return Optional.empty();
		pos = skipWhitespace(stmt, pos + 1);
		if (pos >= stmt.length() || stmt.charAt(pos) != ')')
			return Optional.empty();
		pos = skipWhitespace(stmt, pos + 1);
		// optional return type
		java.util.Optional<Tuple2<String, Integer>> typeOpt = parseOptionalType(stmt, pos);
		if (typeOpt.isEmpty())
			return Optional.empty();
		String typeTok = typeOpt.get().first();
		pos = typeOpt.get().second();
		// expect =>
		if (pos + 1 >= stmt.length() || stmt.charAt(pos) != '=' || stmt.charAt(pos + 1) != '>')
			return Optional.empty();
		pos = skipWhitespace(stmt, pos + 2);
		if (pos >= stmt.length())
			return Optional.empty();
		// rhs must be numeric literal
		if (!Character.isDigit(stmt.charAt(pos)))
			return Optional.empty();
		int vEnd = parseDigitsEnd(stmt, pos);
		if (vEnd < 0)
			return Optional.empty();
		String val = stmt.substring(pos, vEnd);
		pos = skipWhitespace(stmt, vEnd);
		if (pos != stmt.length())
			return Optional.empty();
		// if declared type present and is Bool, numeric invalid
		if (!typeTok.isEmpty() && Character.toUpperCase(typeTok.charAt(0)) == 'B')
			return Optional.of(new Tuple2<>(name, "")); // treat as present but empty to trigger later mismatch
		return Optional.of(new Tuple2<>(name, val));
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
		// If rhs is a numeric literal, check declared type (if any) against the
		// literal's suffix
		if (Character.isDigit(rhs.charAt(0))) {
			Optional<Character> declared = parseLetDeclaredSignedness(stmt);
			// If the declared type is Bool (leading 'B'), numeric RHS is invalid
			if (declared.isPresent() && Character.toUpperCase(declared.get()) == 'B')
				return Optional.of(new Err<>(new InterpretError("Type mismatch in let: " + stmt)));
			Optional<Character> rhsSuffix = parseLetRhsSignedness(stmt);
			if (declared.isPresent() && rhsSuffix.isPresent()) {
				char d = declared.get();
				char r = rhsSuffix.get();
				if (isSignednessChar(d) && isSignednessChar(r) && d != r)
					return Optional.of(new Err<>(new InterpretError("Signedness mismatch in let: " + stmt)));
			}
			return Optional.of(new Ok<>(""));
		}
		// boolean literal
		if (rhs.equals("true") || rhs.equals("false")) {
			Optional<Character> declared = parseLetDeclaredSignedness(stmt);
			if (declared.isPresent()) {
				char d = declared.get();
				// treat types starting with 'B' as Bool
				if (Character.toUpperCase(d) != 'B')
					return Optional.of(new Err<>(new InterpretError("Type mismatch in let: " + stmt)));
			}
			return Optional.of(new Ok<>(""));
		}
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
			Optional<Tuple2<String, String>> kvOpt = parseLetPart(stmt);
			if (kvOpt.isEmpty())
				return Optional.empty();
			Tuple2<String, String> kv = kvOpt.get();
			String name = kv.first();
			String rhs = kv.second();
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

	private Optional<Tuple2<String, String>> parseLetPart(String stmt) {
		// stmt should start with "let "
		if (!stmt.startsWith("let "))
			return Optional.empty();
		int pos = 4;
		java.util.Optional<Tuple2<String, Tuple2<String, Integer>>> nameTypeOpt = parseLetNameAndOptionalType(stmt, pos);
		if (nameTypeOpt.isEmpty())
			return Optional.empty();
		Tuple2<String, Tuple2<String, Integer>> nameType = nameTypeOpt.get();
		String name = nameType.first();
		pos = nameType.second().second();
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
			return Optional.of(new Tuple2<>(name, val));
		} else if (stmt.startsWith("true", pos) || stmt.startsWith("false", pos)) {
			int len = stmt.startsWith("true", pos) ? 4 : 5;
			int vEnd = pos + len;
			String val = stmt.substring(pos, vEnd);
			pos = skipWhitespace(stmt, vEnd);
			if (pos != stmt.length())
				return Optional.empty();
			return Optional.of(new Tuple2<>(name, val));
		} else {
			int rEnd = parseIdentifierEnd(stmt, pos);
			if (rEnd < 0)
				return Optional.empty();
			String rhs = stmt.substring(pos, rEnd);
			pos = skipWhitespace(stmt, rEnd);
			if (pos != stmt.length())
				return Optional.empty();
			return Optional.of(new Tuple2<>(name, rhs));
		}
	}

	/**
	 * Parse identifier name at startPos and any optional type token. Returns
	 * pair (name, (typeToken, posAfterType)). typeToken is empty string if no
	 * type was present.
	 */
	private java.util.Optional<Tuple2<String, Tuple2<String, Integer>>> parseLetNameAndOptionalType(String stmt,
			int startPos) {
		int idEnd = parseIdentifierEnd(stmt, startPos);
		if (idEnd < 0)
			return java.util.Optional.empty();
		String name = stmt.substring(startPos, idEnd);
		int pos = skipWhitespace(stmt, idEnd);
		java.util.Optional<Tuple2<String, Integer>> typeOpt = parseOptionalType(stmt, pos);
		if (typeOpt.isEmpty())
			return java.util.Optional.empty();
		Tuple2<String, Integer> te = typeOpt.get();
		return java.util.Optional.of(new Tuple2<>(name, te));
	}

	/**
	 * Convenience helper that returns only the RHS string for a let-part if
	 * present.
	 */
	private Optional<String> parseLetRhs(String stmt) {
		Optional<Tuple2<String, String>> kvOpt = parseLetPart(stmt);
		if (kvOpt.isEmpty())
			return Optional.empty();
		return Optional.of(kvOpt.get().second());
	}

	/**
	 * Returns the signedness character (first letter) of the declared type if
	 * present,
	 * for example for "let x : U8 = ..." returns Optional.of('U').
	 */
	private Optional<Character> parseLetDeclaredSignedness(String stmt) {
		if (!stmt.startsWith("let "))
			return Optional.empty();
		int pos = 4;
		int idEnd = parseIdentifierEnd(stmt, pos);
		if (idEnd < 0)
			return Optional.empty();
		pos = skipWhitespace(stmt, idEnd);
		java.util.Optional<Tuple2<String, Integer>> typeOpt = parseOptionalType(stmt, pos);
		if (typeOpt.isEmpty())
			return Optional.empty();
		String typeToken = typeOpt.get().first();
		if (!typeToken.isEmpty())
			return Optional.of(Character.toUpperCase(typeToken.charAt(0)));
		return Optional.empty();
	}

	/**
	 * Parse an optional type token following whitespace at pos. If there's no
	 * type (no ':' present) returns a present entry with null key and the
	 * returned pos. If ':' is present but malformed returns empty.
	 */
	private java.util.Optional<Tuple2<String, Integer>> parseOptionalType(String stmt, int pos) {
		pos = skipWhitespace(stmt, pos);
		if (pos < stmt.length() && stmt.charAt(pos) == ':') {
			pos = skipWhitespace(stmt, pos + 1);
			int typeEnd = parseAlnumEnd(stmt, pos);
			if (typeEnd < 0)
				return java.util.Optional.empty();
			String typeToken = stmt.substring(pos, typeEnd).trim();
			pos = skipWhitespace(stmt, typeEnd);
			return java.util.Optional.of(new Tuple2<>(typeToken, pos));
		}
		return java.util.Optional.of(new Tuple2<>("", pos));
	}

	/**
	 * If the RHS is a numeric literal with a suffix (e.g. "10I32"), return the
	 * leading char of the suffix (e.g. 'I'), otherwise empty.
	 */
	private Optional<Character> parseLetRhsSignedness(String stmt) {
		int eq = stmt.indexOf('=');
		if (eq < 0)
			return Optional.empty();
		int pos = skipWhitespace(stmt, eq + 1);
		if (pos >= stmt.length())
			return Optional.empty();
		if (!Character.isDigit(stmt.charAt(pos)))
			return Optional.empty();
		int vEnd = parseDigitsEnd(stmt, pos);
		if (vEnd < 0)
			return Optional.empty();
		if (vEnd >= stmt.length())
			return Optional.empty();
		String suffix = stmt.substring(vEnd).trim();
		if (suffix.isEmpty())
			return Optional.empty();
		return Optional.of(Character.toUpperCase(suffix.charAt(0)));
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
