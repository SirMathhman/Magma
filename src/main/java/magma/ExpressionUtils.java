package magma;

public class ExpressionUtils {
	private static final String[] ALLOWED_SUFFIXES = new String[] { "U8", "U16", "U32", "U64", "I8", "I16", "I32",
			"I64" };

	// Use ExpressionTypes for token/result types to keep this file small

	public static ExpressionTypes.OperandParseResult parseNumberWithSuffix(String t, int pos, String[] allowedSuffixes) {
		int n = t.length();
		if (pos >= n)
			return null;
		int sign = +1;
		if ((t.charAt(pos) == '+' || t.charAt(pos) == '-') && pos + 1 < n && Character.isDigit(t.charAt(pos + 1))) {
			if (t.charAt(pos) == '-')
				sign = -1;
			pos++;
		}
		int ds = pos;
		while (pos < n && Character.isDigit(t.charAt(pos)))
			pos++;
		if (pos == ds)
			return null;
		String digits = t.substring(ds, pos);
		String suf = null;
		for (String s : allowedSuffixes) {
			if (pos + s.length() <= n && t.startsWith(s, pos)) {
				suf = s;
				pos += s.length();
				break;
			}
		}
		long v;
		try {
			v = Long.parseLong((sign == -1 ? "-" : "") + digits);
		} catch (NumberFormatException ex) {
			return null;
		}
		return new ExpressionTypes.OperandParseResult(v, suf, pos);
	}

	public static ExpressionTypes.ExpressionTokens tokenizeExpression(String t, String[] allowedSuffixes) {
		int n = t.length();
		int pos = 0;
		ExpressionTypes.ExpressionTokens out = new ExpressionTypes.ExpressionTokens();
		ExpressionTypes.OperandParseResult first = parseNumberWithSuffix(t, pos, allowedSuffixes);
		if (first == null)
			return null;
		out.operands.add(first);
		pos = first.nextPos;

		while (true) {
			while (pos < n && Character.isWhitespace(t.charAt(pos)))
				pos++;
			if (pos >= n)
				break;
			char c = t.charAt(pos);
			if (c != '+' && c != '-' && c != '*')
				return null;
			out.operators.add(c);
			pos++;
			while (pos < n && Character.isWhitespace(t.charAt(pos)))
				pos++;
			ExpressionTypes.OperandParseResult next = parseNumberWithSuffix(t, pos, allowedSuffixes);
			if (next == null)
				return null;
			out.operands.add(next);
			pos = next.nextPos;
		}
		return out;
	}

	public static boolean suffixesConsistent(ExpressionTypes.ExpressionTokens tokens) {
		String common = null;
		for (ExpressionTypes.OperandParseResult r : tokens.operands) {
			if (r.suffix != null) {
				if (common == null)
					common = r.suffix;
				else if (!common.equals(r.suffix))
					return false;
			}
		}
		return true;
	}

	public static ExpressionTypes.Reduction reduceMultiplications(ExpressionTypes.ExpressionTokens tokens) {
		java.util.List<Long> values = new java.util.ArrayList<>();
		java.util.List<Character> ops = new java.util.ArrayList<>();
		long current = tokens.operands.get(0).value;
		for (int i = 0; i < tokens.operators.size(); i++) {
			char op = tokens.operators.get(i);
			ExpressionTypes.OperandParseResult next = tokens.operands.get(i + 1);
			if (op == '*')
				current = current * next.value;
			else {
				values.add(current);
				ops.add(op);
				current = next.value;
			}
		}
		values.add(current);
		return new ExpressionTypes.Reduction(values, ops);
	}

	// shorter alias to satisfy MethodName rule changes elsewhere
	public static ExpressionTypes.Reduction reduceMult(ExpressionTypes.ExpressionTokens tokens) {
		return reduceMultiplications(tokens);
	}

	public static class ExprParser {
		final String s;
		int pos = 0;
		String commonSuffix = null;

		public ExprParser(String s) {
			this.s = s;
		}

		public interface VarResolver {
			long resolve(String name);

			long resolveRef(String name);
		}

		public long parseExprRes(VarResolver resolver) {
			return parseExprResInternal(resolver);
		}

		private long parseExprResInternal(VarResolver resolver) {
			long v = parseTermWithResolver(resolver);
			skipWhitespace();
			while (pos < s.length()) {
				char c = s.charAt(pos);
				if (c == '+' || c == '-') {
					pos++;
					long r = parseTermWithResolver(resolver);
					if (c == '+')
						v = v + r;
					else
						v = v - r;
					skipWhitespace();
				} else
					break;
			}
			return v;
		}

		private long parseTermWithResolver(VarResolver resolver) {
			long v = parseFactorWithResolver(resolver);
			skipWhitespace();
			while (pos < s.length()) {
				char c = s.charAt(pos);
				if (c == '*') {
					pos++;
					long r = parseFactorWithResolver(resolver);
					v = v * r;
					skipWhitespace();
				} else
					break;
			}
			return v;
		}

		private long parseFactorWithResolver(VarResolver resolver) {
			skipWhitespace();
			if (pos >= s.length())
				throw new IllegalArgumentException("Unexpected end");
			// support unary dereference '*name'
			if (pos < s.length() && s.charAt(pos) == '*') {
				pos++;
				skipWhitespace();
				if (pos < s.length() && Character.isJavaIdentifierStart(s.charAt(pos))) {
					int start = pos;
					pos++;
					while (pos < s.length() && Character.isJavaIdentifierPart(s.charAt(pos)))
						pos++;
					String name = s.substring(start, pos);
					long v = resolver.resolveRef(name);
					skipWhitespace();
					return v;
				} else {
					throw new IllegalArgumentException("Invalid dereference");
				}
			}
			boolean unaryMinus = detectAndConsumeUnarySign();
			skipWhitespace();
			return parseParenOrValRes(resolver, unaryMinus);
		}

		private long parseParenOrValRes(VarResolver resolver, boolean unaryMinus) {
			if (pos < s.length() && s.charAt(pos) == '(') {
				pos++;
				long v = parseExprResInternal(resolver);
				skipWhitespace();
				if (pos >= s.length() || s.charAt(pos) != ')')
					throw new IllegalArgumentException("Missing )");
				pos++;
				return unaryMinus ? -v : v;
			}
			if (pos < s.length() && Character.isJavaIdentifierStart(s.charAt(pos))) {
				int start = pos;
				pos++;
				while (pos < s.length() && Character.isJavaIdentifierPart(s.charAt(pos)))
					pos++;
				String name = s.substring(start, pos);
				long v = resolver.resolve(name);
				return unaryMinus ? -v : v;
			}
			ExpressionTypes.OperandParseResult r = parseNumberWithSuffix(s, pos, ALLOWED_SUFFIXES);
			if (r == null)
				throw new IllegalArgumentException("Invalid number");
			pos = r.nextPos;
			checkAndSetSuffix(r.suffix);
			return unaryMinus ? -r.value : r.value;
		}

		private void checkAndSetSuffix(String suf) {
			if (suf == null)
				return;
			if (commonSuffix == null)
				commonSuffix = suf;
			else if (!commonSuffix.equals(suf))
				throw new IllegalArgumentException("Mixed suffixes");
		}

		public boolean isAtEnd() {
			return pos >= s.length();
		}

		public void skipWhitespace() {
			while (pos < s.length() && Character.isWhitespace(s.charAt(pos)))
				pos++;
		}

		public long parseExpression() {
			long v = parseTerm();
			skipWhitespace();
			while (pos < s.length()) {
				char c = s.charAt(pos);
				if (c == '+' || c == '-') {
					pos++;
					long r = parseTerm();
					if (c == '+')
						v = v + r;
					else
						v = v - r;
					skipWhitespace();
				} else
					break;
			}
			return v;
		}

		public long parseTerm() {
			long v = parseFactor();
			skipWhitespace();
			while (pos < s.length()) {
				char c = s.charAt(pos);
				if (c == '*') {
					pos++;
					long r = parseFactor();
					v = v * r;
					skipWhitespace();
				} else
					break;
			}
			return v;
		}

		public long parseFactor() {
			skipWhitespace();
			if (pos >= s.length())
				throw new IllegalArgumentException("Unexpected end");
			// support unary dereference in non-resolver path (not used by App right now)
			if (pos < s.length() && s.charAt(pos) == '*') {
				pos++;
				skipWhitespace();
				if (pos < s.length() && Character.isJavaIdentifierStart(s.charAt(pos))) {
					int start = pos;
					pos++;
					while (pos < s.length() && Character.isJavaIdentifierPart(s.charAt(pos)))
						pos++;
					String name = s.substring(start, pos);
					throw new IllegalArgumentException("Dereference not available without resolver");
				} else {
					throw new IllegalArgumentException("Invalid dereference");
				}
			}
			boolean unaryMinus = detectAndConsumeUnarySign();
			skipWhitespace();
			if (pos < s.length() && s.charAt(pos) == '(') {
				pos++;
				long v = parseExpression();
				skipWhitespace();
				if (pos >= s.length() || s.charAt(pos) != ')')
					throw new IllegalArgumentException("Missing )");
				pos++;
				return unaryMinus ? -v : v;
			}
			ExpressionTypes.OperandParseResult r = parseNumberWithSuffix(s, pos, ALLOWED_SUFFIXES);
			if (r == null)
				throw new IllegalArgumentException("Invalid number");
			pos = r.nextPos;
			if (r.suffix != null) {
				if (commonSuffix == null)
					commonSuffix = r.suffix;
				else if (!commonSuffix.equals(r.suffix))
					throw new IllegalArgumentException("Mixed suffixes");
			}
			return unaryMinus ? -r.value : r.value;
		}

		private boolean detectAndConsumeUnarySign() {
			if (pos >= s.length())
				return false;
			if (s.charAt(pos) != '+' && s.charAt(pos) != '-')
				return false;
			char signChar = s.charAt(pos);
			int look = pos + 1;
			while (look < s.length() && Character.isWhitespace(s.charAt(look)))
				look++;
			if (look < s.length() && (s.charAt(look) == '(' || Character.isDigit(s.charAt(look)))) {
				pos = look;
				return signChar == '-';
			}
			return false;
		}
	}
}
