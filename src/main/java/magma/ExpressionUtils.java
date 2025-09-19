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

	public static String[] getAllowedSuffixes() {
		return ALLOWED_SUFFIXES;
	}

}
