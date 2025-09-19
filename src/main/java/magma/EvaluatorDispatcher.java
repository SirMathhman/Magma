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
}
