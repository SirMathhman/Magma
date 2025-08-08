package magma;

/**
 * Utility class for string operations.
 * <p>
 * This class provides functionality to transform JavaScript and TypeScript syntax into C syntax.
 * It follows a non-static approach to promote better object-oriented design and testability.
 */
public class Compiler {
	/**
	 * Detects type suffix in the value section of a variable declaration.
	 *
	 * @param valueSection the value section of the declaration
	 * @return the detected type suffix or null if none is found
	 */
	private String detectTypeSuffix(String valueSection) {
		String[] typeSuffixes = {"I8", "I16", "I32", "I64", "U8", "U16", "U32", "U64"};
		for (String suffix : typeSuffixes) {
			if (valueSection.contains(suffix)) {
				return suffix;
			}
		}
		return null;
	}

	/**
	 * Creates a declaration context from an input string.
	 *
	 * @param input the input string containing the declaration
	 * @return a DeclarationContext object with extracted information
	 */
	private DeclarationContext createContext(String input) {
		String variableName = input.substring(4, input.indexOf("=")).trim();
		String valueSection = input.substring(input.indexOf("="));
		String typeSuffix = detectTypeSuffix(valueSection);
		return new DeclarationContext(input, variableName, valueSection, typeSuffix);
	}

	/**
	 * Processes a TypeScript declaration with type annotation.
	 *
	 * @param context the declaration context
	 * @return the processed declaration as a C-style string
	 * @throws CompileException if there is a type incompatibility
	 */
	private String processTypeScriptDeclaration(DeclarationContext context) {
		String input = context.input();
		String typeSuffix = context.typeSuffix();
		String valueSection = context.valueSection();

		// Redefine variableName to extract only up to the type annotation
		String updatedVariableName = input.substring(4, input.indexOf(" : ")).trim();

		// Extract the type annotation
		String typeAnnotation = input.substring(input.indexOf(" : ") + 3, input.indexOf("=")).trim();

		// Check for type compatibility if a type suffix is present in the value
		if (typeSuffix != null && !typeAnnotation.equals(typeSuffix)) {
			throw new CompileException(
					"Type mismatch: Cannot assign " + typeSuffix + " value to " + typeAnnotation + " variable");
		}

		String type = mapTypeToC(typeAnnotation);

		// Remove type suffix from the value section if present
		String cleanValueSection = cleanValueSection(valueSection, typeSuffix);

		VariableDeclaration declaration = new VariableDeclaration(updatedVariableName, cleanValueSection);
		return createTypeDeclaration(declaration, type);
	}

	/**
	 * Cleans the value section by removing type suffix if present.
	 *
	 * @param valueSection the value section to clean
	 * @param typeSuffix   the type suffix to remove, if any
	 * @return the cleaned value section
	 */
	private String cleanValueSection(String valueSection, String typeSuffix) {
		if (typeSuffix != null) {
			return valueSection.replace(typeSuffix, "");
		}
		return valueSection;
	}

	/**
	 * Processes a variable declaration with a type suffix.
	 *
	 * @param context the declaration context
	 * @return the processed declaration as a C-style string
	 */
	private String processTypeSuffixDeclaration(DeclarationContext context) {
		String variableName = context.variableName();
		String valueSection = context.valueSection();
		String typeSuffix = context.typeSuffix();

		// Remove type suffix from the value section
		String cleanValueSection = cleanValueSection(valueSection, typeSuffix);
		String type = mapTypeToC(typeSuffix);
		VariableDeclaration declaration = new VariableDeclaration(variableName, cleanValueSection);
		return createTypeDeclaration(declaration, type);
	}

	/**
	 * Echoes the input string, with special handling for JavaScript and TypeScript-style variable declarations.
	 * If the input is a JavaScript 'let' declaration or TypeScript typed declaration,
	 * it will be converted to a C fixed-width integer type declaration.
	 * Otherwise, returns the input string unchanged.
	 * <p>
	 * The method handles the following formats:
	 * - JavaScript: "let x = 0;" → "int32_t x = 0;"
	 * - TypeScript with type annotations: "let x : TYPE = 0;" → "c_type x = 0;"
	 * where TYPE can be I8, I16, I32, I64, U8, U16, U32, U64
	 * - TypeScript with type suffix: "let x = 0TYPE;" → "c_type x = 0;"
	 * where TYPE can be I8, I16, I32, I64, U8, U16, U32, U64
	 * - Multiple declarations: "let x = 0; let y = x;" → "int32_t x = 0; int32_t y = x;"
	 * - Variable references: "let y = x;" → "int32_t y = x;"
	 *
	 * @param input the string to echo
	 * @return the transformed string or the same string if no transformation is needed
	 * @throws CompileException if there is a type incompatibility between the declared type and the value type
	 */
	public String compile(String input) {
		if (input == null) {
			return null;
		}

		// Check if the input contains variable declarations
		if (!input.contains("let ")) {
			return input;
		}

		// Handle multiple declarations with variable references (e.g., "let x = 100; let y = x;")
		if (input.contains("; let ")) {
			// If the input contains a specific pattern like "let x = 100; let y = x;"
			// Replace all occurrences of "let " with "int32_t "
			return input.replace("let ", "int32_t ");
		}

		// Create a context from the input
		DeclarationContext context = createContext(input);

		// Handle TypeScript-style declarations with type annotations (e.g., "let x : I32 = 0;")
		if (input.contains(" : ")) {
			return processTypeScriptDeclaration(context);
		}

		// Handle variable declarations with type suffixes
		if (context.typeSuffix() != null) {
			return processTypeSuffixDeclaration(context);
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
	 * Creates a type declaration using the provided variable declaration and C type.
	 *
	 * @param declaration the variable declaration containing name and value section
	 * @param type        the C type to use for the declaration
	 * @return a C-style type declaration
	 */
	private String createTypeDeclaration(VariableDeclaration declaration, String type) {
		return type + " " + declaration.name() + " " + declaration.valueSection();
	}
}