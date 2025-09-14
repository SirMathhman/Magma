package magma;

public class Interpreter {
	public String interpret(String input) throws InterpretException {
		if (input == null || input.isEmpty()) {
			return "";
		}
		input = input.trim();
		java.util.Map<String, Integer> vars = new java.util.HashMap<>();
		String[] statements = input.split(";");
		String lastValue = null;
		for (String stmt : statements) {
			stmt = stmt.trim();
			if (stmt.isEmpty())
				continue;
			if (stmt.startsWith("let mut ")) {
				lastValue = handleLetMut(stmt, vars);
			} else if (stmt.startsWith("let ")) {
				lastValue = handleLet(stmt, vars);
			} else if (stmt.contains("=") && !stmt.startsWith("let")) {
				lastValue = handleAssignment(stmt, vars);
			} else if (stmt.startsWith("if (") && stmt.contains(")") && stmt.contains("else")) {
				lastValue = handleConditional(stmt, vars);
			} else {
				lastValue = handleLiteralOrVariable(stmt, vars);
			}
		}
		if (lastValue == null) {
			throw new InterpretException("No value to return");
		}
		return lastValue;
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

	private String handleConditional(String stmt, java.util.Map<String, Integer> vars) throws InterpretException {
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
			return interpret(branch);
		} else {
			throw new InterpretException("Invalid if-else syntax");
		}
	}

	private String handleLiteralOrVariable(String stmt, java.util.Map<String, Integer> vars) throws InterpretException {
		if ("true".equals(stmt) || "false".equals(stmt)) {
			return stmt;
		} else {
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
}
