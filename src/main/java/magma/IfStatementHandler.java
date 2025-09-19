package magma;

public class IfStatementHandler {
	public static int indexOfTopLevelElse(String s) {
		int depth = 0;
		for (int i = 0; i + 4 <= s.length(); i++) {
			char c = s.charAt(i);
			if (c == '(' || c == '{')
				depth++;
			else if (c == ')' || c == '}')
				depth--;
			else if (depth == 0 && s.startsWith("else", i))
				return i;
		}
		return -1;
	}

	public static Long evaluateIfStatement(String stmt, StatementContext ctx) {
		String trimmed = stmt.trim();
		if (!trimmed.startsWith("if"))
			throw new IllegalArgumentException("Invalid if");
		int open = trimmed.indexOf('(');
		if (open < 0)
			throw new IllegalArgumentException("Invalid if cond");
		int depth = 0;
		int condStart = -1, condEnd = -1;
		for (int i = open; i < trimmed.length(); i++) {
			char c = trimmed.charAt(i);
			if (c == '(') {
				if (depth == 0)
					condStart = i + 1;
				depth++;
			} else if (c == ')') {
				depth--;
				if (depth == 0) {
					condEnd = i;
					String rest = trimmed.substring(i + 1).trim();
					int elseIdx = indexOfTopLevelElse(rest);
					String thenPart;
					String elsePart = null;
					if (elseIdx >= 0) {
						thenPart = rest.substring(0, elseIdx).trim();
						elsePart = rest.substring(elseIdx + 4).trim();
					} else {
						thenPart = rest.trim();
					}
					boolean condVal;
					try {
						ExpressionParser cp = new ExpressionParser(trimmed.substring(condStart, condEnd));
						long cv = cp.parseExpression();
						cp.skipWhitespace();
						if (!cp.isAtEnd())
							throw new IllegalArgumentException("Invalid cond");
						condVal = (cv != 0);
					} catch (IllegalArgumentException ex) {
						String bc = trimmed.substring(condStart, condEnd).trim();
						if (bc.equalsIgnoreCase("true"))
							condVal = true;
						else if (bc.equalsIgnoreCase("false"))
							condVal = false;
						else
							throw new IllegalArgumentException("Invalid cond");
					}
					String chosen = condVal ? thenPart : elsePart;
					if (chosen == null)
						return null;
					runStatementsInContext(chosen, ctx);
					return null;
				}
			}
		}
		throw new IllegalArgumentException("Invalid if");
	}

	private static void runStatementsInContext(String stmts, StatementContext ctx) {
		String[] parts = stmts.split(";");
		for (String p : parts) {
			String s = p.trim();
			if (s.isEmpty())
				continue;
			StatementEvaluator.evaluateStatement(s, ctx);
		}
	}
}
