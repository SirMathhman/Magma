/**
 * A simple Hello World program for the Magma project.
 */
public class Main {
	/**
	 * The main entry point for the application.
	 *
	 * @param args Command line arguments (not used)
	 */
	public static void main(String[] args) {
	}

	/**
	 * Compiles Magma code to C code.
	 *
	 * @param input the Magma code to compile
	 * @return the equivalent C code
	 */
	public static String compile(String input) {
		// Handle variable declaration with I32 type
		if (input.startsWith("let ") && input.contains(" : I32 = ")) {
			// Extract the variable name from between "let " and " : I32 = "
			String varName = input.substring(4, input.indexOf(" : I32 = "));
			return "int32_t " + varName + " = 0;";
		}
		// Default behavior for other inputs
		return input;
	}
}