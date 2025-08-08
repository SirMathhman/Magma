package magma;

/**
 * A simple class that processes strings but is stubbed to always throw an error.
 */
public class Compiler {

	/**
	 * Processes the input string.
	 *
	 * @param input The string to process
	 * @return An empty string if the input is empty, otherwise throws an exception
	 * @throws UnsupportedOperationException Thrown to indicate the method is not fully implemented yet
	 */
	public String process(String input) {
		if (input.isEmpty()) {
			return "";
		}

		// Handle variable declarations with pattern "let variableName = value;"
		if (input.startsWith("let ") && input.contains("=") && input.endsWith(";")) {
			return handleVariableDeclaration(input);
		}

		throw new UnsupportedOperationException("This method is not implemented yet");
	}

	/**
	 * Handles variable declarations with pattern "let variableName = value;".
	 *
	 * @param input The variable declaration string
	 * @return The transformed variable declaration
	 */
	private String handleVariableDeclaration(String input) {
		// Extract the variable name and value
		String variablePart = input.substring(4, input.length() - 1); // Remove "let " and ";"
		int equalsIndex = variablePart.indexOf('=');
		if (equalsIndex > 0) {
			String variableName = variablePart.substring(0, equalsIndex).trim();
			String value = variablePart.substring(equalsIndex + 1).trim();
			return "int32_t " + variableName + " = " + value + ";";
		}
		throw new UnsupportedOperationException("Invalid variable declaration format");
	}
}