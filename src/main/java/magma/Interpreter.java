package magma;

import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

import java.util.HashMap;
import java.util.Map;

public class Interpreter {
	public static Result<String, InterpretError> interpret(String input) {
		String trimmed = input == null ? "" : input.trim();

		// quick path: single integer literal
		if (isInteger(trimmed)) {
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
				String value = evalAndPut(name, expr, env);
				if (value == null) {
					return errUndefined(input);
				}
				mutable.put(name, isMut);
				lastValue = value;
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
				String value = evalAndPut(name, expr, env);
				if (value == null) {
					return errUndefined(input);
				}
				lastValue = value;
				continue;
			}

			// expression: either integer literal or variable
			String value = evalExpr(stmt, env);
			if (value == null) {
				return errUndefined(input);
			}
			lastValue = value;
		}

		if (lastValue != null) {
			return new Ok<>(lastValue);
		}

		return errUndefined(input);
	}

	private static String evalExpr(String expr, Map<String, String> env) {
		if (isInteger(expr)) {
			return expr;
		}
		return env.getOrDefault(expr, null);
	}

	private static String evalAndPut(String name, String expr, Map<String, String> env) {
		String value = evalExpr(expr, env);
		if (value != null) {
			env.put(name, value);
		}
		return value;
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

	private static Result<String, InterpretError> errUndefined(String input) {
		return new Err<>(new InterpretError("Undefined value", input));
	}
}
