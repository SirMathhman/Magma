package magma;

public class Interpreter {
	private java.util.Map<String, java.util.List<String>> structDefs;

	public String interpret(String input) throws InterpretException {
		if (input == null || input.isEmpty()) {
			return "";
		}
		input = input.trim();
		java.util.Map<String, Integer> vars = new java.util.HashMap<>();
		java.util.Map<String, String> functions = new java.util.HashMap<>();
		java.util.Map<String, java.util.List<String>> structDefs = new java.util.HashMap<>();
		this.structDefs = structDefs;
		return interpret(input, vars, functions, structDefs);
	}

	private String handlePointerDeref(String stmt, java.util.Map<String, Integer> vars) throws InterpretException {
		stmt = stmt.trim();
		if (!stmt.startsWith("*&"))
			return null;
		String varName = stmt.substring(2).trim();
		if (vars.containsKey(varName))
			return String.valueOf(vars.get(varName));
		throw new InterpretException("Unknown statement or variable: " + stmt);
	}

	private String handleArrayIndex(String stmt, java.util.Map<String, Integer> vars,
			java.util.Map<String, String> functions) throws InterpretException {
		stmt = stmt.trim();
		if (!stmt.startsWith("["))
			return null;
		// find closing bracket for the array literal
		int close = stmt.indexOf(']');
		if (close < 0)
			return null;
		String inner = stmt.substring(1, close).trim();
		// parse elements (simple integers or variables)
		java.util.List<String> elems = new java.util.ArrayList<>();
		if (!inner.isEmpty()) {
			String[] parts = inner.split(",");
			for (String p : parts)
				elems.add(p.trim());
		}
		// after the closing bracket, expect an index like [i]
		String rest = stmt.substring(close + 1).trim();
		if (!rest.startsWith("[") || !rest.endsWith(")") && !rest.endsWith("]")) {
			// support either [index] or nothing
			if (rest.isEmpty())
				return null;
		}
		if (rest.startsWith("[")) {
			int idxClose = rest.indexOf(']');
			if (idxClose < 0)
				return null;
			String idxExpr = rest.substring(1, idxClose).trim();
			int idx = parseOperand(idxExpr, vars);
			if (idx < 0 || idx >= elems.size())
				throw new InterpretException("Index out of bounds: " + idx);
			String el = elems.get(idx);
			// evaluate element
			return handleLiteralOrVariable(el, vars, functions);
		}
		return null;
	}

	private String handleFunctionCall(String stmt, java.util.Map<String, Integer> vars,
			java.util.Map<String, String> functions) throws InterpretException {
		if (!stmt.endsWith("()"))
			return null;
		String name = stmt.substring(0, stmt.length() - 2).trim();
		if (!functions.containsKey(name))
			return null;
		String fn = functions.get(name);
		int arrow = fn.indexOf('>');
		int brace = fn.indexOf('{', arrow >= 0 ? arrow : 0);
		int braceEnd = fn.lastIndexOf('}');
		String body = "";
		if (brace >= 0 && braceEnd > brace) {
			body = fn.substring(brace + 1, braceEnd).trim();
		} else {
			int arr = fn.indexOf("=>");
			if (arr >= 0)
				body = fn.substring(arr + 2).trim();
		}
		int ret = body.indexOf("return ");
		String expr = body;
		if (ret >= 0) {
			expr = body.substring(ret + 7).trim();
			if (expr.endsWith(";"))
				expr = expr.substring(0, expr.length() - 1).trim();
		}
		return handleLiteralOrVariable(expr, vars, functions);
	}

	private String evaluateComparisonExpr(String stmt, java.util.Map<String, Integer> vars) throws InterpretException {
		String[] ops = { "<=", ">=", "==", "!=", "<", ">" };
		for (String op : ops) {
			int idx = stmt.indexOf(op);
			if (idx >= 0) {
				String left = stmt.substring(0, idx).trim();
				String right = stmt.substring(idx + op.length()).trim();
				int l = parseOperand(left, vars);
				int r = parseOperand(right, vars);
				boolean result = false;
				switch (op) {
					case "<=":
						result = l <= r;
						break;
					case ">=":
						result = l >= r;
						break;
					case "==":
						result = l == r;
						break;
					case "!=":
						result = l != r;
						break;
					case "<":
						result = l < r;
						break;
					case ">":
						result = l > r;
						break;
				}
				return result ? "true" : "false";
			}
		}
		return null;
	}

	// internal interpret that reuses the provided vars map so nested evaluations
	// see updates
	private String interpret(String input, java.util.Map<String, Integer> vars, java.util.Map<String, String> functions,
			java.util.Map<String, java.util.List<String>> structDefs) throws InterpretException {
		if (input == null || input.isEmpty()) {
			return "";
		}
		input = input.trim();
		String[] statements = splitTopLevelStatements(input);
		String lastValue = null;
		for (String stmt : statements) {
			stmt = stmt.trim();
			if (stmt.isEmpty())
				continue;
			lastValue = processStatement(stmt, vars, functions, structDefs);
		}
		if (lastValue == null) {
			throw new InterpretException("No value to return");
		}
		return lastValue;
	}

	private String processStatement(String stmt, java.util.Map<String, Integer> vars,
			java.util.Map<String, String> functions, java.util.Map<String, java.util.List<String>> structDefs)
			throws InterpretException {
		// function declaration: fn name() : I32 => { ... }
		if (stmt.startsWith("fn ")) {
			int nameStart = 3;
			int paren = stmt.indexOf('(', nameStart);
			if (paren > nameStart) {
				String name = stmt.substring(nameStart, paren).trim();
				functions.put(name, stmt);
				return "";
			}
		}
		if (stmt.startsWith("while (") && stmt.contains(")")) {
			return handleWhileLoop(stmt, vars, functions);
		}
		if (stmt.startsWith("let mut ")) {
			return handleLetMut(stmt, vars);
		} else if (stmt.startsWith("let ")) {
			return handleLet(stmt, vars);
		} else if (stmt.contains("=") && !stmt.startsWith("let")) {
			return handleAssignment(stmt, vars);
		} else if (stmt.startsWith("if (") && stmt.contains(")") && stmt.contains("else")) {
			return handleConditional(stmt, vars, functions);
		} else {
			String structAccess = handleStructAccess(stmt, vars, functions, structDefs);
			if (structAccess != null)
				return structAccess;
			return handleLiteralOrVariable(stmt, vars, functions);
		}
	}

	private String handleStructAccess(String stmt, java.util.Map<String, Integer> vars,
			java.util.Map<String, String> functions, java.util.Map<String, java.util.List<String>> structDefs)
			throws InterpretException {
		stmt = stmt.trim();
		// struct declaration: struct Name { field : I32 }
		if (stmt.startsWith("struct ")) {
			int nameStart = 7;
			int brace = stmt.indexOf('{', nameStart);
			int braceEnd = stmt.indexOf('}', brace);
			if (brace > nameStart && braceEnd > brace) {
				String name = stripGenericName(stmt.substring(nameStart, brace).trim());
				String inside = stmt.substring(brace + 1, braceEnd).trim();
				java.util.List<String> fields = parseStructFieldNames(inside);
				structDefs.put(name, fields);
				return "";
			}
		}
		// struct literal access: Name { 100 }.field
		int dot = stmt.indexOf('.');
		if (dot > 0) {
			String left = stmt.substring(0, dot).trim();
			String field = stmt.substring(dot + 1).trim();
			// left should be like: Name { 100 }
			int brace = left.indexOf('{');
			int braceEnd = left.indexOf('}');
			if (brace > 0 && braceEnd > brace) {
				String name = stripGenericName(left.substring(0, brace).trim());
				String inner = left.substring(brace + 1, braceEnd).trim();
				java.util.List<String> defs = structDefs.get(name);
				if (defs == null)
					return null;
				// parse values
				String[] vals = inner.isEmpty() ? new String[0] : inner.split(",");
				int idx = -1;
				for (int i = 0; i < defs.size(); i++) {
					if (defs.get(i).equals(field)) {
						idx = i;
						break;
					}
				}
				if (idx == -1)
					throw new InterpretException("Unknown field: " + field);
				if (idx >= vals.length)
					throw new InterpretException("Missing field value for: " + field);
				String valExpr = vals[idx].trim();
				return handleLiteralOrVariable(valExpr, vars, functions);
			}
		}
		return null;
	}

	private String[] splitTopLevelStatements(String input) {
		java.util.List<String> parts = new java.util.ArrayList<>();
		StringBuilder cur = new StringBuilder();
		int depth = 0;
		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (c == '{') {
				depth++;
				cur.append(c);
				continue;
			}
			if (c == '}') {
				depth--;
				cur.append(c);
				// if we closed a top-level brace, and the next non-space char is not a
				// semicolon,
				// treat this as a statement boundary so e.g. "} get()" splits between brace and
				// call
				if (depth == 0) {
					int j = i + 1;
					while (j < input.length() && Character.isWhitespace(input.charAt(j)))
						j++;
					if (j < input.length() && input.charAt(j) != ';' && input.charAt(j) != '.') {
						parts.add(cur.toString());
						cur.setLength(0);
					}
				}
				continue;
			}
			if (c == ';' && depth == 0) {
				parts.add(cur.toString());
				cur.setLength(0);
				continue;
			}
			cur.append(c);
		}
		if (cur.length() > 0)
			parts.add(cur.toString());
		return parts.toArray(new String[0]);
	}

	private String handleWhileLoop(String stmt, java.util.Map<String, Integer> vars,
			java.util.Map<String, String> functions) throws InterpretException {
		int condStart = stmt.indexOf('(') + 1;
		int condEnd = stmt.indexOf(')');
		String condition = stmt.substring(condStart, condEnd).trim();
		String body = stmt.substring(condEnd + 1).trim();
		// body may end with a semicolon when split earlier; ensure it's a valid
		// statement
		String last = null;
		int safety = 10000; // prevent infinite loops
		while (evaluateCondition(condition, vars)) {
			if (safety-- <= 0)
				throw new InterpretException("Possible infinite loop");
			// execute body as a statement or block
			if (body.startsWith("{") && body.endsWith("}")) {
				String inner = body.substring(1, body.length() - 1).trim();
				last = interpret(inner, vars, functions, this.structDefs);
			} else {
				last = interpret(body, vars, functions, this.structDefs);
			}
		}
		return last == null ? "" : last;
	}

	private boolean evaluateCondition(String condition, java.util.Map<String, Integer> vars) throws InterpretException {
		condition = condition.trim();
		// support simple comparisons like a < b, a <= b, a > b, a >= b, a == b, a != b
		String[] ops = { "<=", ">=", "==", "!=", "<", ">" };
		for (String op : ops) {
			int idx = condition.indexOf(op);
			if (idx >= 0) {
				String left = condition.substring(0, idx).trim();
				String right = condition.substring(idx + op.length()).trim();
				int l = parseOperand(left, vars);
				int r = parseOperand(right, vars);
				switch (op) {
					case "<=":
						return l <= r;
					case ">=":
						return l >= r;
					case "==":
						return l == r;
					case "!=":
						return l != r;
					case "<":
						return l < r;
					case ">":
						return l > r;
				}
			}
		}
		// support boolean literals
		if ("true".equals(condition))
			return true;
		if ("false".equals(condition))
			return false;
		throw new InterpretException("Unsupported condition: " + condition);
	}

	private int parseOperand(String token, java.util.Map<String, Integer> vars) throws InterpretException {
		try {
			return Integer.parseInt(token);
		} catch (NumberFormatException e) {
			if (vars.containsKey(token))
				return vars.get(token);
			throw new InterpretException("Unknown operand: " + token);
		}
	}

	private String stripGenericName(String name) {
		int lt = name.indexOf('<');
		if (lt >= 0) {
			return name.substring(0, lt).trim();
		}
		return name;
	}

	private java.util.List<String> parseStructFieldNames(String inside) {
		java.util.List<String> fields = new java.util.ArrayList<>();
		if (inside == null || inside.isEmpty()) {
			return fields;
		}
		String[] fieldDecls = inside.split(",");
		for (String fd : fieldDecls) {
			String[] p = fd.split(":");
			if (p.length >= 1) {
				fields.add(p[0].trim());
			}
		}
		return fields;
	}

	private String handleLetMut(String stmt, java.util.Map<String, Integer> vars) throws InterpretException {
		String[] parts = stmt.substring(8).split("=");
		if (parts.length == 2) {
			String varName = parts[0].trim();
			String varValue = parts[1].trim();
			try {
				int value = Integer.parseInt(varValue);
				vars.put(varName, value);
				return String.valueOf(value);
			} catch (NumberFormatException e) {
				throw new InterpretException("Assigned value is not an integer");
			}
		} else {
			throw new InterpretException("Invalid let mut syntax");
		}
	}

	private String handleLet(String stmt, java.util.Map<String, Integer> vars) throws InterpretException {
		String[] parts = stmt.substring(4).split("=");
		if (parts.length == 2) {
			String varName = parts[0].trim();
			String varValue = parts[1].trim();
			try {
				int value = Integer.parseInt(varValue);
				vars.put(varName, value);
				return String.valueOf(value);
			} catch (NumberFormatException e) {
				throw new InterpretException("Assigned value is not an integer");
			}
		} else {
			throw new InterpretException("Invalid let syntax");
		}
	}

	private String handleAssignment(String stmt, java.util.Map<String, Integer> vars) throws InterpretException {
		stmt = stmt.trim();
		if (stmt.contains("+=")) {
			String[] parts = stmt.split("\\+=");
			if (parts.length == 2) {
				String varName = parts[0].trim();
				String varValue = parts[1].trim();
				if (!vars.containsKey(varName)) {
					throw new InterpretException("Variable not declared: " + varName);
				}
				try {
					int value = Integer.parseInt(varValue);
					int newValue = vars.get(varName) + value;
					vars.put(varName, newValue);
					return String.valueOf(newValue);
				} catch (NumberFormatException e) {
					throw new InterpretException("Assigned value is not an integer");
				}
			} else {
				throw new InterpretException("Invalid += assignment syntax");
			}
		} else {
			String[] parts = stmt.split("=");
			if (parts.length == 2) {
				String varName = parts[0].trim();
				String varValue = parts[1].trim();
				if (!vars.containsKey(varName)) {
					throw new InterpretException("Variable not declared: " + varName);
				}
				try {
					int value = Integer.parseInt(varValue);
					vars.put(varName, value);
					return String.valueOf(value);
				} catch (NumberFormatException e) {
					throw new InterpretException("Assigned value is not an integer");
				}
			} else {
				throw new InterpretException("Invalid assignment syntax");
			}
		}
	}

	private String handleConditional(String stmt, java.util.Map<String, Integer> vars,
			java.util.Map<String, String> functions) throws InterpretException {
		int condStart = stmt.indexOf('(') + 1;
		int condEnd = stmt.indexOf(')');
		String condition = stmt.substring(condStart, condEnd).trim();
		String[] parts = stmt.substring(condEnd + 1).split("else");
		if (parts.length == 2) {
			String trueBranch = parts[0].trim();
			String falseBranch = parts[1].trim();
			boolean condValue;
			if ("true".equals(condition)) {
				condValue = true;
			} else if ("false".equals(condition)) {
				condValue = false;
			} else {
				throw new InterpretException("Unsupported condition: " + condition);
			}
			String branch = condValue ? trueBranch : falseBranch;
			return interpret(branch, vars, functions, this.structDefs);
		} else {
			throw new InterpretException("Invalid if-else syntax");
		}
	}

	private String handleLiteralOrVariable(String stmt, java.util.Map<String, Integer> vars,
			java.util.Map<String, String> functions) throws InterpretException {
		if ("true".equals(stmt) || "false".equals(stmt)) {
			return stmt;
		}
		String fnCall = handleFunctionCall(stmt, vars, functions);
		if (fnCall != null)
			return fnCall;
		// pointer deref pattern *&x
		String ptr = handlePointerDeref(stmt, vars);
		if (ptr != null)
			return ptr;
		// array indexing: [a, b, ...][i]
		String arrResult = handleArrayIndex(stmt, vars, functions);
		if (arrResult != null)
			return arrResult;
		// Check for comparison expressions
		String comp = evaluateComparisonExpr(stmt, vars);
		if (comp != null)
			return comp;
		try {
			int value = Integer.parseInt(stmt);
			return String.valueOf(value);
		} catch (NumberFormatException e) {
			if (vars.containsKey(stmt)) {
				return String.valueOf(vars.get(stmt));
			} else {
				throw new InterpretException("Unknown statement or variable: " + stmt);
			}
		}
	}
}
