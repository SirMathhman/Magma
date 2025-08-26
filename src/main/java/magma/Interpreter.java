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

		String[] parts = trimmed.split(";");
		AtomicReference<String> lastValue = new AtomicReference<>(null);
		for (String raw : parts) {
			String stmt = raw.trim();
			if (stmt.isEmpty())
				continue;

			// let declaration: let [mut] name = expr
			if (stmt.startsWith("let ")) {
				String rest = stmt.substring(4).trim();
				boolean isMut = false;
				if (rest.startsWith("mut ")) {
					isMut = true;
					rest = rest.substring(4).trim();
				}
				String[] ne = splitNameExpr(rest);
				if (ne == null) {
					return errUndefined(input);
				}
				String name = ne[0];
				String expr = ne[1];
				Option<String> value = evalAndPut(name, expr, env);
				Result<String, InterpretError> r1 = optionToResult(value, input);
				Result<String, InterpretError> setErr1 = setLastFromResultOrErr(r1, lastValue);
				if (setErr1 != null)
					return setErr1;
				mutable.put(name, isMut);
				continue;
			}

			// assignment: name = expr
			if (stmt.contains("=")) {
				String[] ne = splitNameExpr(stmt);
				if (ne == null)
					return errUndefined(input);
				String name = ne[0];
				String expr = ne[1];
				Result<String, InterpretError> checkRes = ensureExistsAndMutableOrErr(name, env, mutable, input);
				if (checkRes != null)
					return checkRes;
				Option<String> value = evalAndPut(name, expr, env);
				Result<String, InterpretError> r2 = optionToResult(value, input);
				Result<String, InterpretError> setErr2 = setLastFromResultOrErr(r2, lastValue);
				if (setErr2 != null)
					return setErr2;
				continue;
			}

			// post-increment: name++
			if (stmt.endsWith("++")) {
				String name = stmt.substring(0, stmt.length() - 2).trim();
				Result<String, InterpretError> checkRes = ensureExistsAndMutableOrErr(name, env, mutable, input);
				if (checkRes != null)
					return checkRes;
				String cur = env.get(name);
				if (!isInteger(cur)) {
					return errUndefined(input);
				}
				try {
					int v = Integer.parseInt(cur);
					int nv = v + 1;
					env.put(name, Integer.toString(nv));
					lastValue.set(Integer.toString(nv));
				} catch (NumberFormatException ex) {
					return errUndefined(input);
				}
				continue;
			}

			// expression: either integer literal or variable
			Option<String> opt = evalExpr(stmt, env);
			Result<String, InterpretError> r3 = optionToResult(opt, input);
			Result<String, InterpretError> setErr3 = setLastFromResultOrErr(r3, lastValue);
			if (setErr3 != null)
				return setErr3;
		}

		if (lastValue.get() != null) {
			return new Ok<>(lastValue.get());
		}

		return errUndefined(input);
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

	private static Result<String, InterpretError> optionToResult(Option<String> opt, String input) {
		if (opt instanceof Some(var v)) {
			return new Ok<>(v);
		}
		return errUndefined(input);
	}

	private static Result<String, InterpretError> setLastFromResultOrErr(Result<String, InterpretError> r,
			AtomicReference<String> last) {
		if (r instanceof Ok(var v)) {
			last.set(v);
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

		if (isInteger(t)) {
			return new Some<>(expr);
		}

		if (isBoolean(t)) {
			return new Some<>(t);
		}

		String v = env.get(t);
		if (v != null)
			return new Some<>(v);
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

	private static Result<String, InterpretError> errUndefined(String input) {
		return new Err<>(new InterpretError("Undefined value", input));
	}
}
