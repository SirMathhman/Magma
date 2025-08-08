package magma;

/**
 * Utility class for string operations.
 */
public class StringUtils {

	/**
	 * Echoes the input string, with special handling for JavaScript-style variable declarations.
	 * If the input is a JavaScript 'let' declaration, it will be converted to a C 'int32_t' declaration.
	 * Otherwise, returns the input string unchanged.
	 *
	 * @param input the string to echo
	 * @return the transformed string or the same string if no transformation is needed
	 */
	public static String echo(String input) {
		// Check if the input is a JavaScript 'let' declaration
		if (input != null && input.startsWith("let ") && input.contains("=")) {
			// Replace 'let' with 'int32_t' to convert to C-style declaration
			return input.replaceFirst("let", "int32_t");
		}

		// Return input unchanged if it's not a JavaScript 'let' declaration
		return input;
	}
}