import java.util.Optional;

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
		// Handle variable declarations with integer types
		return parseVariableDeclaration(input).orElse(input);
	}

	private static Optional<String> parseVariableDeclaration(String input) {
		if (!input.startsWith("let ") || !input.contains(" : ") || !input.contains(" = ")) {
			return Optional.empty();
		}
		// Check for all supported types
		String[] supportedTypes = {"I8", "I16", "I32", "I64", "U8", "U16", "U32", "U64", "Bool"};

		for (String type : supportedTypes) {
			String typePattern = " : " + type + " = ";
			Optional<String> cType = parseTypeDeclaration(input, type, typePattern);
			if (cType.isPresent()) return cType;
		}
		return Optional.empty();
	}

	private static Optional<String> parseTypeDeclaration(String input, String type, String typePattern) {
		if (input.contains(typePattern)) {
			// Extract the variable name
			String varName = input.substring(4, input.indexOf(typePattern));

			// Extract the value
			String value = input.substring(input.indexOf(typePattern) + typePattern.length(), input.indexOf(";"));

			// Handle boolean values
			if (type.equals("Bool")) {
				// Ensure boolean values are properly translated
				if (value.trim().equals("true") || value.trim().equals("false")) {
					// C uses the same true/false literals, so we can use the value as is
					value = value.trim();
				}
			}

			final var cType = mapMagmaTypeToC(type);
			return Optional.of(cType + " " + varName + " = " + value + ";");
		}
		return Optional.empty();
	}

	private static String mapMagmaTypeToC(String type) {
		// Map Magma type to C type
		return switch (type) {
			case "I8" -> "int8_t";
			case "I16" -> "int16_t";
			case "I32" -> "int32_t";
			case "I64" -> "int64_t";
			case "U8" -> "uint8_t";
			case "U16" -> "uint16_t";
			case "U32" -> "uint32_t";
			case "U64" -> "uint64_t";
			case "Bool" -> "bool";
			default -> "int32_t"; // Default to int32_t
		};
	}
}