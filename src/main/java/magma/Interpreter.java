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
		// regex-free parsing: scan for [sign]digits [whitespace] op [whitespace]
		// [sign]digits
		try {
			int len = trimmed.length();
			int i = 0;
			boolean parsedBinary = false;
			long res = 0L;
			// parse first number
			ParseResult r1 = parseSignedLong(trimmed, i);
			if (r1.success) {
				i = r1.nextIndex;
				// skip whitespace
				while (i < len && Character.isWhitespace(trimmed.charAt(i)))
					i++;
				// operator
				if (i < len) {
					char op = trimmed.charAt(i);
					if (op == '+' || op == '-' || op == '*' || op == '/' || op == '<') {
						i++;
						// skip whitespace
						while (i < len && Character.isWhitespace(trimmed.charAt(i)))
							i++;
						// parse second number
						ParseResult r2 = parseSignedLong(trimmed, i);
						if (r2.success) {
							i = r2.nextIndex;
							// any trailing non-whitespace means not a pure binary expr
							int j = i;
							while (j < len) {
								if (!Character.isWhitespace(trimmed.charAt(j))) {
									j = -1;
									break;
								}
								j++;
							}
							if (j != -1) {
								long a = r1.value;
								long b = r2.value;
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
									case '<':
										// we'll encode boolean results using res==1 for true, 0 for false
										res = (a < b) ? 1L : 0L;
										break;
								}
								parsedBinary = true;
							}
						}
					}
				}
			}
			if (parsedBinary) {
				// If operator was '<', return boolean string; otherwise numeric
				// A '<' sets res to 1 for true and 0 for false above; detect by checking
				// whether the original trimmed contains '<' between numbers. Simpler: if
				// res is 0 or 1 and trimmed contains '<', return boolean.
				if (trimmed.contains("<")) {
					return new Ok<>((res == 1L) ? "true" : "false");
				}
				return new Ok<>(Long.toString(res));
			}
		} catch (NumberFormatException ex) {
			// fall through to echo
		}
		// fallback: try a tiny if-expression, then let-binding/mutable-let, otherwise
		// echo
		java.util.Optional<String> ifRes = parseIfExpression(trimmed);
		if (ifRes.isPresent())
			return new Ok<>(ifRes.get());
		java.util.Optional<String> letRes = parseLetForm(trimmed);
		if (letRes.isPresent())
			return new Ok<>(letRes.get());
		return new Ok<>(input);
	}

	private static java.util.Optional<Integer> tryConsumeMut(String s, int from) {
		int len = s.length();
		int p = skipWhitespace(s, from);
		if (p + 3 <= len && s.substring(p, Math.min(len, p + 3)).equals("mut")) {
			int after = p + 3;
			if (after < len && !Character.isWhitespace(s.charAt(after)))
				return java.util.Optional.empty();
			return java.util.Optional.of(after);
		}
		return java.util.Optional.of(p);
	}

	private static final class ParseIdentResult {
		final boolean success;
		final Optional<String> name;
		final int nextIndex;

		ParseIdentResult(boolean success, Optional<String> name, int nextIndex) {
			this.success = success;
			this.name = name;
			this.nextIndex = nextIndex;
		}
	}

	private static final class NameIndex {
		final String name;
		final int nextIndex;

		NameIndex(String name, int nextIndex) {
			this.name = name;
			this.nextIndex = nextIndex;
		}
	}

	private static java.util.Optional<NameIndex> parseIdentifierName(String s, int from) {
		ParseIdentResult idr = parseIdentifier(s, from);
		if (!idr.success)
			return java.util.Optional.empty();
		int next = skipWhitespace(s, idr.nextIndex);
		return java.util.Optional.of(new NameIndex(idr.name.orElse(""), next));
	}

	private static ParseIdentResult parseIdentifier(String s, int from) {
		int len = s.length();
		int i = from;
		i = skipWhitespace(s, i);
		if (i >= len)
			return new ParseIdentResult(false, Optional.empty(), from);
		if (!(Character.isLetter(s.charAt(i)) || s.charAt(i) == '_'))
			return new ParseIdentResult(false, Optional.empty(), from);
		int start = i;
		i++;
		while (i < len && (Character.isLetterOrDigit(s.charAt(i)) || s.charAt(i) == '_'))
			i++;
		String name = s.substring(start, i);
		return new ParseIdentResult(true, Optional.of(name), i);
	}

	private static int skipWhitespace(String s, int from) {
		int i = from;
		int len = s.length();
		while (i < len && Character.isWhitespace(s.charAt(i)))
			i++;
		return i;
	}

	private static int expectChar(String s, int from, char c) {
		int i = skipWhitespace(s, from);
		if (i >= s.length() || s.charAt(i) != c)
			return -1;
		return i + 1;
	}

	private static java.util.Optional<String> parseLetForm(String s) {
		int len = s.length();
		if (!(len >= 4 && s.startsWith("let") && Character.isWhitespace(s.charAt(3))))
			return java.util.Optional.empty();
		int p = 3;
		p = skipWhitespace(s, p);
		boolean isMut = false;
		java.util.Optional<Integer> mutOpt = tryConsumeMut(s, p);
		if (mutOpt.isEmpty())
			return java.util.Optional.empty();
		int afterMut = mutOpt.get();
		if (afterMut != p)
			isMut = true;
		p = afterMut;
		// parse identifier and its initializer: either typed (<ident> : I32 = <int>;)
		// or untyped (<ident> = <int>;)
		java.util.Optional<IdentValueResult> declOpt = parseIdentTypeValue(s, p);
		if (declOpt.isEmpty()) {
			// allow untyped initializer form
			declOpt = parseIdentAssignValue(s, p);
		}
		if (declOpt.isEmpty())
			return java.util.Optional.empty();
		IdentValueResult decl = declOpt.get();
		String name = decl.name;
		ParseResult init = decl.value;
		p = decl.nextIndex;
		if (!isMut) {
			// allow additional top-level `let` declarations before the final identifier
			int cur = p;
			for (;;) {
				cur = skipWhitespace(s, cur);
				if (cur >= len)
					return java.util.Optional.empty();
				// if another 'let' starts here, parse it and advance
				if (cur + 3 <= len && s.startsWith("let", cur) && (cur + 3 == len || Character.isWhitespace(s.charAt(cur + 3)))) {
					int q = cur + 3;
					q = skipWhitespace(s, q);
					// optional 'mut' on the inner declaration; accept but don't alter outer binding
					java.util.Optional<Integer> innerMutOpt = tryConsumeMut(s, q);
					if (innerMutOpt.isEmpty())
						return java.util.Optional.empty();
					q = innerMutOpt.get();
					q = skipWhitespace(s, q);
					java.util.Optional<IdentValueResult> innerDeclOpt = parseIdentTypeValue(s, q);
					if (innerDeclOpt.isEmpty()) {
						innerDeclOpt = parseIdentAssignValue(s, q);
					}
					if (innerDeclOpt.isEmpty())
						return java.util.Optional.empty();
					cur = innerDeclOpt.get().nextIndex;
					continue;
				}
				// otherwise expect final identifier matching the original name and end-of-input
				java.util.Optional<String> finish = finalizeAndReturn(s, cur, name, init.value);
				if (finish.isEmpty())
					return java.util.Optional.empty();
				return finish;
			}
		} else {
			// try assignment: <ident> = <int> ;
			java.util.Optional<IdentValueResult> assignOpt = parseIdentAssignValue(s, p);
			if (assignOpt.isPresent()) {
				IdentValueResult assignv = assignOpt.get();
				if (!name.equals(assignv.name))
					return java.util.Optional.empty();
				ParseResult assigned = assignv.value;
				java.util.Optional<String> finish = finalizeAndReturn(s, assignv.nextIndex, name, assigned.value);
				if (finish.isEmpty())
					return java.util.Optional.empty();
				return finish;
			}
			// try plus-assign: <ident> += <int> ;
			java.util.Optional<IdentValueResult> plusOpt = parseIdentThenValueOp(s, p, false, "+=");
			if (plusOpt.isPresent()) {
				IdentValueResult pv = plusOpt.get();
				if (!name.equals(pv.name))
					return java.util.Optional.empty();
				long added = decl.value.value + pv.value.value;
				java.util.Optional<String> finish3 = finalizeAndReturn(s, pv.nextIndex, name, added);
				if (finish3.isEmpty())
					return java.util.Optional.empty();
				return finish3;
			}
			// try post-increment: <ident>++ ;
			java.util.Optional<NameIndex> postOpt = parsePostIncrementName(s, p);
			if (postOpt.isEmpty())
				return java.util.Optional.empty();
			NameIndex post = postOpt.get();
			if (!name.equals(post.name))
				return java.util.Optional.empty();
			// apply post-increment to the declared initial value
			long incremented = decl.value.value + 1;
			java.util.Optional<String> finish2 = finalizeAndReturn(s, post.nextIndex, name, incremented);
			if (finish2.isEmpty())
				return java.util.Optional.empty();
			return finish2;
		}
	}

	private static java.util.Optional<String> finalizeAndReturn(String s, int from, String expectedName, long value) {
		java.util.Optional<Integer> finalIdx = parseFinalIdentMatch(s, from, expectedName);
		if (finalIdx.isEmpty())
			return java.util.Optional.empty();
		return java.util.Optional.of(Long.toString(value));
	}

	private static java.util.Optional<ParseResult> parseOpThenSignedLongSemicolon(String s, int from, String op) {
		int p = skipWhitespace(s, from);
		int oplen = op.length();
		if (p + oplen > s.length())
			return java.util.Optional.empty();
		for (int k = 0; k < oplen; k++)
			if (s.charAt(p + k) != op.charAt(k))
				return java.util.Optional.empty();
		p += oplen;
		return parseSignedLongOptSemicolon(s, p, true);
	}

	private static java.util.Optional<ParseResult> parseSignedLongOptSemicolon(String s, int from,
			boolean requireSemicolon) {
		int len = s.length();
		int p = skipWhitespace(s, from);
		ParseResult val = parseSignedLong(s, p);
		if (!val.success)
			return java.util.Optional.empty();
		p = val.nextIndex;
		p = skipWhitespace(s, p);
		if (requireSemicolon) {
			if (p >= len || s.charAt(p) != ';')
				return java.util.Optional.empty();
			return java.util.Optional.of(new ParseResult(true, val.value, p + 1));
		} else {
			return java.util.Optional.of(new ParseResult(true, val.value, p));
		}
	}

	private static final class IdentValueResult {
		final String name;
		final ParseResult value;
		final int nextIndex;

		IdentValueResult(String name, ParseResult value, int nextIndex) {
			this.name = name;
			this.value = value;
			this.nextIndex = nextIndex;
		}
	}

	private static java.util.Optional<IdentValueResult> parseIdentTypeValue(String s, int from) {
		return parseIdentThenValue(s, from, true);
	}

	private static java.util.Optional<IdentValueResult> parseIdentAssignValue(String s, int from) {
		return parseIdentThenValue(s, from, false);
	}

	private static java.util.Optional<IdentValueResult> parseIdentThenValue(String s, int from, boolean typeForm) {
		return parseIdentThenValueOp(s, from, typeForm, "=");
	}

	private static java.util.Optional<IdentValueResult> parseIdentThenValueOp(String s, int from, boolean typeForm,
			String op) {
		int p = from;
		java.util.Optional<NameIndex> nameOpt = parseIdentifierName(s, p);
		if (nameOpt.isEmpty())
			return java.util.Optional.empty();
		NameIndex ni = nameOpt.get();
		String name = ni.name;
		p = ni.nextIndex;
		if (typeForm) {
			int len = s.length();
			p = skipWhitespace(s, p);
			p = expectChar(s, p, ':');
			if (p == -1)
				return java.util.Optional.empty();
			p = skipWhitespace(s, p);
			if (p + 3 > len || !s.substring(p, Math.min(len, p + 3)).equals("I32"))
				return java.util.Optional.empty();
			p += 3;
		}
		java.util.Optional<ParseResult> pr = parseOpThenSignedLongSemicolon(s, p, op);
		if (pr.isEmpty())
			return java.util.Optional.empty();
		ParseResult val = pr.get();
		return java.util.Optional.of(new IdentValueResult(name, val, val.nextIndex));
	}

	private static java.util.Optional<Integer> parseFinalIdentMatch(String s, int from, String expectedName) {
		java.util.Optional<NameIndex> niOpt = parseIdentifierName(s, from);
		if (niOpt.isEmpty())
			return java.util.Optional.empty();
		NameIndex ni = niOpt.get();
		if (ni.nextIndex != s.length())
			return java.util.Optional.empty();
		if (!expectedName.equals(ni.name))
			return java.util.Optional.empty();
		return java.util.Optional.of(ni.nextIndex);
	}

	private static java.util.Optional<NameIndex> parsePostIncrementName(String s, int from) {
		java.util.Optional<NameIndex> niOpt = parseIdentifierName(s, from);
		if (niOpt.isEmpty())
			return java.util.Optional.empty();
		NameIndex ni = niOpt.get();
		int p = ni.nextIndex;
		// expect '++' immediately after identifier (allowing whitespace was already
		// skipped)
		if (p + 2 > s.length() || s.charAt(p) != '+' || s.charAt(p + 1) != '+')
			return java.util.Optional.empty();
		p += 2;
		p = skipWhitespace(s, p);
		if (p >= s.length() || s.charAt(p) != ';')
			return java.util.Optional.empty();
		p++;
		return java.util.Optional.of(new NameIndex(ni.name, p));
	}

	private static java.util.Optional<String> parseIfExpression(String s) {
		// parse: if ( <bool-literal> ) <int> else <int>
		int len = s.length();
		int p = 0;
		p = skipWhitespace(s, p);
		if (!s.startsWith("if", p))
			return java.util.Optional.empty();
		p += 2;
		p = skipWhitespace(s, p);
		p = expectChar(s, p, '(');
		if (p == -1)
			return java.util.Optional.empty();
		p = skipWhitespace(s, p);
		// parse boolean literal: true | false
		boolean condVal;
		if (p + 4 <= len && s.substring(p, Math.min(len, p + 4)).equals("true")) {
			condVal = true;
			p += 4;
		} else if (p + 5 <= len && s.substring(p, Math.min(len, p + 5)).equals("false")) {
			condVal = false;
			p += 5;
		} else {
			return java.util.Optional.empty();
		}
		p = skipWhitespace(s, p);
		p = expectChar(s, p, ')');
		if (p == -1)
			return java.util.Optional.empty();
		p = skipWhitespace(s, p);
		// parse then-expression and advance
		int[] ph = new int[] { p };
		java.util.Optional<ParseResult> thenOpt = parseAndAdvanceSignedLongNoSemicolon(s, ph);
		if (thenOpt.isEmpty())
			return java.util.Optional.empty();
		ParseResult thenVal = thenOpt.get();
		p = ph[0];
		p = skipWhitespace(s, p);
		// expect 'else'
		if (!(p + 4 <= len && s.substring(p, Math.min(len, p + 4)).equals("else")))
			return java.util.Optional.empty();
		p += 4;
		p = skipWhitespace(s, p);
		ph[0] = p;
		java.util.Optional<ParseResult> elseOpt = parseAndAdvanceSignedLongNoSemicolon(s, ph);
		if (elseOpt.isEmpty())
			return java.util.Optional.empty();
		ParseResult elseVal = elseOpt.get();
		p = ph[0];
		p = skipWhitespace(s, p);
		if (p != len)
			return java.util.Optional.empty();
		return java.util.Optional.of(Long.toString(condVal ? thenVal.value : elseVal.value));
	}

	// helper returned by parseSignedLong
	private static final class ParseResult {
		final boolean success;
		final long value;
		final int nextIndex;

		ParseResult(boolean success, long value, int nextIndex) {
			this.success = success;
			this.value = value;
			this.nextIndex = nextIndex;
		}
	}

	// Parse a signed long starting at index 'from' in the string. If parsing fails,
	// return success=false.
	private static ParseResult parseSignedLong(String s, int from) {
		int len = s.length();
		int i = from;
		boolean negative = false;
		if (i < len && (s.charAt(i) == '+' || s.charAt(i) == '-')) {
			if (s.charAt(i) == '-')
				negative = true;
			i++;
		}
		int startDigits = i;
		while (i < len && Character.isDigit(s.charAt(i)))
			i++;
		if (startDigits == i) {
			return new ParseResult(false, 0L, from);
		}
		String digits = s.substring(startDigits, i);
		long value;
		try {
			value = Long.parseLong((negative ? "-" : "") + digits);
		} catch (NumberFormatException ex) {
			return new ParseResult(false, 0L, from);
		}
		return new ParseResult(true, value, i);
	}

	private static java.util.Optional<ParseResult> parseAndAdvanceSignedLongNoSemicolon(String s, int[] pHolder) {
		int p = skipWhitespace(s, pHolder[0]);
		ParseResult val = parseSignedLong(s, p);
		if (!val.success)
			return java.util.Optional.empty();
		p = val.nextIndex;
		p = skipWhitespace(s, p);
		pHolder[0] = p;
		return java.util.Optional.of(new ParseResult(true, val.value, p));
	}
}
