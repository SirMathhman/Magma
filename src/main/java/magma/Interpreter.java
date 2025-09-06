package magma;

import java.util.Objects;
import java.util.Optional;

public class Interpreter {
	public Result<String, InterpretError> interpret(String input) {
		if (Objects.isNull(input)) {
			return new Err<>(new InterpretError("input is absent"));
		}
		String trimmed = input.trim();
		if (trimmed.equals("")) {
			return new Ok<>("");
		}

		// try a very small expression evaluator for binary integer ops: a <op> b
		// support + - * /
		try {
			String s = trimmed;
			int len = s.length();
			int pos = 0;
			int firstStart = pos;
			// parse first signed integer
			int firstEnd = parseSignedIntegerEnd(s, pos);
			if (firstEnd != -1) {
				pos = firstEnd;
				pos = skipWS(s, pos);
				if (pos < len) {
					char op = s.charAt(pos);
					if (op == '+' || op == '-' || op == '*' || op == '/') {
						pos++;
						pos = skipWS(s, pos);
						int secondStart = pos;
						int secondEnd = parseSignedIntegerEnd(s, pos);
						if (secondEnd != -1) {
							pos = skipWS(s, secondEnd);
							if (pos == len) {
								try {
									long a = Long.parseLong(s.substring(firstStart, firstEnd));
									long b = Long.parseLong(s.substring(secondStart, secondEnd));
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
										case '/':
											if (b == 0) {
												return new Err<>(new InterpretError("division by zero"));
											}
											res = a / b;
											break;
										default:
											return new Ok<>(trimmed);
									}
									return new Ok<>(Long.toString(res));
								} catch (NumberFormatException ex) {
									// fall through to other parsers
								}
							}
						}
					}
				}
			}
		} catch (NumberFormatException ex) {
			// fall through to other parsers
		}

		// unified let parsing: attempt to parse a let declaration (mutable or not)
		try {
			String s = trimmed;
			Optional<LetDecl> declOpt = parseLetDecl(s, 0);
			if (declOpt.isPresent()) {
				LetDecl decl = declOpt.get();
				if (!decl.isMutable) {
					// immutable: expect a single identifier reference after the semicolon
					Optional<String> maybe = matchFinalIdentifierAndReturnValue(s, decl.afterSemicolon, decl.id,
							s.substring(decl.valStart, decl.valEnd));
					if (maybe.isPresent())
						return new Ok<>(maybe.get());
				} else {
					// mutable: parse an assignment following the declaration
					Optional<Assignment> asgOpt = parseAssignment(s, decl.afterSemicolon, decl.id);
					if (asgOpt.isPresent()) {
						Assignment asg = asgOpt.get();
						Optional<String> maybe = matchFinalIdentifierAndReturnValue(s, asg.afterSecondSemicolon, decl.id,
								asg.value);
						if (maybe.isPresent())
							return new Ok<>(maybe.get());
					}
				}
			}
		} catch (Exception ex) {
			// on any parse error, fall through to echo
		}

		// fallback: echo input
		return new Ok<>(input);
	}

	// helper: skip whitespace starting at pos, return new pos
	private static int skipWS(String s, int pos) {
		int len = s.length();
		while (pos < len && Character.isWhitespace(s.charAt(pos)))
			pos++;
		return pos;
	}

	// helper: parse a signed integer starting at pos; return end index (exclusive)
	// or -1 if none
	private static int parseSignedIntegerEnd(String s, int pos) {
		int len = s.length();
		if (pos < len && (s.charAt(pos) == '+' || s.charAt(pos) == '-'))
			pos++;
		int digitsStart = pos;
		while (pos < len && Character.isDigit(s.charAt(pos)))
			pos++;
		if (pos > digitsStart) {
			return pos;
		}
		return -1;
	}

	// helper: parse an unsigned integer starting at pos; return end index
	// (exclusive) or -1 if none
	private static int parseUnsignedIntegerEnd(String s, int pos) {
		int len = s.length();
		int start = pos;
		while (pos < len && Character.isDigit(s.charAt(pos)))
			pos++;
		if (pos > start)
			return pos;
		return -1;
	}

	// helper: return index where identifier starts (skipping whitespace) or -1
	private static int parseIdentifierStart(String s, int pos) {
		pos = skipWS(s, pos);
		if (pos < s.length() && isIdentifierStartChar(s.charAt(pos)))
			return pos;
		return -1;
	}

	// helper: if the final identifier at pos matches expectedId and consumes to end
	// return the provided value, otherwise empty
	private static Optional<String> matchFinalIdentifierAndReturnValue(String s, int pos, String expectedId,
			String value) {
		int idEnd = parseIdentifierEnd(s, pos);
		if (idEnd != -1) {
			int p = skipWS(s, idEnd);
			if (p == s.length() && expectedId.equals(s.substring(pos, idEnd))) {
				return Optional.of(value);
			}
		}
		return Optional.empty();
	}

	// helper: parse identifier (letter or underscore start) beginning at pos;
	// return end index (exclusive) or -1
	private static int parseIdentifierEnd(String s, int pos) {
		int len = s.length();
		if (pos < len && isIdentifierStartChar(s.charAt(pos))) {
			pos++;
			while (pos < len && (Character.isLetterOrDigit(s.charAt(pos)) || s.charAt(pos) == '_'))
				pos++;
			return pos;
		}
		return -1;
	}

	// small helper: character is valid identifier start
	private static boolean isIdentifierStartChar(char c) {
		return Character.isLetter(c) || c == '_';
	}

	private static int expectCharAndSkipWS(String s, int pos, char expected) {
		int len = s.length();
		pos = skipWS(s, pos);
		if (pos < len && s.charAt(pos) == expected) {
			pos++;
			return skipWS(s, pos);
		}
		return -1;
	}

	// helper record for let declaration parse result
	private static record LetDecl(boolean isMutable, String id, int valStart, int valEnd, int afterSemicolon) {
	}

	// helper record for assignment parse result
	private static record Assignment(String value, int afterSecondSemicolon) {
	}

	// parse a let declaration starting at pos; returns empty if not a let decl
	private static Optional<LetDecl> parseLetDecl(String s, int pos) {
		int len = s.length();
		pos = skipWS(s, pos);
		if (!startsWithKeyword(s, pos, "let"))
			return Optional.empty();
		pos += 3;
		pos = skipWS(s, pos);
		boolean isMutable = false;
		if (startsWithKeyword(s, pos, "mut")) {
			isMutable = true;
			pos += 3;
			pos = skipWS(s, pos);
		}
		// parse identifier
		Optional<Ident> idOpt = getIdentSkipping(s, pos);
		if (idOpt.isEmpty())
			return Optional.empty();
		Ident idRec = idOpt.get();
		String id = idRec.id();
		pos = idRec.end();
		int afterColon = expectCharAndSkipWS(s, pos, ':');
		if (afterColon == -1)
			return Optional.empty();
		if (!(afterColon + 3 <= len && s.substring(afterColon, afterColon + 3).equals("I32")))
			return Optional.empty();
		pos = afterColon + 3;
		Optional<NumberSpan> nsOpt = parseNumberAfterEq(s, pos);
		if (nsOpt.isEmpty())
			return Optional.empty();
		NumberSpan ns = nsOpt.get();
		int valStart = ns.start();
		int valEnd = ns.end();
		pos = skipWS(s, valEnd);
		int afterSemicolon = expectCharAndSkipWS(s, pos, ';');
		if (afterSemicolon == -1)
			return Optional.empty();
		return Optional.of(new LetDecl(isMutable, id, valStart, valEnd, afterSemicolon));
	}

	// parse an assignment of the form: id = <number> ; starting at pos; id must
	// match expectedId
	private static Optional<Assignment> parseAssignment(String s, int pos, String expectedId) {
		Optional<Ident> idOpt = getIdentSkipping(s, pos);
		if (idOpt.isEmpty())
			return Optional.empty();
		Ident idRec = idOpt.get();
		if (!idRec.id().equals(expectedId))
			return Optional.empty();
		int idEnd = idRec.end();
		Optional<int[]> spanOpt = getNumberSpanInts(s, idEnd);
		if (spanOpt.isEmpty())
			return Optional.empty();
		int assignStart = spanOpt.get()[0];
		int assignEnd = spanOpt.get()[1];
		int p = skipWS(s, assignEnd);
		int afterSecondSemicolon = expectCharAndSkipWS(s, p, ';');
		if (afterSecondSemicolon == -1)
			return Optional.empty();
		String value = s.substring(assignStart, assignEnd);
		return Optional.of(new Assignment(value, afterSecondSemicolon));
	}

	// helper: parse identifier at pos returning its text and end index
	private static record Ident(String id, int start, int end) {
		public String id() {
			return id;
		}

		public int start() {
			return start;
		}

		public int end() {
			return end;
		}
	}

	// helper: parse a (unsigned) integer after an '=' at pos; returns start/end
	private static record NumberSpan(int start, int end) {
		public int start() {
			return start;
		}

		public int end() {
			return end;
		}
	}

	// legacy: kept for reference, not used

	// new: parse identifier skipping leading whitespace (returns Ident)
	private static Optional<Ident> getIdentSkipping(String s, int pos) {
		int start = parseIdentifierStart(s, pos);
		if (start == -1)
			return Optional.empty();
		int end = parseIdentifierEnd(s, start);
		if (end == -1)
			return Optional.empty();
		return Optional.of(new Ident(s.substring(start, end), start, end));
	}

	private static Optional<NumberSpan> parseNumberAfterEq(String s, int pos) {
		int afterEq = expectCharAndSkipWS(s, pos, '=');
		if (afterEq == -1)
			return Optional.empty();
		int len = s.length();
		int valStart = afterEq;
		if (valStart < len && (s.charAt(valStart) == '+' || s.charAt(valStart) == '-'))
			valStart++;
		int valEnd = parseUnsignedIntegerEnd(s, valStart);
		if (valEnd == -1)
			return Optional.empty();
		return Optional.of(new NumberSpan(valStart, valEnd));
	}

	// new: returns int[] {start,end} for number span or empty
	private static Optional<int[]> getNumberSpanInts(String s, int pos) {
		Optional<NumberSpan> ns = parseNumberAfterEq(s, pos);
		if (ns.isEmpty())
			return Optional.empty();
		return Optional.of(new int[] { ns.get().start(), ns.get().end() });
	}

	// helper: does the string start with keyword at pos (followed by whitespace or
	// EOL)
	private static boolean startsWithKeyword(String s, int pos, String kw) {
		int len = s.length();
		if (!(len - pos >= kw.length() && s.startsWith(kw, pos)))
			return false;
		int end = pos + kw.length();
		return end == len || Character.isWhitespace(s.charAt(end));
	}
}
