import java.util.Arrays;
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
		// Check if input starts with "let" followed by whitespace
		if (!input.trim().startsWith("let") || !input.contains(":") || !input.contains("=")) return Optional.empty();
		// Check for all supported types
		String[] supportedTypes = {"I8", "I16", "I32", "I64", "U8", "U16", "U32", "U64", "Bool"};

		return Arrays.stream(supportedTypes)
								 .map(type -> parseTypePattern(input, type))
								 .flatMap(Optional::stream)
								 .findFirst();
	}

	private static Optional<String> parseTypePattern(String input, String type) {
		// Check if the type exists in the input with various whitespace patterns
		if (!input.matches(".*:\\s*" + type + "\\s*=.*")) return Optional.empty();
		return parseTypeDeclaration(input, type);
	}

	private static Optional<String> parseTypeDeclaration(String input, String type) {
		// Find the positions of key elements in the declaration
		int letIndex = input.indexOf("let");
		int colonIndex = input.indexOf(":");
		int equalIndex = input.indexOf("=");
		int semicolonIndex = input.indexOf(";");

		// Validate the structure
		if ((letIndex == -1) || (colonIndex == -1) || (equalIndex == -1) || (semicolonIndex == -1) ||
				(letIndex > colonIndex) || (colonIndex > equalIndex) || (equalIndex > semicolonIndex)) {
			return Optional.empty();
		}

		// Extract and trim the variable name
		String varName = input.substring(letIndex + 3, colonIndex).trim();

		// Extract and trim the type from the input
		String extractedType = input.substring(colonIndex + 1, equalIndex).trim();
		if (!extractedType.equals(type)) return Optional.empty();

		// Extract and trim the value
		String value = input.substring(equalIndex + 1, semicolonIndex).trim();
		value = parseCharacterLiteral(type, value).orElse(value);
		final var cType = convertMagmaTypeToC(type);
		return Optional.of(cType + " " + varName + " = " + value + ";");
	}

	private static Optional<String> parseCharacterLiteral(String type, String value) {
		// Handle character literals for I8 type
		if (!type.equals("I8") || (value.length() < 3) || (value.charAt(0) != '\'') ||
				(value.charAt(value.length() - 1) != '\'')) {return Optional.empty();}

		// Extract the character from between the single quotes
		String charContent = value.substring(1, value.length() - 1);

		// Handle escape sequences
		if ((charContent.length() == 2) && (charContent.charAt(0) == '\\')) {
			char escapedChar = charContent.charAt(1);
			int charValue = switch (escapedChar) {
				case 'n' -> '\n';
				case 't' -> '\t';
				case 'r' -> '\r';
				case '\\' -> '\\';
				case '\'' -> '\'';
				default -> escapedChar;
			};

			return Optional.of(String.valueOf(charValue));
		}

		// Single character, convert to its ASCII value
		if (charContent.length() == 1) return Optional.of(String.valueOf((int) charContent.charAt(0)));

		return Optional.empty();
	}

	private static String convertMagmaTypeToC(String type) {
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