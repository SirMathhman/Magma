package magma;

public class Interpreter {
	public String interpret(String input) throws InterpretException {
		if (input == null || input.isEmpty()) {
			return "";
		} else {
			input = input.trim();
			// Handle simple integer
			try {
				Integer.parseInt(input);
				return input;
			} catch (NumberFormatException e) {
				// Not a direct integer, try variable assignment and usage
				if (input.startsWith("let ") && input.contains(";")) {
					String[] parts = input.split(";");
					if (parts.length == 2) {
						String assignment = parts[0].trim();
						String usage = parts[1].trim();
						if (assignment.startsWith("let ") && assignment.contains("=")) {
							String[] assignParts = assignment.substring(4).split("=");
							if (assignParts.length == 2) {
								String varName = assignParts[0].trim();
								String varValue = assignParts[1].trim();
								if (usage.equals(varName)) {
									try {
										Integer.parseInt(varValue);
										return varValue;
									} catch (NumberFormatException ex) {
										throw new InterpretException("Assigned value is not an integer");
									}
								}
							}
						}
					}
				}
				throw new InterpretException("Non-empty input is not allowed");
			}
		}
	}
}
