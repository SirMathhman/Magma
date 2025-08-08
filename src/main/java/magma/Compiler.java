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
	 * it will be converted to a C fixed-width integer type declaration.
	 * Otherwise, returns the input string unchanged.
	 * <p>
	 * The method handles the following formats:
	 * - JavaScript: "let x = 0;" → "int32_t x = 0;"
	 * - TypeScript with type annotations: "let x : TYPE = 0;" → "c_type x = 0;"
	 *   where TYPE can be I8, I16, I32, I64, U8, U16, U32, U64
	 * - TypeScript with type suffix: "let x = 0TYPE;" → "c_type x = 0;"
	 *   where TYPE can be I8, I16, I32, I64, U8, U16, U32, U64
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
		String type = "int32_t"; // Default type

		// Handle TypeScript-style declarations with type annotations (e.g., "let x : I32 = 0;")
		if (input.contains(" : ")) {
			// Redefine variableName to extract only up to the type annotation
			variableName = input.substring(4, input.indexOf(" : ")).trim();
			
			// Extract the type annotation
			String typeAnnotation = input.substring(input.indexOf(" : ") + 3, input.indexOf("=")).trim();
			type = mapTypeToC(typeAnnotation);
			
			VariableDeclaration declaration = new VariableDeclaration(variableName, valueSection);
			return createTypeDeclaration(declaration, type);
		}

		// Handle variable declarations with type suffixes
		String[] typeSuffixes = {"I8", "I16", "I32", "I64", "U8", "U16", "U32", "U64"};
		for (String suffix : typeSuffixes) {
			if (valueSection.contains(suffix)) {
				// Remove type suffix from the value section
				valueSection = valueSection.replace(suffix, "");
				type = mapTypeToC(suffix);
				VariableDeclaration declaration = new VariableDeclaration(variableName, valueSection);
				return createTypeDeclaration(declaration, type);
			}
		}

		// Handle standard JavaScript declarations (e.g., "let x = 0;")
		return input.replaceFirst("let", "int32_t");
	}

	/**
	 * Maps TypeScript/JavaScript type to corresponding C type.
	 *
	 * @param type the TypeScript/JavaScript type (I8, I16, I32, I64, U8, U16, U32, U64)
	 * @return the corresponding C type
	 */
	private String mapTypeToC(String type) {
		return switch (type) {
			case "I8" -> "int8_t";
			case "I16" -> "int16_t";
			case "I32" -> "int32_t";
			case "I64" -> "int64_t";
			case "U8" -> "uint8_t";
			case "U16" -> "uint16_t";
			case "U32" -> "uint32_t";
			case "U64" -> "uint64_t";
			default -> "int32_t"; // Default to int32_t
		};
	}

	/**
	 * Represents a variable declaration with its components.
	 * This record helps encapsulate the variable name and value section.
	 */
	private record VariableDeclaration(String name, String valueSection) {
	}
	
	/**
	 * Creates a type declaration using the provided variable declaration and C type.
	 *
	 * @param declaration the variable declaration containing name and value section
	 * @param type the C type to use for the declaration
	 * @return a C-style type declaration
	 */
	private String createTypeDeclaration(VariableDeclaration declaration, String type) {
		return type + " " + declaration.name() + " " + declaration.valueSection();
	}
	
	/**
	 * Creates an int32_t declaration using the provided variable name and value section.
	 * This method is kept for backward compatibility.
	 *
	 * @param variableName the name of the variable
	 * @param valueSection the value assignment section including the equals sign
	 * @return a C-style int32_t declaration
	 */
	private String createInt32Declaration(String variableName, String valueSection) {
		VariableDeclaration declaration = new VariableDeclaration(variableName, valueSection);
		return createTypeDeclaration(declaration, "int32_t");
	}
}