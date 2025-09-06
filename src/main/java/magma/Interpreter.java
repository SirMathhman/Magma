package magma;

import java.util.Objects;

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
			// parse first number
			ParseResult r1 = parseSignedLong(trimmed, i);
			if (!r1.success)
				return new Ok<>(input);
			i = r1.nextIndex;
			// skip whitespace
			while (i < len && Character.isWhitespace(trimmed.charAt(i)))
				i++;
			// operator
			if (i >= len)
				return new Ok<>(input);
			char op = trimmed.charAt(i);
			if (op != '+' && op != '-' && op != '*' && op != '/')
				return new Ok<>(input);
			i++;
			// skip whitespace
			while (i < len && Character.isWhitespace(trimmed.charAt(i)))
				i++;
			// parse second number
			ParseResult r2 = parseSignedLong(trimmed, i);
			if (!r2.success)
				return new Ok<>(input);
			i = r2.nextIndex;
			// any trailing non-whitespace means not a pure binary expr
			while (i < len) {
				if (!Character.isWhitespace(trimmed.charAt(i))) {
					return new Ok<>(input);
				}
				i++;
			}
			long a = r1.value;
			long b = r2.value;
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
					return new Ok<>(trimmed); // shouldn't happen
			}
			return new Ok<>(Long.toString(res));
		} catch (NumberFormatException ex) {
			// fall through to echo
		}
		// fallback: echo input
		return new Ok<>(input);
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
