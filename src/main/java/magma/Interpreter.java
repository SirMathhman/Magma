package magma;

/**
 * Simple interpreter with a tiny expression evaluator.
 * Supports integer literals, simple addition, and basic let-bindings
 * within a single interpret call (e.g. "let x = 10; x").
 */
public class Interpreter {

	public Result<String, InterpreterError> interpret(String input) {
		String normalized = java.util.Optional.ofNullable(input).orElse("").trim();
		if (normalized.isEmpty()) {
			return new Result.Ok<>("");
		}

		java.util.Map<String, Integer> env = new java.util.HashMap<>();
		String[] stmts = normalized.split(";");
		java.util.Optional<Integer> lastValue = java.util.Optional.empty();

		for (String rawStmt : stmts) {
			String stmt = rawStmt.trim();
			if (stmt.isEmpty())
				continue;

			// let <ident> = <expr>
			if (stmt.startsWith("let ")) {
				String rest = stmt.substring(4).trim();
				int eq = rest.indexOf('=');
				if (eq <= 0) {
					return new Result.Err<>(new InterpreterError("invalid let statement", stmt, java.util.List.of()));
				}
				String ident = rest.substring(0, eq).trim();
				String expr = rest.substring(eq + 1).trim();
				if (ident.isEmpty()) {
					return new Result.Err<>(new InterpreterError("invalid identifier in let", stmt, java.util.List.of()));
				}
				java.util.Optional<Integer> vOpt = evalExpr(expr, env);
				if (vOpt.isEmpty()) {
					return new Result.Err<>(new InterpreterError("invalid expression in let", expr, java.util.List.of()));
				}
				Integer v = vOpt.get();
				env.put(ident, v);
				lastValue = java.util.Optional.of(v);
				continue;
			}

			java.util.Optional<Integer> vOpt = evalExpr(stmt, env);
			if (vOpt.isEmpty()) {
				return new Result.Err<>(new InterpreterError("invalid input", stmt, java.util.List.of()));
			}
			lastValue = java.util.Optional.of(vOpt.get());
		}

		if (lastValue.isPresent()) {
			return new Result.Ok<>(String.valueOf(lastValue.get()));
		}

		return new Result.Err<>(new InterpreterError("invalid input", normalized, java.util.List.of()));
	}

	private java.util.Optional<Integer> evalExpr(String expr, java.util.Map<String, Integer> env) {
		// caller guarantees expr is provided (not empty)
		expr = expr.trim();
		if (expr.isEmpty())
			return java.util.Optional.empty();

		// variable lookup
		if (env.containsKey(expr))
			return java.util.Optional.of(env.get(expr));

		// integer literal
		try {
			return java.util.Optional.of(Integer.parseInt(expr));
		} catch (NumberFormatException e) {
			// fallthrough
		}

		// simple addition a + b (split on first '+')
		int plus = expr.indexOf('+');
		if (plus > 0) {
			String left = expr.substring(0, plus).trim();
			String right = expr.substring(plus + 1).trim();
			java.util.Optional<Integer> a = evalExpr(left, env);
			java.util.Optional<Integer> b = evalExpr(right, env);
			if (a.isPresent() && b.isPresent())
				return java.util.Optional.of(a.get() + b.get());
		}

		return java.util.Optional.empty();
	}
}
