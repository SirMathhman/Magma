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

		// quick accept boolean literal inputs
		if (normalized.equals("true") || normalized.equals("false")) {
			return new Result.Ok<>(normalized);
		}

		// quick single-expression comparison support (e.g., "3 < 5")
		if (!normalized.contains(";") && normalized.contains("<")) {
			int lt = normalized.indexOf('<');
			String left = normalized.substring(0, lt).trim();
			String right = normalized.substring(lt + 1).trim();
			java.util.Optional<Integer> a = evalExpr(left, java.util.Collections.emptyMap());
			java.util.Optional<Integer> b = evalExpr(right, java.util.Collections.emptyMap());
			if (a.isEmpty() || b.isEmpty())
				return new Result.Err<>(new InterpreterError("invalid input", normalized, java.util.List.of()));
			return new Result.Ok<>(a.get() < b.get() ? "true" : "false");
		}

		java.util.Map<String, Integer> env = new java.util.HashMap<>();
		// track mutability: true => mutable
		java.util.Map<String, Boolean> mutable = new java.util.HashMap<>();
		String[] stmts = normalized.split(";");
		java.util.Optional<Integer> lastValue = java.util.Optional.empty();

		for (String rawStmt : stmts) {
			String stmt = rawStmt.trim();
			if (stmt.isEmpty())
				continue;

			// let <ident> = <expr> or let mut <ident> = <expr>
			if (stmt.startsWith("let ")) {
				String rest = stmt.substring(4).trim();
				boolean isMut = false;
				if (rest.startsWith("mut ")) {
					isMut = true;
					rest = rest.substring(4).trim();
				}
				int eq = rest.indexOf('=');
				if (eq <= 0) {
					return new Result.Err<>(new InterpreterError("invalid let statement", stmt, java.util.List.of()));
				}
				java.util.Optional<java.util.Map.Entry<String, Integer>> entryOpt = parseAndEval(rest, env);
				if (entryOpt.isEmpty())
					return new Result.Err<>(new InterpreterError("invalid expression in let", rest, java.util.List.of()));
				java.util.Map.Entry<String, Integer> entry = entryOpt.get();
				String ident = entry.getKey();
				Integer v = entry.getValue();
				env.put(ident, v);
				mutable.put(ident, isMut);
				lastValue = java.util.Optional.of(v);
				continue;
			}

			// assignment: <ident> = <expr>
			// compound assignment: += (e.g., x += 10)
			int plusEq = stmt.indexOf("+=");
			if (plusEq > 0 && !stmt.startsWith("let ")) {
				String lhs = stmt.substring(0, plusEq).trim();
				String rhs = stmt.substring(plusEq + 2).trim();
				if (lhs.isEmpty() || rhs.isEmpty()) {
					return new Result.Err<>(new InterpreterError("invalid assignment", stmt, java.util.List.of()));
				}
				if (!mutable.getOrDefault(lhs, false)) {
					return new Result.Err<>(new InterpreterError("assignment to immutable variable", lhs, java.util.List.of()));
				}
				java.util.Optional<Integer> rOpt = evalExpr(rhs, env);
				if (rOpt.isEmpty())
					return new Result.Err<>(new InterpreterError("invalid expression in assignment", rhs, java.util.List.of()));
				Integer add = rOpt.get();
				Integer base = env.getOrDefault(lhs, 0);
				Integer nv = base + add;
				env.put(lhs, nv);
				lastValue = java.util.Optional.of(nv);
				continue;
			}

			// assignment: <ident> = <expr>
			int assignIdx = stmt.indexOf('=');
			if (assignIdx > 0 && !stmt.startsWith("let ")) {
				java.util.Optional<java.util.Map.Entry<String, Integer>> entryOpt = parseAndEval(stmt, env);
				if (entryOpt.isEmpty())
					return new Result.Err<>(new InterpreterError("invalid assignment", stmt, java.util.List.of()));
				java.util.Map.Entry<String, Integer> entry = entryOpt.get();
				String lhs = entry.getKey();
				Integer v = entry.getValue();
				if (!mutable.getOrDefault(lhs, false)) {
					return new Result.Err<>(new InterpreterError("assignment to immutable variable", lhs, java.util.List.of()));
				}
				env.put(lhs, v);
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

		// if-expression: if (<cond>) <then> else <else>
		if (expr.startsWith("if ")) {
			String rest = expr.substring(3).trim();
			// expect parentheses around condition or a bare condition
			String cond;
			if (rest.startsWith("(")) {
				int close = rest.indexOf(')');
				if (close < 0)
					return java.util.Optional.empty();
				cond = rest.substring(1, close).trim();
				rest = rest.substring(close + 1).trim();
			} else {
				int sp = rest.indexOf(' ');
				if (sp < 0)
					return java.util.Optional.empty();
				cond = rest.substring(0, sp).trim();
				rest = rest.substring(sp + 1).trim();
			}
			// find 'else' separator
			int elseIdx = rest.indexOf(" else ");
			if (elseIdx < 0)
				return java.util.Optional.empty();
			String thenPart = rest.substring(0, elseIdx).trim();
			String elsePart = rest.substring(elseIdx + 6).trim();
			// evaluate condition: true/false or integer non-zero
			boolean condTrue = false;
			if (cond.equals("true"))
				condTrue = true;
			else if (cond.equals("false"))
				condTrue = false;
			else {
				java.util.Optional<Integer> cval = evalExpr(cond, env);
				if (cval.isEmpty())
					return java.util.Optional.empty();
				condTrue = cval.get() != 0;
			}
			String chosen = condTrue ? thenPart : elsePart;
			return evalExpr(chosen, env);
		}

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

	// validate identifier not empty and evaluate expression; returns Optional with
	// value or empty on failure
	private java.util.Optional<Integer> validateAndEval(String ident, String expr, java.util.Map<String, Integer> env) {
		if (java.util.Objects.isNull(ident) || ident.trim().isEmpty()) {
			return java.util.Optional.empty();
		}
		expr = expr.trim();
		if (expr.isEmpty())
			return java.util.Optional.empty();
		return evalExpr(expr, env);
	}

	// parse strings like "ident = expr" (rest is already trimmed); returns
	// Optional<[ident, expr]> empty on failure
	private java.util.Optional<String[]> parseIdentExpr(String rest) {
		int eq = rest.indexOf('=');
		if (eq <= 0)
			return java.util.Optional.empty();
		String ident = rest.substring(0, eq).trim();
		String expr = rest.substring(eq + 1).trim();
		if (ident.isEmpty() || expr.isEmpty())
			return java.util.Optional.empty();
		return java.util.Optional.of(new String[] { ident, expr });
	}

	// parse and evaluate an "ident = expr" fragment; returns Optional of
	// Map.Entry(ident,value)
	private java.util.Optional<java.util.Map.Entry<String, Integer>> parseAndEval(String rest,
			java.util.Map<String, Integer> env) {
		java.util.Optional<String[]> pairOpt = parseIdentExpr(rest);
		if (pairOpt.isEmpty())
			return java.util.Optional.empty();
		String[] pair = pairOpt.get();
		String ident = pair[0];
		String expr = pair[1];
		java.util.Optional<Integer> vOpt = validateAndEval(ident, expr, env);
		if (vOpt.isEmpty())
			return java.util.Optional.empty();
		Integer v = vOpt.get();
		return java.util.Optional.of(new java.util.AbstractMap.SimpleEntry<>(ident, v));
	}
}
