package magma;

import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

import java.util.HashMap;
import java.util.Map;

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
		String lastValue = null;
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
				if (!rest.contains("=")) {
					return errUndefined(input);
				}
				String[] kv = rest.split("=", 2);
				String name = kv[0].trim();
				String expr = kv[1].trim();
				Option<String> value = evalAndPut(name, expr, env);
				if (value instanceof Some(var v1)) {
					mutable.put(name, isMut);
					lastValue = v1;
				} else {
					return errUndefined(input);
				}
				continue;
			}

			// assignment: name = expr
			if (stmt.contains("=")) {
				String[] kv = stmt.split("=", 2);
				String name = kv[0].trim();
				String expr = kv[1].trim();
				if (!env.containsKey(name)) {
					return errUndefined(input);
				}
				if (!Boolean.TRUE.equals(mutable.get(name))) {
					return new Err<>(new InterpretError("Immutable assignment", input));
				}
				Option<String> value = evalAndPut(name, expr, env);
				if (value instanceof Some(var v2)) {
					lastValue = v2;
				} else {
					return errUndefined(input);
				}
				continue;
			}

			// expression: either integer literal or variable
			Option<String> opt = evalExpr(stmt, env);
			if (opt instanceof Some(var v3)) {
				lastValue = v3;
			} else {
				return errUndefined(input);
			}
		}

		if (lastValue != null) {
			return new Ok<>(lastValue);
		}

		return errUndefined(input);
	}

	private static Option<String> evalExpr(String expr, Map<String, String> env) {
		String t = expr == null ? "" : expr.trim();

		// if-expression: if (cond) thenExpr else elseExpr
		if (t.startsWith("if")) {
			int open = t.indexOf('(');
			if (open == -1) return None.instance();
			// find matching closing parenthesis
			int depth = 1;
			int close = -1;
			for (int i = open + 1; i < t.length(); i++) {
				char c = t.charAt(i);
				if (c == '(') depth++;
				else if (c == ')') {
					depth--;
					if (depth == 0) { close = i; break; }
				}
			}
			if (close == -1) return None.instance();
			String cond = t.substring(open + 1, close).trim();
			int afterClose = close + 1;
			int elseIdx = t.indexOf("else", afterClose);
			if (elseIdx == -1) return None.instance();
			String thenPart = t.substring(afterClose, elseIdx).trim();
			String elsePart = t.substring(elseIdx + 4).trim();
			Option<String> condVal = evalExpr(cond, env);
			if (condVal instanceof Some(var cv)) {
				if (!isBoolean(cv)) return None.instance();
				if ("true".equals(cv)) return evalExpr(thenPart, env);
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
