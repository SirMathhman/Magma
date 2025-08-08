package magma;

/**
 * Utility class for string operations.
 * <p>
 * This class provides functionality to transform JavaScript and TypeScript syntax into C syntax.
 * It follows a non-static approach to promote better object-oriented design and testability.
 */
public class StringUtils {

	/**
	 * Echoes the input string, with special handling for JavaScript and TypeScript-style variable declarations.
	 * If the input is a JavaScript 'let' declaration or TypeScript typed declaration,
	 * it will be converted to a C 'int32_t' declaration.
	 * Otherwise, returns the input string unchanged.
	 * <p>
	 * The method handles both formats:
	 * - JavaScript: "let x = 0;" → "int32_t x = 0;"
	 * - TypeScript: "let x : I32 = 0;" → "int32_t x = 0;"
	 *
	 * @param input the string to echo
	 * @return the transformed string or the same string if no transformation is needed
	 */
	public String echo(String input) {
		// Check if the input is null or not a variable declaration
		if (input == null || !input.startsWith("let ") || !input.contains("=")) {
			// Return input unchanged if it's not a variable declaration
			return input;
		}

		// Handle TypeScript-style declarations with type annotations (e.g., "let x : I32 = 0;")
		if (input.contains(" : ")) {
			// Extract the variable name and value, ignoring the type annotation
			String variableName = input.substring(4, input.indexOf(" : ")).trim();
			String valueSection = input.substring(input.indexOf("="));

			// Create a new C-style declaration with int32_t type
			return "int32_t " + variableName + " " + valueSection;
		}

		// Handle standard JavaScript declarations (e.g., "let x = 0;")
		return input.replaceFirst("let", "int32_t");
	}
}