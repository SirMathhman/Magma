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
			// Handle let mut x = value
			if (stmt.startsWith("let mut ")) {
				String[] parts = stmt.substring(8).split("=");
				if (parts.length == 2) {
					String varName = parts[0].trim();
					String varValue = parts[1].trim();
					try {
						int value = Integer.parseInt(varValue);
						vars.put(varName, value);
						lastValue = String.valueOf(value);
					} catch (NumberFormatException e) {
						throw new InterpretException("Assigned value is not an integer");
					}
				} else {
					throw new InterpretException("Invalid let mut syntax");
				}
			}
			// Handle let x = value
			else if (stmt.startsWith("let ")) {
				String[] parts = stmt.substring(4).split("=");
				if (parts.length == 2) {
					String varName = parts[0].trim();
					String varValue = parts[1].trim();
					try {
						int value = Integer.parseInt(varValue);
						vars.put(varName, value);
						lastValue = String.valueOf(value);
					} catch (NumberFormatException e) {
						throw new InterpretException("Assigned value is not an integer");
					}
				} else {
					throw new InterpretException("Invalid let syntax");
				}
			}
			// Handle variable assignment: x = value
			else if (stmt.contains("=") && !stmt.startsWith("let")) {
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
						lastValue = String.valueOf(value);
					} catch (NumberFormatException e) {
						throw new InterpretException("Assigned value is not an integer");
					}
				} else {
					throw new InterpretException("Invalid assignment syntax");
				}
			}
			// Handle integer literal
			else {
				try {
					int value = Integer.parseInt(stmt);
					lastValue = String.valueOf(value);
				} catch (NumberFormatException e) {
					// Handle variable usage
					if (vars.containsKey(stmt)) {
						lastValue = String.valueOf(vars.get(stmt));
					} else {
						throw new InterpretException("Unknown statement or variable: " + stmt);
					}
				}
			}
		}
		if (lastValue == null) {
			throw new InterpretException("No value to return");
		}
		return lastValue;
	}
}
