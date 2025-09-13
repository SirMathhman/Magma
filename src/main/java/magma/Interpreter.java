package magma;

import java.util.Optional;

public class Interpreter {
	public static Result<String, InterpreterError> interpret(String source) {
		if (source.isEmpty())
			return new Ok<>("");

		// Try precedence-aware expression evaluation (+, -, *) first
		Optional<Result<String, InterpreterError>> eval = tryEvalExpression(source);
		if (eval.isPresent())
			return eval.get();

		// Try multiplication fallback
		Optional<Result<String, InterpreterError>> mul = tryMul(source);
		if (mul.isPresent())
			return mul.get();

		// Try parse as integer
		try {
			Integer.parseInt(source);
			return new Ok<>(source);
		} catch (NumberFormatException e) {
			// Extract leading digits if present
			String lead = leadingDigits(source);
			if (!lead.isEmpty())
				return new Ok<>(lead);
			return new Err<>(new InterpreterError("Invalid input", source));
		}
	}

	private static Optional<Result<String, InterpreterError>> tryAddSub(String source) {
		Optional<Expression> expr = parseExpression(source);
		if (expr.isEmpty())
			return Optional.empty();
		Expression e = expr.get();
		if (e.values.size() < 2)
			return Optional.empty();
		if (!hasConsistentSuffixes(e))
			return Optional.of(new Err<>(new InterpreterError("Invalid input", source)));
		int result = evaluateExpression(e);
		return Optional.of(new Ok<>(String.valueOf(result)));
	}

	private static Optional<Result<String, InterpreterError>> tryMul(String source) {
		if (!source.contains("*"))
			return Optional.empty();
		String[] parts = source.split("\\*");
		if (parts.length < 2)
			return Optional.empty();

		int prod = 1;
		String commonSuffix = "";
		boolean anyParsed = false;

		for (String raw : parts) {
			String part = raw.trim();

			try {
				int v = Integer.parseInt(part);
				prod *= v;
				anyParsed = true;
				continue;
			} catch (NumberFormatException ignored) {
				// fall through
			}

			String ld = leadingDigits(part);
			if (ld.isEmpty())
				return Optional.empty();

			String suffix = part.substring(ld.length());
			if (!suffix.isEmpty()) {
				if (commonSuffix.isEmpty())
					commonSuffix = suffix;
				else if (!commonSuffix.equals(suffix)) {
					return Optional.of(new Err<>(new InterpreterError("Invalid input", source)));
				}
			}

			prod *= Integer.parseInt(ld);
			anyParsed = true;
		}

		if (!anyParsed)
			return Optional.empty();
		return Optional.of(new Ok<>(String.valueOf(prod)));
	}

	// Precedence-aware evaluator: handles +, -, * with * having higher precedence
	private static Optional<Result<String, InterpreterError>> tryEvalExpression(String source) {
		Parser p = new Parser(source);
		Optional<Integer> v = p.parseExpression();
		if (v.isEmpty())
			return Optional.empty();
		if (!p.atEnd())
			return Optional.empty();
		if (!p.hasConsistentSuffixes()) {
			return Optional.of(new Err<>(new InterpreterError("Invalid input", source)));
		}
		return Optional.of(new Ok<>(String.valueOf(v.get())));
	}

	private static class Parser {
		private final String s;
		private int i;
		private final java.util.List<String> suffixes = new java.util.ArrayList<>();

		Parser(String s) {
			this.s = s;
			this.i = 0;
		}

		Optional<Integer> parseExpression() {
			Optional<Integer> left = parseTerm();
			if (left.isEmpty())
				return Optional.empty();
			int acc = left.get();
			while (true) {
				skipSpaces();
				if (atEnd())
					break;
				char c = s.charAt(i);
				if (c == '+' || c == '-') {
					i++;
					Optional<Integer> right = parseTerm();
					if (right.isEmpty())
						return Optional.empty();
					if (c == '+')
						acc += right.get();
					else
						acc -= right.get();
				} else
					break;
			}
			return Optional.of(acc);
		}

		Optional<Integer> parseTerm() {
			Optional<Integer> left = parseFactor();
			if (left.isEmpty())
				return Optional.empty();
			int acc = left.get();
			while (true) {
				skipSpaces();
				if (atEnd())
					break;
				char c = s.charAt(i);
				if (c == '*') {
					i++;
					Optional<Integer> right = parseFactor();
					if (right.isEmpty())
						return Optional.empty();
					acc *= right.get();
				} else
					break;
			}
			return Optional.of(acc);
		}

		Optional<Integer> parseFactor() {
			skipSpaces();
			if (atEnd())
				return Optional.empty();
			// parse number with optional suffix
			StringBuilder digits = new StringBuilder();
			while (i < s.length() && Character.isDigit(s.charAt(i))) {
				digits.append(s.charAt(i));
				i++;
			}
			if (digits.length() == 0)
				return Optional.empty();
			StringBuilder suf = new StringBuilder();
			while (i < s.length() && s.charAt(i) != '+' && s.charAt(i) != '-' && s.charAt(i) != '*') {
				suf.append(s.charAt(i));
				i++;
			}
			String suffix = suf.toString().trim();
			this.suffixes.add(suffix);
			return Optional.of(Integer.parseInt(digits.toString()));
		}

		void skipSpaces() {
			while (i < s.length() && Character.isWhitespace(s.charAt(i)))
				i++;
		}

		boolean atEnd() {
			skipSpaces();
			return i >= s.length();
		}

		boolean hasConsistentSuffixes() {
			String common = "";
			for (String sfx : suffixes) {
				if (sfx.isEmpty())
					continue;
				if (common.isEmpty())
					common = sfx;
				else if (!common.equals(sfx))
					return false;
			}
			return true;
		}
	}

	private static class Expression {
		java.util.List<Integer> values = new java.util.ArrayList<>();
		java.util.List<Character> ops = new java.util.ArrayList<>();
		java.util.List<String> suffixes = new java.util.ArrayList<>();
	}

	private static Optional<Expression> parseExpression(String source) {
		int len = source.length();
		int i = 0;
		Expression e = new Expression();
		Optional<Token> t = parseTokenAt(source, i);
		if (t.isEmpty())
			return Optional.empty();
		Token tok = t.get();
		e.values.add(tok.value);
		e.suffixes.add(tok.suffix);
		i = tok.nextIndex;

		while (i < len) {
			while (i < len && Character.isWhitespace(source.charAt(i)))
				i++;
			if (i >= len)
				break;
			char c = source.charAt(i);
			if (c != '+' && c != '-')
				break;
			e.ops.add(c);
			i++;
			Optional<Token> nt = parseTokenAt(source, i);
			if (nt.isEmpty())
				return Optional.empty();
			Token nv = nt.get();
			e.values.add(nv.value);
			e.suffixes.add(nv.suffix);
			i = nv.nextIndex;
		}
		return Optional.of(e);
	}

	private static boolean hasConsistentSuffixes(Expression e) {
		String common = "";
		for (String s : e.suffixes) {
			if (s.isEmpty())
				continue;
			if (common.isEmpty())
				common = s;
			else if (!common.equals(s))
				return false;
		}
		return true;
	}

	private static int evaluateExpression(Expression e) {
		int acc = e.values.get(0);
		for (int k = 0; k < e.ops.size(); k++) {
			char op = e.ops.get(k);
			int v = e.values.get(k + 1);
			if (op == '+')
				acc += v;
			else
				acc -= v;
		}
		return acc;
	}

	private static class Token {
		final int value;
		final String suffix;
		final int nextIndex;

		Token(int value, String suffix, int nextIndex) {
			this.value = value;
			this.suffix = suffix;
			this.nextIndex = nextIndex;
		}
	}

	private static Optional<Token> parseTokenAt(String source, int start) {
		int len = source.length();
		int i = start;
		while (i < len && Character.isWhitespace(source.charAt(i)))
			i++;
		if (i >= len)
			return Optional.empty();

		StringBuilder digits = new StringBuilder();
		while (i < len && Character.isDigit(source.charAt(i))) {
			digits.append(source.charAt(i));
			i++;
		}
		if (digits.length() == 0)
			return Optional.empty();

		StringBuilder suf = new StringBuilder();
		while (i < len && source.charAt(i) != '+' && source.charAt(i) != '-') {
			suf.append(source.charAt(i));
			i++;
		}
		return Optional.of(new Token(Integer.parseInt(digits.toString()), suf.toString().trim(), i));
	}

	private static String leadingDigits(String s) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (Character.isDigit(c))
				sb.append(c);
			else
				break;
		}
		return sb.toString();
	}
}
