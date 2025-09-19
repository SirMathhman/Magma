package magma;

public class MatchEvaluator {
	public static String tryEvalMatchExpression(String t) {
		MatchHeader mh = parseMatchHeader(t);
		if (mh == null)
			return null;
		return evaluateArms(mh.scrutineeValue, mh.arms);
	}

	private static class MatchHeader {
		long scrutineeValue;
		String arms;
	}

	private static MatchHeader parseMatchHeader(String t) {
		String trimmed = t.trim();
		if (!trimmed.startsWith("match"))
			return null;
		int open = trimmed.indexOf('(');
		if (open < 0)
			return null;
		int[] pe = findTopLevelParenContent(trimmed, open);
		if (pe == null)
			return null;
		int exprStart = pe[0];
		int exprEnd = pe[1];
		int[] be = findTopLevelBraceContent(trimmed, exprEnd + 1);
		if (be == null)
			return null;
		String expr = trimmed.substring(exprStart, exprEnd).trim();
		String arms = trimmed.substring(be[0], be[1]).trim();
		Long val = parseExpressionLong(expr);
		if (val == null)
			return null;
		MatchHeader mh = new MatchHeader();
		mh.scrutineeValue = val.longValue();
		mh.arms = arms;
		return mh;
	}

	private static int[] findTopLevelParenContent(String s, int openIdx) {
		int depth = 0;
		int start = -1;
		for (int i = openIdx; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c == '(') {
				if (depth == 0)
					start = i + 1;
				depth++;
			} else if (c == ')') {
				depth--;
				if (depth == 0)
					return new int[] { start, i };
			}
		}
		return null;
	}

	private static int[] findTopLevelBraceContent(String s, int fromIdx) {
		int open = s.indexOf('{', fromIdx);
		if (open < 0)
			return null;
		int depth = 0;
		int start = -1;
		for (int i = open; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c == '{') {
				if (depth == 0)
					start = i + 1;
				depth++;
			} else if (c == '}') {
				depth--;
				if (depth == 0)
					return new int[] { start, i };
			}
		}
		return null;
	}

	private static Long parseExpressionLong(String expr) {
		try {
			ExpressionParser p = new ExpressionParser(expr);
			long v = p.parseExpression();
			p.skipWhitespace();
			if (!p.isAtEnd())
				return null;
			return Long.valueOf(v);
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}

	private static String evaluateArms(long val, String arms) {
		return matchArmsResult(val, arms);
	}

	private static String matchArmsResult(long val, String arms) {
		String[] armParts = arms.split(";");
		for (String arm : armParts) {
			String a = arm.trim();
			if (a.isEmpty())
				continue;
			int arrow = a.indexOf("=>");
			if (arrow < 0)
				return null;
			String pat = a.substring(0, arrow).trim();
			String body = a.substring(arrow + 2).trim();
			boolean match = false;
			if (pat.equals("_"))
				match = true;
			else {
				try {
					ExpressionParser pp = new ExpressionParser(pat);
					long pv = pp.parseExpression();
					pp.skipWhitespace();
					if (!pp.isAtEnd())
						continue;
					if (pv == val)
						match = true;
				} catch (IllegalArgumentException ex) {
					continue;
				}
			}
			if (match) {
				try {
					return App.interpret(body);
				} catch (InterpretException ie) {
					return null;
				}
			}
		}
		return null;
	}
}
