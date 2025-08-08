package magma;

/**
 * Utility class for string operations.
 * <p>
 * This class provides functionality to transform JavaScript and TypeScript syntax into C syntax.
 * It follows a non-static approach to promote better object-oriented design and testability.
 */
public class Compiler {

	/**
	 * Echoes the input string, with special handling for JavaScript and TypeScript-style variable declarations.
	 * If the input is a JavaScript 'let' declaration or TypeScript typed declaration,
	 * it will be converted to a C 'int32_t' declaration.
	 * Otherwise, returns the input string unchanged.
	 * <p>
	 * The method handles the following formats:
	 * - JavaScript: "let x = 0;" → "int32_t x = 0;"
	 * - TypeScript with type annotation: "let x : I32 = 0;" → "int32_t x = 0;"
	 * - TypeScript with I32 suffix: "let x = 0I32;" → "int32_t x = 0;"
	 *
	 * @param input the string to echo
	 * @return the transformed string or the same string if no transformation is needed
	 */
	public String compile(String input) {
		// Check if the input is null or not a variable declaration
		if (input == null || !input.startsWith("let ") || !input.contains("=")) {
			// Return input unchanged if it's not a variable declaration
			return input;
		}

		// Extract the variable name from the declaration
		String variableName = input.substring(4, input.indexOf("=")).trim();
		String valueSection = input.substring(input.indexOf("="));

		// Handle TypeScript-style declarations with type annotations (e.g., "let x : I32 = 0;")
		if (input.contains(" : ")) {
			// Redefine variableName to extract only up to the type annotation
			variableName = input.substring(4, input.indexOf(" : ")).trim();
			// Create a new C-style declaration with int32_t type
			return createInt32Declaration(variableName, valueSection);
		}

		// Handle variable declarations with I32 suffix (e.g., "let x = 0I32;")
		if (input.contains("I32")) {
			// Remove I32 suffix from the value section
			valueSection = valueSection.replace("I32", "");
			// Create a new C-style declaration with int32_t type
			return createInt32Declaration(variableName, valueSection);
		}

		// Handle standard JavaScript declarations (e.g., "let x = 0;")
		return input.replaceFirst("let", "int32_t");
	}

	/**
	 * Creates an int32_t declaration using the provided variable name and value section.
	 *
	 * @param variableName the name of the variable
	 * @param valueSection the value assignment section including the equals sign
	 * @return a C-style int32_t declaration
	 */
	private String createInt32Declaration(String variableName, String valueSection) {
		return "int32_t " + variableName + " " + valueSection;
	}
}