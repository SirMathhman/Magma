package magma;

import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class Interpreter {
	public static Result<String, InterpretError> interpret(String input) {
		String trimmed = input == null ? "" : input.trim();

		// strip matching outer braces, e.g. "{5}" -> "5"
		while (trimmed.length() >= 2 && trimmed.charAt(0) == '{' && trimmed.charAt(trimmed.length() - 1) == '}') {
			trimmed = trimmed.substring(1, trimmed.length() - 1).trim();
		}

		// quick path: single integer literal
		if (isInteger(trimmed)) {
			return new Ok<>(trimmed);
		}

		// quick path: boolean literal
		if (isBoolean(trimmed)) {
			return new Ok<>(trimmed);
		}

		Map<String, String> env = new HashMap<>();
		Map<String, Boolean> mutable = new HashMap<>();

		String[] parts = splitTopLevelStatements(trimmed);
		AtomicReference<String> lastValue = new AtomicReference<>(null);
		for (String raw : parts) {
			String stmt = raw.trim();
			if (stmt.isEmpty())
				continue;

			// let declaration: let [mut] name = expr
			if (stmt.startsWith("let ")) {
				Result<String, InterpretError> r = handleLetDeclaration(stmt.substring(4).trim(), env, mutable, lastValue,
						input);
				if (r != null)
					return r;
				continue;
			}

			// while loop: while (cond) body (handle first to avoid confusing other checks)
			if (stmt.startsWith("while")) {
				int open = stmt.indexOf('(');
				if (open == -1)
					return err("Malformed while", input);
				int close = findMatchingParen(stmt, open);
				if (close == -1)
					return err("Malformed while", input);
				String cond = stmt.substring(open + 1, close).trim();
				String body = stmt.substring(close + 1).trim();
				if (body.isEmpty()) {
					// no-op body
					continue;
				}
				Result<String, InterpretError> r = executeConditionalLoop(cond, body, env, mutable, lastValue, input,
						"Invalid while condition", "Invalid assignment in while body", "Invalid expression in while body",
						null);
				if (r != null)
					return r;
				continue;
			}

			// for loop: for(init; cond; incr) body
			if (stmt.startsWith("for")) {
				int open = stmt.indexOf('(');
				if (open == -1)
					return err("Malformed for", input);
				int close = findMatchingParen(stmt, open);
				if (close == -1)
					return err("Malformed for", input);
				String header = stmt.substring(open + 1, close).trim();
				// split header into three parts by top-level semicolons
				int depth = 0;
				int firstSemi = -1;
				int secondSemi = -1;
				for (int i = 0; i < header.length(); i++) {
					char c = header.charAt(i);
					if (c == '(')
						depth++;
					else if (c == ')')
						depth--;
					else if (c == ';' && depth == 0) {
						if (firstSemi == -1)
							firstSemi = i;
						else {
							secondSemi = i;
							break;
						}
					}
				}
				if (firstSemi == -1 || secondSemi == -1)
					return err("Malformed for header", input);
				String init = header.substring(0, firstSemi).trim();
				String cond = header.substring(firstSemi + 1, secondSemi).trim();
				String incr = header.substring(secondSemi + 1).trim();
				String body = stmt.substring(close + 1).trim();
				// execute init
				if (!init.isEmpty()) {
					if (init.startsWith("let ")) {
						String rest = init.substring(4).trim();
						Result<String, InterpretError> r = handleLetDeclaration(rest, env, mutable, lastValue, input);
						if (r != null)
							return r;
					} else if (!init.isEmpty()) {
						Result<String, InterpretError> rInit = executeSimpleOrExpression(init, env, mutable, lastValue, input,
								false,
								"Invalid init in for", "Invalid init in for");
						if (rInit != null)
							return rInit;
					}
				}
				// empty body -> continue
				if (body.isEmpty())
					continue;
				// loop
				java.util.function.Supplier<Result<String, InterpretError>> incrSupplier = null;
				if (!incr.isEmpty()) {
					incrSupplier = () -> executeSimpleOrExpression(incr, env, mutable, lastValue, input, true,
							"Invalid incr in for", "Invalid incr in for");
				}
				Result<String, InterpretError> rLoop = executeConditionalLoop(cond, body, env, mutable, lastValue, input,
						"Invalid for condition", "Invalid assignment in for body", "Invalid expression in for body", incrSupplier);
				if (rLoop != null)
					return rLoop;
				continue;
			}

			// function declaration: fn name() => expr
			if (stmt.startsWith("fn ")) {
				String rest = stmt.substring(3).trim();
				int open = rest.indexOf('(');
				if (open == -1)
					return err("Malformed function", input);
				int close = rest.indexOf(')', open);
				if (close == -1)
					return err("Malformed function", input);
				String name = rest.substring(0, open).trim();
				int arrow = rest.indexOf("=>", close);
				if (arrow == -1)
					return err("Malformed function", input);
				String body = rest.substring(arrow + 2).trim();
				if (name.isEmpty() || body.isEmpty())
					return err("Malformed function", input);
				// store function body with a special prefix in env
				env.put(name, "fn:" + body);
				System.out.println("[DEBUG] define fn " + name + " -> " + body);
				continue;
			}

			// for any non-let, non-while statement delegate to helper to handle assignment,
			// post-increment, or expression
			Result<String, InterpretError> rMain = executeSimpleOrExpression(stmt, env, mutable, lastValue, input, false,
					"Invalid assignment expression for " + (splitNameExpr(stmt) == null ? "" : splitNameExpr(stmt)[0]),
					"Undefined expression: " + stmt);
			if (rMain != null)
				return rMain;
			continue;
		}

		if (lastValue.get() != null) {
			return new Ok<>(lastValue.get());
		}

		return err("Undefined value", input);
	}

	private static String[] splitTopLevelStatements(String src) {
		if (src == null || src.isEmpty())
			return new String[0];
		java.util.List<String> parts = new java.util.ArrayList<>();
		int depthParen = 0;
		int depthBrace = 0;
		int last = 0;
		for (int i = 0; i < src.length(); i++) {
			char c = src.charAt(i);
			if (c == '(')
				depthParen++;
			else if (c == ')')
				depthParen--;
			else if (c == '{')
				depthBrace++;
			else if (c == '}')
				depthBrace--;
			else if (c == ';' && depthParen == 0 && depthBrace == 0) {
				parts.add(src.substring(last, i));
				last = i + 1;
			}
		}
		if (last <= src.length())
			parts.add(src.substring(last));
		return parts.stream().map(String::trim).toArray(String[]::new);
	}

	private static int findMatchingParen(String s, int openIndex) {
		int depth = 1;
		for (int i = openIndex + 1; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c == '(')
				depth++;
			else if (c == ')') {
				depth--;
				if (depth == 0)
					return i;
			}
		}
		return -1;
	}

	private static Result<String, InterpretError> handleLetDeclaration(String rest, Map<String, String> env,
			Map<String, Boolean> mutable, AtomicReference<String> lastValue, String input) {
		boolean isMut = false;
		if (rest.startsWith("mut ")) {
			isMut = true;
			rest = rest.substring(4).trim();
		}
		String[] ne = splitNameExpr(rest);
		if (ne == null) {
			return err("Missing '=' in let declaration", input);
		}
		String name = ne[0];
		String expr = ne[1];
		Option<String> value = evalAndPut(name, expr, env);
		Result<String, InterpretError> r1 = optionToResult(value, input, "Invalid initializer for " + name);
		Result<String, InterpretError> setErr1 = setLastFromResultOrErr(r1, lastValue);
		if (setErr1 != null)
			return setErr1;
		mutable.put(name, isMut);
		return null;
	}

	private static Result<String, InterpretError> executeConditionalLoop(String cond, String body,
			Map<String, String> env, Map<String, Boolean> mutable, AtomicReference<String> lastValue, String input,
			String condErrMsg, String assignErrMsg, String exprErrMsg,
			java.util.function.Supplier<Result<String, InterpretError>> optionalIncr) {
		while (true) {
			Option<String> condVal = evalExpr(cond, env);
			if (!(condVal instanceof Some(var cv)) || !isBoolean(cv)) {
				return err(condErrMsg, input);
			}
			if ("false".equals(cv))
				break;
			Result<String, InterpretError> rBody = executeSimpleOrExpression(body, env, mutable, lastValue, input, true,
					assignErrMsg, exprErrMsg);
			if (rBody != null)
				return rBody;
			if (optionalIncr != null) {
				Result<String, InterpretError> rIncr = optionalIncr.get();
				if (rIncr != null)
					return rIncr;
			}
		}
		return null;
	}

	private static Result<String, InterpretError> performAssignmentInEnv(String stmt, Map<String, String> env,
			Map<String, Boolean> mutable,
			AtomicReference<String> lastValue, String input, String contextMessage) {
		// simple '=' assignment
		String[] ne = splitNameExpr(stmt);
		if (ne == null)
			return err("Missing '=' in assignment", input);
		String name = ne[0];
		String expr = ne[1];
		Result<String, InterpretError> checkRes = ensureExistsAndMutableOrErr(name, env, mutable, input);
		if (checkRes != null)
			return checkRes;
		Option<String> value = evalAndPut(name, expr, env);
		Result<String, InterpretError> r = optionToResult(value, input, contextMessage);
		return setLastFromResultOrErr(r, lastValue);

	}

	private static Result<String, InterpretError> executeSimpleOrExpression(String stmt, Map<String, String> env,
			Map<String, Boolean> mutable, AtomicReference<String> lastValue, String input, boolean allowIncrementInPlace,
			String assignmentContextMessage, String exprContextMessage) {
		// handle '+=' or '=' assignments, post-increment, or plain expressions
		// handle compound and simple assignments
		int plusEq = stmt.indexOf("+=");
		if (plusEq != -1) {
			// reuse logic from performAssignmentInEnv for '+='
			String name = stmt.substring(0, plusEq).trim();
			String expr = stmt.substring(plusEq + 2).trim();
			Result<Integer, InterpretError> curRes = getIntegerVarOrErr(name, env, mutable, input);
			if (curRes instanceof Err(var e))
				return new Err<>(e);
			int a = ((Ok<Integer, InterpretError>) curRes).value();
			Option<String> valueOpt = evalExpr(expr, env);
			if (!(valueOpt instanceof Some(var v))) {
				return err(assignmentContextMessage, input);
			}
			String addend = v;
			if (!isInteger(addend))
				return err("Right-hand side of '+=' is not an integer", input);
			try {
				int b = Integer.parseInt(addend);
				int sum = a + b;
				env.put(name, Integer.toString(sum));
				return setLastFromResultOrErr(new Ok<>(Integer.toString(sum)), lastValue);
			} catch (NumberFormatException ex) {
				return err("Invalid integer during '+='", input);
			}
		}

		// simple '=' assignment
		if (stmt.contains("=")) {
			// avoid treating '+=' here since handled above
			Result<String, InterpretError> r = performAssignmentInEnv(stmt, env, mutable, lastValue, input,
					assignmentContextMessage);
			return r;
		}

		// post-increment
		if (stmt.endsWith("++")) {
			String name = stmt.substring(0, stmt.length() - 2).trim();
			Result<String, InterpretError> r = performIncrement(name, env, mutable, lastValue, input, allowIncrementInPlace);
			return r;
		}

		// zero-arg function call (e.g., name())
		if (stmt.endsWith("()")) {
			String name = stmt.substring(0, stmt.length() - 2).trim();
			Option<String> res = evalZeroArgFunction(name, env);
			Result<String, InterpretError> rr = optionToResult(res, input, exprContextMessage);
			return setLastFromResultOrErr(rr, lastValue);
		}

		// expression
		return performExpressionAndSetLast(stmt, env, lastValue, input, exprContextMessage);
	}

	private static Result<String, InterpretError> performIncrement(String name, Map<String, String> env,
			Map<String, Boolean> mutable,
			AtomicReference<String> lastValue, String input, boolean inPlace) {
		Result<Integer, InterpretError> r = getIntegerVarOrErr(name, env, mutable, input);
		if (r instanceof Err(var err))
			return new Err<>(err);
		int v = ((Ok<Integer, InterpretError>) r).value();
		int nv = v + 1;
		env.put(name, Integer.toString(nv));
		lastValue.set(Integer.toString(nv));
		return null;
	}

	private static Result<Integer, InterpretError> getIntegerVarOrErr(String name, Map<String, String> env,
			Map<String, Boolean> mutable, String input) {
		Result<String, InterpretError> check = ensureExistsAndMutableOrErr(name, env, mutable, input);
		if (check != null) {
			if (check instanceof Err(var e))
				return new Err<>(e);
		}
		String cur = env.get(name);
		if (!isInteger(cur))
			return new Err<>(new InterpretError("Cannot use non-integer variable '" + name + "'", input));
		try {
			int v = Integer.parseInt(cur);
			return new Ok<>(v);
		} catch (NumberFormatException ex) {
			return new Err<>(new InterpretError("Invalid integer value for variable '" + name + "'", input));
		}
	}

	private static Result<String, InterpretError> performExpressionAndSetLast(String expr, Map<String, String> env,
			AtomicReference<String> lastValue, String input, String contextMessage) {
		Option<String> opt = evalExpr(expr, env);
		Result<String, InterpretError> r = optionToResult(opt, input, contextMessage);
		return setLastFromResultOrErr(r, lastValue);
	}

	private static String[] splitNameExpr(String stmt) {
		if (stmt == null)
			return null;
		int idx = stmt.indexOf('=');
		if (idx == -1)
			return null;
		String name = stmt.substring(0, idx).trim();
		String expr = stmt.substring(idx + 1).trim();
		return new String[] { name, expr };
	}

	private static Result<String, InterpretError> optionToResult(Option<String> opt, String input,
			String contextMessage) {
		if (opt instanceof Some(var v)) {
			return new Ok<>(v);
		}
		return err(contextMessage, input);
	}

	private static Option<String> evalZeroArgFunction(String name, Map<String, String> env) {
		String fv = env.get(name);
		System.out.println("[DEBUG] call fn " + name + " -> " + fv);
		if (fv != null && fv.startsWith("fn:")) {
			String body = fv.substring(3);
			return evalExpr(body, env);
		}
		return None.instance();
	}

	private static Result<String, InterpretError> err(String message, String input) {
		return new Err<>(new InterpretError(message, input));
	}

	private static Result<String, InterpretError> setLastFromResultOrErr(Result<String, InterpretError> r,
			AtomicReference<String> last) {
		if (r instanceof Ok(var v)) {
			last.set(v);
			System.out.println("[DEBUG] set last -> " + v);
			return null;
		}
		return r;
	}

	private static Result<String, InterpretError> ensureExistsAndMutableOrErr(String name, Map<String, String> env,
			Map<String, Boolean> mutable, String input) {
		if (!env.containsKey(name)) {
			return new Err<>(new InterpretError("Undefined value", input));
		}
		if (!Boolean.TRUE.equals(mutable.get(name))) {
			return new Err<>(new InterpretError("Immutable assignment", input));
		}
		return null;
	}

	private static Option<String> evalExpr(String expr, Map<String, String> env) {
		String t = expr == null ? "" : expr.trim();

		// if-expression: if (cond) thenExpr else elseExpr
		if (t.startsWith("if")) {
			int open = t.indexOf('(');
			if (open == -1)
				return None.instance();
			// find matching closing parenthesis
			int depth = 1;
			int close = -1;
			for (int i = open + 1; i < t.length(); i++) {
				char c = t.charAt(i);
				if (c == '(')
					depth++;
				else if (c == ')') {
					depth--;
					if (depth == 0) {
						close = i;
						break;
					}
				}
			}
			if (close == -1)
				return None.instance();
			String cond = t.substring(open + 1, close).trim();
			int afterClose = close + 1;
			int elseIdx = t.indexOf("else", afterClose);
			if (elseIdx == -1)
				return None.instance();
			String thenPart = t.substring(afterClose, elseIdx).trim();
			String elsePart = t.substring(elseIdx + 4).trim();
			Option<String> condVal = evalExpr(cond, env);
			if (condVal instanceof Some(var cv)) {
				if (!isBoolean(cv))
					return None.instance();
				if ("true".equals(cv))
					return evalExpr(thenPart, env);
				return evalExpr(elsePart, env);
			}
			return None.instance();
		}

		// binary less-than: a < b
		int lt = t.indexOf('<');
		if (lt != -1) {
			String left = t.substring(0, lt).trim();
			String right = t.substring(lt + 1).trim();
			Option<String> lopt = evalExpr(left, env);
			Option<String> ropt = evalExpr(right, env);
			if (lopt instanceof Some(var lv) && ropt instanceof Some(var rv)) {
				if (!isInteger(lv) || !isInteger(rv))
					return None.instance();
				try {
					int li = Integer.parseInt(lv);
					int ri = Integer.parseInt(rv);
					return new Some<>(li < ri ? "true" : "false");
				} catch (NumberFormatException ex) {
					return None.instance();
				}
			}
			return None.instance();
		}

		if (isInteger(t)) {
			return new Some<>(expr);
		}

		if (isBoolean(t)) {
			return new Some<>(t);
		}

		// zero-arg function call: name()
		if (t.endsWith("()")) {
			String name = t.substring(0, t.length() - 2).trim();
			return evalZeroArgFunction(name, env);
		}

		String v = env.get(t);
		if (v != null) {
			// function stored as special env entry 'fn:BODY'
			if (v.startsWith("fn:")) {
				String body = v.substring(3);
				Option<String> res = evalExpr(body, env);
				return res;
			}
			return new Some<>(v);
		}
		return None.instance();
	}

	private static Option<String> evalAndPut(String name, String expr, Map<String, String> env) {
		Option<String> opt = evalExpr(expr, env);
		if (opt instanceof Some(var optValue)) {
			env.put(name, optValue);
			return opt;
		}
		return None.instance();
	}

	private static boolean isInteger(String s) {
		if (s == null || s.isEmpty())
			return false;
		try {
			Integer.parseInt(s);
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}

	private static boolean isBoolean(String s) {
		return "true".equals(s) || "false".equals(s);
	}
}
