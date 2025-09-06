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

		// try a tiny if-expression: if (<bool>) <int> else <int>
		try {
			String s = trimmed;
			Optional<String> ifRes = parseIfExpression(s, 0);
			if (ifRes.isPresent())
				return new Ok<>(ifRes.get());
		} catch (Exception ex) {
			// fall through to echo
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
		Optional<NumberSpan> ns = parseSignedNumberSpanNoWS(s, pos);
		return ns.map(NumberSpan::end).orElse(-1);
	}

	// helper: parse an unsigned integer starting at pos; return end index
	// (exclusive) or -1 if none
	private static int parseUnsignedIntegerEnd(String s, int pos) {
		int end = scanDigits(s, pos);
		if (end > pos)
			return end;
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
		int p = expectKeywordAndSkipWS(s, pos, "let");
		if (p == -1)
			return Optional.empty();
		pos = p;
		boolean isMutable = false;
		int mutp = expectKeywordAndSkipWS(s, pos, "mut");
		if (mutp != -1) {
			isMutable = true;
			pos = mutp;
		}
		// parse identifier
		Optional<IdentName> idNameOpt = optionalIdentName(s, pos);
		if (idNameOpt.isEmpty())
			return Optional.empty();
		IdentName idName = idNameOpt.get();
		String id = idName.name();
		pos = idName.end();
		int afterColon = expectCharAndSkipWS(s, pos, ':');
		if (afterColon == -1)
			return Optional.empty();
		if (!(afterColon + 3 <= len && s.substring(afterColon, afterColon + 3).equals("I32")))
			return Optional.empty();
		pos = afterColon + 3;
		Optional<NumberAndAfter> naaOpt = parseNumberAndExpectSemicolon(s, pos);
		if (naaOpt.isEmpty())
			return Optional.empty();
		NumberAndAfter naa = naaOpt.get();
		NumberSpan ns = naa.span();
		int valStart = ns.start();
		int valEnd = ns.end();
		int afterSemicolon = naa.afterSemicolon();
		return Optional.of(new LetDecl(isMutable, id, valStart, valEnd, afterSemicolon));
	}

	// parse an assignment of the form: id = <number> ; starting at pos; id must
	// match expectedId
	private static Optional<Assignment> parseAssignment(String s, int pos, String expectedId) {
		Optional<IdentName> idNameOpt = optionalIdentName(s, pos);
		if (idNameOpt.isEmpty())
			return Optional.empty();
		IdentName idName = idNameOpt.get();
		if (!idName.name().equals(expectedId))
			return Optional.empty();
		int idEnd = idName.end();
		Optional<NumberAndAfter> naaOpt = parseNumberAndExpectSemicolon(s, idEnd);
		if (naaOpt.isEmpty())
			return Optional.empty();
		NumberAndAfter naa = naaOpt.get();
		NumberSpan ns = naa.span();
		int assignStart = ns.start();
		int assignEnd = ns.end();
		int afterSecondSemicolon = naa.afterSemicolon();
		String value = s.substring(assignStart, assignEnd);
		return Optional.of(new Assignment(value, afterSecondSemicolon));
	}

	// helper: parse identifier at pos returning its text and end index
	private static record Ident(String id, int start, int end) {
	}

	// helper: parse a (unsigned) integer after an '=' at pos; returns start/end
	private static record NumberSpan(int start, int end) {
	}

	// helper: number span plus the index after a following semicolon
	private static record NumberAndAfter(NumberSpan span, int afterSemicolon) {
	}

	// helper: small holder for identifier name + end index to reduce repetition
	private static record IdentName(String name, int end) {
	}

	// helper: number span plus the next position after skipping whitespace
	private static record NumberSpanWithNext(NumberSpan span, int nextPos) {
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

	// helper: parse identifier name skipping WS and return name+end to reduce
	// repeated call sites
	private static Optional<IdentName> optionalIdentName(String s, int pos) {
		Optional<Ident> idOpt = getIdentSkipping(s, pos);
		if (idOpt.isEmpty())
			return Optional.empty();
		Ident id = idOpt.get();
		return Optional.of(new IdentName(id.id(), id.end()));
	}

	private static Optional<NumberSpan> parseNumberAfterEq(String s, int pos) {
		int afterEq = expectCharAndSkipWS(s, pos, '=');
		if (afterEq == -1)
			return Optional.empty();
		// parse signed number at afterEq (after skipping WS and '=' )
		Optional<NumberSpan> ns = parseSignedNumberSpanNoWS(s, afterEq);
		return ns;
	}

	// parse a signed number after '=' and require a following semicolon; returns
	// span and index after semicolon
	private static Optional<NumberAndAfter> parseNumberAndExpectSemicolon(String s, int pos) {
		Optional<NumberSpan> nsOpt = parseNumberAfterEq(s, pos);
		if (nsOpt.isEmpty())
			return Optional.empty();
		NumberSpan ns = nsOpt.get();
		int p = skipWS(s, ns.end());
		int afterSemicolon = expectCharAndSkipWS(s, p, ';');
		if (afterSemicolon == -1)
			return Optional.empty();
		return Optional.of(new NumberAndAfter(ns, afterSemicolon));
	}

	// parse signed integer span at pos without skipping whitespace; return
	// NumberSpan
	// start is the original pos (so includes optional leading sign)
	private static Optional<NumberSpan> parseSignedNumberSpanNoWS(String s, int pos) {
		int len = s.length();
		int cur = pos;
		if (cur < len && (s.charAt(cur) == '+' || s.charAt(cur) == '-'))
			cur++;
		int end = scanDigits(s, cur);
		if (end > cur)
			return Optional.of(new NumberSpan(pos, end));
		return Optional.empty();
	}

	// parse signed integer span at pos but skip leading whitespace first
	private static Optional<NumberSpan> parseSignedSpanWithNext(String s, int pos) {
		int start = skipWS(s, pos);
		return parseSignedNumberSpanNoWS(s, start);
	}

	// parse signed integer span and return span plus position after skipping WS
	private static Optional<NumberSpanWithNext> parseSignedSpanAndSkipWS(String s, int pos) {
		Optional<NumberSpan> nsOpt = parseSignedSpanWithNext(s, pos);
		if (nsOpt.isEmpty())
			return Optional.empty();
		NumberSpan ns = nsOpt.get();
		int next = skipWS(s, ns.end());
		return Optional.of(new NumberSpanWithNext(ns, next));
	}

	// scan digits starting at pos and return index after last digit
	private static int scanDigits(String s, int pos) {
		int len = s.length();
		int cur = pos;
		while (cur < len && Character.isDigit(s.charAt(cur)))
			cur++;
		return cur;
	}

	// ...existing code...

	// helper: does the string start with keyword at pos (followed by whitespace or
	// EOL)
	private static boolean startsWithKeyword(String s, int pos, String kw) {
		int len = s.length();
		if (!(len - pos >= kw.length() && s.startsWith(kw, pos)))
			return false;
		int end = pos + kw.length();
		return end == len || Character.isWhitespace(s.charAt(end));
	}

	// skip whitespace, expect keyword at this position, advance past it and skip WS
	// returns new pos or -1 if keyword not present
	private static int expectKeywordAndSkipWS(String s, int pos, String kw) {
		pos = skipWS(s, pos);
		int len = s.length();
		if (!(len - pos >= kw.length() && s.startsWith(kw, pos)))
			return -1;
		int end = pos + kw.length();
		if (!(end == len || Character.isWhitespace(s.charAt(end))))
			return -1;
		return skipWS(s, end);
	}

	// parse a minimal if-expression: if (<true|false>) <int> else <int>
	// returns Optional of the selected branch text (no trimming) or empty
	private static Optional<String> parseIfExpression(String s, int pos) {
		int len = s.length();
		pos = skipWS(s, pos);
		if (!startsWithKeyword(s, pos, "if"))
			return Optional.empty();
		pos += 2;
		pos = skipWS(s, pos);
		int open = expectCharAndSkipWS(s, pos, '(');
		if (open == -1)
			return Optional.empty();
		pos = open;
		// only accept literal true or false
		boolean cond;
		if (pos + 4 <= len && s.substring(pos, pos + 4).equals("true")) {
			cond = true;
			pos += 4;
		} else if (pos + 5 <= len && s.substring(pos, pos + 5).equals("false")) {
			cond = false;
			pos += 5;
		} else {
			return Optional.empty();
		}
		pos = skipWS(s, pos);
		if (pos >= len || s.charAt(pos) != ')')
			return Optional.empty();
		pos++;
		pos = skipWS(s, pos);
		// parse first integer
		Optional<NumberSpanWithNext> firstOpt = parseSignedSpanAndSkipWS(s, pos);
		if (firstOpt.isEmpty())
			return Optional.empty();
		NumberSpan firstNs = firstOpt.get().span();
		int firstStart = firstNs.start();
		int firstEnd = firstNs.end();
		int elsep = expectKeywordAndSkipWS(s, firstOpt.get().nextPos(), "else");
		if (elsep == -1)
			return Optional.empty();
		pos = elsep;
		Optional<NumberSpanWithNext> secondOpt = parseSignedSpanAndSkipWS(s, pos);
		if (secondOpt.isEmpty())
			return Optional.empty();
		NumberSpan secondNs = secondOpt.get().span();
		int secondStart = secondNs.start();
		int secondEnd = secondNs.end();
		pos = secondOpt.get().nextPos();
		if (pos != len)
			return Optional.empty();
		return Optional.of(s.substring(cond ? firstStart : secondStart, cond ? firstEnd : secondEnd));
	}
}
