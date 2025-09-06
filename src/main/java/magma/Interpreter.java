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
					if (op == '+' || op == '-' || op == '*' || op == '/') {
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
								}
								parsedBinary = true;
							}
						}
					}
				}
			}
			if (parsedBinary)
				return new Ok<>(Long.toString(res));
		} catch (NumberFormatException ex) {
			// fall through to echo
		}
		// fallback: echo input; also try a tiny let-binding form
		java.util.Optional<String> mutRes = parseMutableLetBinding(trimmed);
		if (mutRes.isPresent())
			return new Ok<>(mutRes.get());
		java.util.Optional<String> letRes = parseLetBinding(trimmed);
		if (letRes.isPresent())
			return new Ok<>(letRes.get());
		return new Ok<>(input);
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

	private static java.util.Optional<String> parseLetBinding(String s) {
		int len = s.length();
		if (!(len >= 4 && s.startsWith("let") && Character.isWhitespace(s.charAt(3))))
			return java.util.Optional.empty();
		int p = 3;
		while (p < len && Character.isWhitespace(s.charAt(p)))
			p++;
		java.util.Optional<IdentValueResult> decl = parseIdentTypeValue(s, p);
		if (decl.isEmpty()) return java.util.Optional.empty();
		IdentValueResult declv = decl.get();
		String name = declv.name;
		ParseResult val = declv.value;
		p = declv.nextIndex;
		p = skipWhitespace(s, p);
		ParseIdentResult idr2 = parseIdentifier(s, p);
		if (!idr2.success) return java.util.Optional.empty();
		String name2 = idr2.name.orElse("");
		p = idr2.nextIndex;
		p = skipWhitespace(s, p);
		if (p != len) return java.util.Optional.empty();
		if (!name.equals(name2)) return java.util.Optional.empty();
		return java.util.Optional.of(Long.toString(val.value));
	}

	private static java.util.Optional<String> parseMutableLetBinding(String s) {
		int len = s.length();
		// expect "let" then whitespace then "mut"
		int p = 0;
		if (!(len >= 6 && s.startsWith("let") && Character.isWhitespace(s.charAt(3)))) return java.util.Optional.empty();
		p = 3;
		p = skipWhitespace(s, p);
		// expect "mut"
		if (p + 3 > len || !s.substring(p, Math.min(len, p + 3)).equals("mut")) return java.util.Optional.empty();
		p += 3;
		p = skipWhitespace(s, p);
		// identifier
		ParseIdentResult idr = parseIdentifier(s, p);
		if (!idr.success) return java.util.Optional.empty();
		String name = idr.name.orElse("");
		p = idr.nextIndex;
		// parse initial type and initializer: : I32 = <int>;
		java.util.Optional<ParseResult> initOpt = parseTypeI32EqualsInt(s, p);
		if (initOpt.isEmpty()) return java.util.Optional.empty();
		ParseResult init = initOpt.get();
		p = init.nextIndex;
		p = skipWhitespace(s, p);
		// expect assignment: <ident> = <int>;
		java.util.Optional<IdentValueResult> assign = parseIdentAssignValue(s, p);
		if (assign.isEmpty()) return java.util.Optional.empty();
		IdentValueResult assignv = assign.get();
		if (!name.equals(assignv.name)) return java.util.Optional.empty();
		ParseResult assigned = assignv.value;
		p = assignv.nextIndex;
		// final identifier
		p = skipWhitespace(s, p);
		ParseIdentResult idr3 = parseIdentifier(s, p);
		if (!idr3.success) return java.util.Optional.empty();
		String name3 = idr3.name.orElse("");
		p = idr3.nextIndex;
		p = skipWhitespace(s, p);
		if (p != len) return java.util.Optional.empty();
		if (!name.equals(name3)) return java.util.Optional.empty();
		return java.util.Optional.of(Long.toString(assigned.value));
	}

	private static java.util.Optional<ParseResult> parseTypeI32EqualsInt(String s, int from) {
		int len = s.length();
		int p = skipWhitespace(s, from);
		p = expectChar(s, p, ':');
		if (p == -1) return java.util.Optional.empty();
		p = skipWhitespace(s, p);
		if (p + 3 > len || !s.substring(p, Math.min(len, p + 3)).equals("I32")) return java.util.Optional.empty();
		p += 3;
		p = expectChar(s, p, '=');
		if (p == -1) return java.util.Optional.empty();
		p = skipWhitespace(s, p);
		ParseResult val = parseSignedLong(s, p);
		if (!val.success) return java.util.Optional.empty();
		p = val.nextIndex;
		p = skipWhitespace(s, p);
		if (p >= len || s.charAt(p) != ';') return java.util.Optional.empty();
		return java.util.Optional.of(new ParseResult(true, val.value, p + 1));
	}

	private static java.util.Optional<ParseResult> parseEqualsIntSemicolon(String s, int from) {
		int len = s.length();
		int p = skipWhitespace(s, from);
		p = expectChar(s, p, '=');
		if (p == -1) return java.util.Optional.empty();
		p = skipWhitespace(s, p);
		ParseResult val = parseSignedLong(s, p);
		if (!val.success) return java.util.Optional.empty();
		p = val.nextIndex;
		p = skipWhitespace(s, p);
		if (p >= len || s.charAt(p) != ';') return java.util.Optional.empty();
		return java.util.Optional.of(new ParseResult(true, val.value, p + 1));
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
		// parses: <ident> : I32 = <int> ;
		int p = from;
		ParseIdentResult idr = parseIdentifier(s, p);
		if (!idr.success) return java.util.Optional.empty();
		String name = idr.name.orElse("");
		p = idr.nextIndex;
		java.util.Optional<ParseResult> opt = parseTypeI32EqualsInt(s, p);
		if (opt.isEmpty()) return java.util.Optional.empty();
		ParseResult val = opt.get();
		return java.util.Optional.of(new IdentValueResult(name, val, val.nextIndex));
	}

	private static java.util.Optional<IdentValueResult> parseIdentAssignValue(String s, int from) {
		// parses: <ident> = <int> ;
		int p = from;
		ParseIdentResult idr = parseIdentifier(s, p);
		if (!idr.success) return java.util.Optional.empty();
		String name = idr.name.orElse("");
		p = idr.nextIndex;
		java.util.Optional<ParseResult> opt = parseEqualsIntSemicolon(s, p);
		if (opt.isEmpty()) return java.util.Optional.empty();
		ParseResult val = opt.get();
		return java.util.Optional.of(new IdentValueResult(name, val, val.nextIndex));
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
}
