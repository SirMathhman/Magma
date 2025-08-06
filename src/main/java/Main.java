import java.util.Arrays;
import java.util.List;
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
		System.out.println("compile called with: " + input);
		// Handle variable declarations
		String result = parseDeclaration(input).orElse(input);
		System.out.println("compile returning: " + result);
		return result;
	}

	/**
	 * Parses any variable declaration (primitive or array).
	 *
	 * @param input the Magma code containing a variable declaration
	 * @return the equivalent C code for the variable declaration, or empty if not a valid declaration
	 */
	private static Optional<String> parseDeclaration(String input) {
		System.out.println("parseDeclaration called with: " + input);
		// Check if input starts with "let" followed by whitespace
		if (!input.trim().startsWith("let") || !input.contains(":") || !input.contains("=")) {
			System.out.println("parseDeclaration returning empty (not a valid declaration)");
			return Optional.empty();
		}

		// Extract common parts of the declaration
		int letIndex = input.indexOf("let");
		int colonIndex = input.indexOf(":");
		int equalIndex = input.indexOf("=");
		int semicolonIndex = input.lastIndexOf(";");

		// Validate the structure
		if (letIndex == -1 || colonIndex == -1 || equalIndex == -1 || semicolonIndex == -1 || letIndex > colonIndex ||
				colonIndex > equalIndex || equalIndex > semicolonIndex) {return Optional.empty();}

		// Extract and trim the variable name
		String varName = input.substring(letIndex + 3, colonIndex).trim();

		// Extract and trim the type from the input
		String typeDeclaration = input.substring(colonIndex + 1, equalIndex).trim();

		// Extract and trim the value
		String value = input.substring(equalIndex + 1, semicolonIndex).trim();

		// Check if it's a string literal (starts and ends with double quotes)
		if (value.length() >= 2 && value.charAt(0) == '"' && value.charAt(value.length() - 1) == '"') {
			System.out.println("parseDeclaration detected string literal: " + value);

			// Extract the string content from between the double quotes
			String stringContent = value.substring(1, value.length() - 1);

			// Count the actual length after processing escape sequences
			int actualLength = 0;
			for (int i = 0; i < stringContent.length(); i++) {
				char c = stringContent.charAt(i);
				if (c == '\\' && i + 1 < stringContent.length()) {
					// Skip the next character as it's part of the escape sequence
					i++;
				}
				actualLength++;
			}

			// Parse the string literal to get the initializer
			Optional<String> initializer = parseStringLiteral(value);
			if (initializer.isEmpty()) {
				System.out.println("parseDeclaration returning empty: failed to parse string literal");
				return Optional.empty();
			}

			// Convert I8 to C type
			String cType = "int8_t";

			// Generate C code for the array declaration
			String result = cType + " " + varName + "[" + actualLength + "] = " + initializer.get() + ";";
			System.out.println("parseDeclaration returning for string: " + result);
			return Optional.of(result);
		}

		// Create a Declaration record
		Declaration declaration = new Declaration(varName, typeDeclaration, value);

		// Check if it's an array type declaration
		Optional<String> result;
		if (typeDeclaration.startsWith("[") && typeDeclaration.contains(";") && typeDeclaration.endsWith("]")) {
			result = parseArrayType(declaration);
			System.out.println("parseDeclaration returning from parseArrayType: " + result);
		} else {
			// It's a primitive type declaration
			result = parsePrimitiveType(declaration);
			System.out.println("parseDeclaration returning from parsePrimitiveType: " + result);
		}
		return result;
	}

	/**
	 * Parses a primitive type variable declaration.
	 *
	 * @param declaration the declaration record containing variable name, type declaration, and value
	 * @return the equivalent C code for the variable declaration, or empty if not a valid declaration
	 */
	private static Optional<String> parsePrimitiveType(Declaration declaration) {
		// Check for all supported primitive types
		String[] supportedTypes = {"I8", "I16", "I32", "I64", "U8", "U16", "U32", "U64", "Bool"};

		if (!Arrays.asList(supportedTypes).contains(declaration.typeDeclaration())) return Optional.empty();

		// Check if it's a string literal (starts and ends with double quotes)
		String value = declaration.value();
		if (value.length() >= 2 && value.charAt(0) == '"' && value.charAt(value.length() - 1) == '"') {
			System.out.println("parsePrimitiveType detected string literal: " + value);

			// Extract the string content from between the double quotes
			String stringContent = value.substring(1, value.length() - 1);

			// Parse the string literal to get the initializer
			Optional<String> initializer = parseStringLiteral(value);
			if (initializer.isEmpty()) {
				System.out.println("parsePrimitiveType returning empty: failed to parse string literal");
				return Optional.empty();
			}

			// Convert I8 to C type
			String cType = "int8_t";

			// Generate C code for the array declaration
			String result =
					cType + " " + declaration.varName() + "[" + stringContent.length() + "] = " + initializer.get() + ";";
			System.out.println("parsePrimitiveType returning for string: " + result);
			return Optional.of(result);
		}

		// Create a TypeValue record for the type and value
		TypeValue typeValue = new TypeValue(declaration.typeDeclaration(), declaration.value());

		// Handle character literals for I8 type
		String processedValue = parseCharacterLiteral(typeValue).orElse(declaration.value());

		// Convert Magma type to C type
		String cType = convertMagmaTypeToC(declaration.typeDeclaration());

		return Optional.of(cType + " " + declaration.varName() + " = " + processedValue + ";");
	}

	/**
	 * Parses an array type variable declaration.
	 *
	 * @param declaration the declaration record containing variable name, type declaration, and value
	 * @return the equivalent C code for the array declaration, or empty if not a valid array declaration
	 */
	private static Optional<String> parseArrayType(Declaration declaration) {
		System.out.println("parseArrayType called with: " + declaration);
		String typeDeclaration = declaration.typeDeclaration();
		String value = declaration.value();

		// Check if it's an array type declaration (should start with "[" and contain ";")
		if (!typeDeclaration.startsWith("[") || !typeDeclaration.contains(";") || !typeDeclaration.endsWith("]")) {
			System.out.println("parseArrayType returning empty: not a valid array type declaration");
			return Optional.empty();
		}

		// Extract the element type and array length
		// For nested arrays like [[I32; 3]; 2], we need to find the matching closing bracket
		// for the element type, not just the first semicolon
		int openBrackets = 0;
		int semicolonInTypeIndex = -1;

		for (int i = 0; i < typeDeclaration.length(); i++) {
			char c = typeDeclaration.charAt(i);
			if (c == '[') {openBrackets++;} else if (c == ']') {openBrackets--;} else if (c == ';' && openBrackets == 1) {
				// This is the semicolon at the top level of the array type
				semicolonInTypeIndex = i;
				break;
			}
		}

		if (semicolonInTypeIndex == -1 || semicolonInTypeIndex == typeDeclaration.length() - 1) {
			System.out.println("parseArrayType returning empty: invalid semicolon position");
			return Optional.empty();
		}

		String elementType = typeDeclaration.substring(1, semicolonInTypeIndex).trim();
		String lengthStr = typeDeclaration.substring(semicolonInTypeIndex + 1, typeDeclaration.length() - 1).trim();
		System.out.println("parseArrayType elementType: " + elementType + ", lengthStr: " + lengthStr);

		// Validate element type and length
		if (!isValidType(elementType)) {
			System.out.println("parseArrayType returning empty: invalid element type");
			return Optional.empty();
		}

		if (!isValidArrayLength(lengthStr)) {
			System.out.println("parseArrayType returning empty: invalid array length");
			return Optional.empty();
		}

		int length;
		try {
			length = Integer.parseInt(lengthStr);
		} catch (NumberFormatException e) {
			System.out.println("parseArrayType returning empty: NumberFormatException");
			return Optional.empty();
		}

		// Create an ArrayType record
		ArrayType arrayType = new ArrayType(elementType, length);
		System.out.println("parseArrayType created ArrayType: " + arrayType);

		// Check if it's a valid array initializer (should start with "[" and end with "]")
		if (!value.startsWith("[") || !value.endsWith("]")) {
			System.out.println("parseArrayType returning empty: invalid array initializer format");
			return Optional.empty();
		}

		// Parse the array initializer values
		String[] values = parseArrayInitializer(value);
		System.out.println("parseArrayType parsed values: " + (values != null ? Arrays.toString(values) : "null"));
		if (values == null) {
			System.out.println("parseArrayType returning empty: values is null");
			return Optional.empty();
		}

		if (values.length != arrayType.length()) {
			System.out.println("parseArrayType returning empty: values length (" + values.length + ") != array length (" +
												 arrayType.length() + ")");
			return Optional.empty();
		}

		// Generate C code for the array declaration
		String cType;
		StringBuilder dimensionsStr = new StringBuilder();
		String cInitializer;

		// Handle nested array types
		if (elementType.startsWith("[") && elementType.contains(";") && elementType.endsWith("]")) {
			// For nested arrays, we need to process each element recursively
			String[] processedValues = new String[values.length];

			for (int i = 0; i < values.length; i++) {
				// Create a temporary declaration for each element
				Declaration tempDeclaration = new Declaration("temp", elementType, values[i]);
				Optional<String> result = parseArrayType(tempDeclaration);

				if (result.isEmpty()) return Optional.empty();

				// Extract just the initializer part from the result
				String tempResult = result.get();
				int startIndex = tempResult.indexOf("{");
				int endIndex = tempResult.lastIndexOf("}");

				if (startIndex == -1 || endIndex == -1) return Optional.empty();

				processedValues[i] = tempResult.substring(startIndex, endIndex + 1);
			}

			cInitializer = "{" + String.join(", ", processedValues) + "}";

			// Get the base type and all dimensions for nested arrays
			String baseType = getBaseType(elementType);
			String[] dimensions = getAllDimensions(elementType);

			cType = convertMagmaTypeToC(baseType);

			// Add dimensions for nested arrays in the correct order
			for (String dimension : dimensions) {dimensionsStr.append("[").append(dimension).append("]");}
		} else {
			// For simple arrays, just join the values
			cType = convertMagmaTypeToC(arrayType.elementType());
			cInitializer = "{" + String.join(", ", values) + "}";
		}

		String result;
		if (dimensionsStr.isEmpty()) {
			// For simple arrays
			result = cType + " " + declaration.varName() + "[" + arrayType.length() + "]" + " = " + cInitializer + ";";
		} else {
			// For nested arrays, add all dimensions in the correct order
			result =
					cType + " " + declaration.varName() + "[" + arrayType.length() + "]" + dimensionsStr + " = " + cInitializer +
					";";
		}
		System.out.println("parseArrayType returning: " + result);
		return Optional.of(result);
	}

	/**
	 * Gets the base type from a nested array type.
	 * For example, [[I32; 3]; 2] returns I32.
	 *
	 * @param type the nested array type
	 * @return the base type
	 */
	private static String getBaseType(String type) {
		if (!type.startsWith("[") || !type.contains(";") || !type.endsWith("]")) return type;

		int semicolonIndex = type.indexOf(";");
		String elementType = type.substring(1, semicolonIndex).trim();

		return getBaseType(elementType);
	}

	/**
	 * Gets all dimensions from a nested array type.
	 * For example, [[I32; 3]; 2] returns ["2", "3"].
	 *
	 * @param type the nested array type
	 * @return array of dimension strings
	 */
	private static String[] getAllDimensions(String type) {
		if (!type.startsWith("[") || !type.contains(";") || !type.endsWith("]")) return new String[0];

		// For nested arrays like [[I32; 3]; 2], we need to find the matching closing bracket
		// for the element type, not just the first semicolon
		int openBrackets = 0;
		int semicolonIndex = -1;

		for (int i = 0; i < type.length(); i++) {
			char c = type.charAt(i);
			if (c == '[') {openBrackets++;} else if (c == ']') {openBrackets--;} else if (c == ';' && openBrackets == 1) {
				// This is the semicolon at the top level of the array type
				semicolonIndex = i;
				break;
			}
		}

		if (semicolonIndex == -1) return new String[0];

		String elementType = type.substring(1, semicolonIndex).trim();
		String lengthStr = type.substring(semicolonIndex + 1, type.length() - 1).trim();

		String[] innerDimensions = getAllDimensions(elementType);
		String[] dimensions = new String[innerDimensions.length + 1];

		dimensions[0] = lengthStr;
		System.arraycopy(innerDimensions, 0, dimensions, 1, innerDimensions.length);

		return dimensions;
	}

	/**
	 * Parses a character literal for I8 type.
	 *
	 * @param typeValue the record containing type and value
	 * @return the equivalent C code for the character literal, or empty if not a valid character literal
	 */
	private static Optional<String> parseCharacterLiteral(TypeValue typeValue) {
		String type = typeValue.type();
		String value = typeValue.value();

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

	/**
	 * Parses a string literal and converts it to an array of I8 elements.
	 *
	 * @param value the string literal value (enclosed in double quotes)
	 * @return the equivalent C code for the string as an array of I8 elements, or empty if not a valid string literal
	 */
	private static Optional<String> parseStringLiteral(String value) {
		System.out.println("parseStringLiteral called with: " + value);

		// Check if it's a valid string literal (starts and ends with double quotes)
		if (value.length() < 2 || value.charAt(0) != '"' || value.charAt(value.length() - 1) != '"') {
			System.out.println("parseStringLiteral returning empty: not a valid string literal");
			return Optional.empty();
		}

		// Extract the string content from between the double quotes
		String stringContent = value.substring(1, value.length() - 1);
		System.out.println("parseStringLiteral extracted content: " + stringContent);

		// Convert each character to its ASCII value
		final var initializer = buildStringArrayInitializer(stringContent);

		System.out.println("parseStringLiteral returning: " + initializer);
		return Optional.of(initializer.toString());
	}

	private static StringBuilder buildStringArrayInitializer(String stringContent) {
		StringBuilder initializer = new StringBuilder("{");
		for (int i = 0; i < stringContent.length(); i++) {
			char c = stringContent.charAt(i);

			// Handle escape sequences
			if (c == '\\' && i + 1 < stringContent.length()) {
				char nextChar = stringContent.charAt(i + 1);
				initializer.append(getCharValue(nextChar));
				i++; // Skip the next character as it's part of the escape sequence
			} else {
				initializer.append((int) c);
			}

			// Add comma if not the last character
			if (i < stringContent.length() - 1) {
				initializer.append(", ");
			}
		}
		initializer.append("}");
		return initializer;
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
	 * Checks if the given type is a valid Magma type.
	 *
	 * @param type the type to check
	 * @return true if the type is valid, false otherwise
	 */
	private static boolean isValidType(String type) {
		System.out.println("isValidType called with: " + type);

		// Check for primitive types
		String[] supportedTypes = {"I8", "I16", "I32", "I64", "U8", "U16", "U32", "U64", "Bool"};
		if (Arrays.asList(supportedTypes).contains(type)) {
			System.out.println("isValidType returning true: primitive type");
			return true;
		}

		final var isValidArrayType = validateArrayTypeFormat(type);
		if (isValidArrayType) return true;
		System.out.println("isValidType returning false: not a valid type");
		return false;
	}

	private static boolean validateArrayTypeFormat(String type) {
		// Check for array types (format: [elementType; length])
		if (!type.startsWith("[") || !type.contains(";") || !type.endsWith("]")) return false;

		// For nested arrays like [[I32; 3]; 2], we need to find the matching closing bracket
		// for the element type, not just the first semicolon
		int openBrackets = 0;
		int semicolonIndex = -1;

		for (int i = 0; i < type.length(); i++) {
			char c = type.charAt(i);
			if (c == '[') {openBrackets++;} else if (c == ']') {openBrackets--;} else if (c == ';' && openBrackets == 1) {
				// This is the semicolon at the top level of the array type
				semicolonIndex = i;
				break;
			}
		}

		if (semicolonIndex <= 1 || semicolonIndex >= type.length() - 2) return false;
		String elementType = type.substring(1, semicolonIndex).trim();
		String lengthStr = type.substring(semicolonIndex + 1, type.length() - 1).trim();
		System.out.println("isValidType array type - elementType: " + elementType + ", lengthStr: " + lengthStr);

		// Recursively check if the element type is valid
		boolean result = isValidType(elementType) && isValidArrayLength(lengthStr);
		System.out.println("isValidType returning " + result + " for array type");
		return result;
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
	 * Also handles nested arrays like "[[1, 2], [3, 4]]".
	 *
	 * @param initializer the array initializer string
	 * @return an array of the parsed values, or null if the initializer is invalid
	 */
	private static String[] parseArrayInitializer(String initializer) {
		System.out.println("parseArrayInitializer called with: " + initializer);

		// Remove the square brackets
		if (!initializer.startsWith("[") || !initializer.endsWith("]")) {
			System.out.println("parseArrayInitializer returning null: invalid format");
			return null;
		}

		String content = initializer.substring(1, initializer.length() - 1).trim();
		if (content.isEmpty()) {
			System.out.println("parseArrayInitializer returning empty array: content is empty");
			return new String[0];
		}

		// For nested arrays, we need to properly handle the commas
		// We'll split by commas, but only those at the top level
		final var valuesList = getStrings(content);

		String[] values = valuesList.toArray(new String[0]);
		System.out.println("parseArrayInitializer returning: " + Arrays.toString(values));
		return values;
	}

	private static List<String> getStrings(String content) {
		List<String> valuesList = new java.util.ArrayList<>();
		int openBrackets = 0;
		StringBuilder currentValue = new StringBuilder();

		for (int i = 0; i < content.length(); i++) {
			char c = content.charAt(i);

			if (c == '[') {
				openBrackets++;
				currentValue.append(c);
			} else if (c == ']') {
				openBrackets--;
				currentValue.append(c);
			} else if (c == ',' && openBrackets == 0) {
				// This is a top-level comma, use it to split
				valuesList.add(currentValue.toString().trim());
				currentValue = new StringBuilder();
			} else {currentValue.append(c);}
		}

		// Add the last value
		if (!currentValue.isEmpty()) valuesList.add(currentValue.toString().trim());
		return valuesList;
	}
}