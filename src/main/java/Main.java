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

		// Check if it's an array type declaration
		Optional<String> arrayResult = parseArrayDeclaration(input);
		if (arrayResult.isPresent()) {
			return arrayResult;
		}

		// Check for all supported primitive types
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
			int charValue = getCharValue(escapedChar);

			return Optional.of(String.valueOf(charValue));
		}

		// Single character, convert to its ASCII value
		if (charContent.length() == 1) return Optional.of(String.valueOf((int) charContent.charAt(0)));

		return Optional.empty();
	}

	private static int getCharValue(char escapedChar) {
		return switch (escapedChar) {
			case 'n' -> '\n';
			case 't' -> '\t';
			case 'r' -> '\r';
			case '\\' -> '\\';
			case '\'' -> '\'';
			default -> escapedChar;
		};
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

	/**
	 * Parses an array declaration in the format "let myArray : [TYPE; LENGTH] = [val1, val2, val3];"
	 *
	 * @param input the Magma code containing an array declaration
	 * @return the equivalent C code for the array declaration, or empty if not a valid array declaration
	 */
	private static Optional<String> parseArrayDeclaration(String input) {
		// Check if input starts with "let" and contains ":" and "="
		if (!input.trim().startsWith("let") || !input.contains(":") || !input.contains("=")) {
			return Optional.empty();
		}

		// Find the positions of key elements in the declaration
		int letIndex = input.indexOf("let");
		int colonIndex = input.indexOf(":");
		int equalIndex = input.indexOf("=");
		// Find the semicolon at the end of the statement, not the one in the array type declaration
		int semicolonIndex = input.lastIndexOf(";");

		// Validate the structure
		if (letIndex == -1 || colonIndex == -1 || equalIndex == -1 || semicolonIndex == -1 || letIndex > colonIndex ||
				colonIndex > equalIndex || equalIndex > semicolonIndex) {
			return Optional.empty();
		}

		// Extract and trim the variable name
		String varName = input.substring(letIndex + 3, colonIndex).trim();

		// Extract and trim the type from the input
		String typeDeclaration = input.substring(colonIndex + 1, equalIndex).trim();

		// Check if it's an array type declaration (should start with "[" and contain ";")
		if (!typeDeclaration.startsWith("[") || !typeDeclaration.contains(";") || !typeDeclaration.endsWith("]")) {
			return Optional.empty();
		}

		// Extract the element type and array length
		int semicolonInTypeIndex = typeDeclaration.indexOf(";");
		if (semicolonInTypeIndex == -1 || semicolonInTypeIndex == typeDeclaration.length() - 1) {
			return Optional.empty();
		}

		String elementType = typeDeclaration.substring(1, semicolonInTypeIndex).trim();
		String lengthStr = typeDeclaration.substring(semicolonInTypeIndex + 1, typeDeclaration.length() - 1).trim();

		// Validate element type and length
		if (!isValidType(elementType) || !isValidArrayLength(lengthStr)) {
			return Optional.empty();
		}

		int length;
		try {
			length = Integer.parseInt(lengthStr);
		} catch (NumberFormatException e) {
			return Optional.empty();
		}

		// Extract and trim the array initializer
		String initializer = input.substring(equalIndex + 1, semicolonIndex).trim();

		// Check if it's a valid array initializer (should start with "[" and end with "]")
		if (!initializer.startsWith("[") || !initializer.endsWith("]")) {
			return Optional.empty();
		}

		// Parse the array initializer values
		String[] values = parseArrayInitializer(initializer);
		if (values == null || values.length != length) {
			return Optional.empty();
		}

		// Generate C code for the array declaration
		String cType = convertMagmaTypeToC(elementType);
		String cInitializer = "{" + String.join(", ", values) + "}";

		return Optional.of(cType + " " + varName + "[" + length + "] = " + cInitializer + ";");
	}

	/**
	 * Checks if the given type is a valid Magma type.
	 *
	 * @param type the type to check
	 * @return true if the type is valid, false otherwise
	 */
	private static boolean isValidType(String type) {
		String[] supportedTypes = {"I8", "I16", "I32", "I64", "U8", "U16", "U32", "U64", "Bool"};
		return Arrays.asList(supportedTypes).contains(type);
	}

	/**
	 * Checks if the given array length is valid.
	 *
	 * @param lengthStr the array length as a string
	 * @return true if the length is valid, false otherwise
	 */
	private static boolean isValidArrayLength(String lengthStr) {
		try {
			int length = Integer.parseInt(lengthStr);
			return length > 0;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * Parses an array initializer in the format "[val1, val2, val3]".
	 *
	 * @param initializer the array initializer string
	 * @return an array of the parsed values, or null if the initializer is invalid
	 */
	private static String[] parseArrayInitializer(String initializer) {
		// Remove the square brackets
		if (!initializer.startsWith("[") || !initializer.endsWith("]")) {
			return null;
		}

		String content = initializer.substring(1, initializer.length() - 1).trim();
		if (content.isEmpty()) {
			return new String[0];
		}

		// Split by commas, but handle whitespace
		String[] values = content.split("\\s*,\\s*");

		// Trim each value
		for (int i = 0; i < values.length; i++) {
			values[i] = values[i].trim();
		}

		return values;
	}
}