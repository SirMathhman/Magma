package magma;

public class EvaluatorDispatcher {
	public static String run(String t, String input) throws InterpretException {
		java.util.List<java.util.function.Function<String, String>> funcs = new java.util.ArrayList<>();
		funcs.add(s -> {
			try {
				return StatementEvaluator.parseEvalStmts(s);
			} catch (InterpretException e) {
				return null;
			}
		});
		funcs.add(s -> App.tryEvalEquality(s));
		funcs.add(s -> App.tryEvalBooleanLiteral(s));
		funcs.add(s -> tryEvalIfExpression(s));
		funcs.add(s -> App.parseAddEval(s));
		funcs.add(s -> {
			try {
				return App.tryEvalNumericPrefix(s, input);
			} catch (InterpretException e) {
				return null;
			}
		});
		for (java.util.function.Function<String, String> f : funcs) {
			String res = f.apply(t);
			if (res != null)
				return res;
		}
		return null;
	}

		private static String tryEvalIfExpression(String t) {
			String[] parts = findIfParts(t);
			if (parts == null)
				return null;
			String cond = parts[0];
			String thenPart = parts[1];
			String elsePart = parts[2];
			boolean condVal;
			try {
				ExpressionParser cp = new ExpressionParser(cond);
				long cv = cp.parseExpression();
				cp.skipWhitespace();
				if (!cp.isAtEnd())
					return null;
				condVal = (cv != 0);
			} catch (IllegalArgumentException ex) {
				if (cond.equalsIgnoreCase("true"))
					condVal = true;
				else if (cond.equalsIgnoreCase("false"))
					condVal = false;
				else
					return null;
			}
			String chosen = condVal ? thenPart : elsePart;
			try {
				return App.interpret(chosen);
			} catch (InterpretException ex) {
				return null;
			}
		}

		private static String[] findIfParts(String t) {
			String trimmed = t.trim();
			if (!trimmed.startsWith("if"))
				return null;
			if (trimmed.length() > 2) {
				char c = trimmed.charAt(2);
				if (!Character.isWhitespace(c) && c != '(')
					return null;
			}
			int condOpen = trimmed.indexOf('(');
			if (condOpen < 0)
				return null;
			int depth = 0;
			int condStart = -1, condEnd = -1;
			for (int i = condOpen; i < trimmed.length(); i++) {
				char ch = trimmed.charAt(i);
				if (ch == '(') {
					if (depth == 0)
						condStart = i + 1;
					depth++;
				} else if (ch == ')') {
					depth--;
					if (depth == 0) {
						condEnd = i;
						String rest = trimmed.substring(i + 1).trim();
						int elseIdx = indexOfTopLevelElse(rest);
						if (elseIdx < 0)
							return null;
						String thenPart = rest.substring(0, elseIdx).trim();
						String elsePart = rest.substring(elseIdx + 4).trim();
						return new String[] { trimmed.substring(condStart, condEnd).trim(), thenPart, elsePart };
					}
				}
			}
			return null;
		}

		private static int indexOfTopLevelElse(String s) {
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
}
