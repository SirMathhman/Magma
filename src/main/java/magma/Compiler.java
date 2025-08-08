package magma;

/**
 * A simple class that processes strings but is stubbed to always throw an error.
 */
public class Compiler {
	/**
	 * Processes the input string.
	 *
	 * @param input The string to process
	 * @return An empty string if the input is empty, otherwise throws an exception
	 * @throws UnsupportedOperationException Thrown to indicate the method is not fully implemented yet
	 */
	public String process(String input) {
		if (input.isEmpty()) {
			return "";
		}

		// Handle variable declarations with pattern "let variableName = value;"
		if (input.startsWith("let ") && input.contains("=") && input.endsWith(";")) {
			return handleVariableDeclaration(input);
		}

		throw new UnsupportedOperationException("This method is not implemented yet");
	}

	/**
	 * Handles variable declarations with pattern "let variableName = value;" or "let variableName: Type = value;".
	 *
	 * @param input The variable declaration string
	 * @return The transformed variable declaration
	 */
	private String handleVariableDeclaration(String input) {
		// Extract the variable part (everything between "let " and ";")
		String variablePart = extractVariablePart(input);
		int equalsIndex = variablePart.indexOf('=');
		if (equalsIndex <= 0) {
			throw new UnsupportedOperationException("Invalid variable declaration format");
		}

		return formatDeclaration(variablePart, equalsIndex);
	}

	/**
	 * Extracts the variable part from the input string.
	 *
	 * @param input The input string
	 * @return The variable part (everything between "let " and ";")
	 */
	private String extractVariablePart(String input) {
		return input.substring(4, input.length() - 1); // Remove "let " and ";"
	}

	/**
	 * Formats the declaration using the variable part and equals index.
	 *
	 * @param variablePart The variable part of the declaration
	 * @param equalsIndex  The index of the equals sign
	 * @return The formatted declaration
	 */
	private String formatDeclaration(String variablePart, int equalsIndex) {
		String variableNamePart = variablePart.substring(0, equalsIndex).trim();
		String value = variablePart.substring(equalsIndex + 1).trim();

		// Parse variable name and type
		VariableInfo varInfo = parseVariableInfo(variableNamePart);

		// Map Magma type to C/C++ type
		String cType = mapTypeToCType(varInfo.type());

		return cType + " " + varInfo.name() + " = " + value + ";";
	}

	/**
	 * Parses variable information from the variable name part of a declaration.
	 *
	 * @param variableNamePart The part of the declaration containing the variable name and optional type
	 * @return A VariableInfo object containing the variable name and type
	 */
	private VariableInfo parseVariableInfo(String variableNamePart) {
		int colonIndex = variableNamePart.indexOf(':');
		if (colonIndex > 0) {
			return parseTypedVariable(variableNamePart, colonIndex);
		} else {
			return new VariableInfo(variableNamePart, "I32"); // Default type if not specified
		}
	}

	/**
	 * Parses a typed variable declaration.
	 *
	 * @param variableNamePart The part of the declaration containing the variable name and type
	 * @param colonIndex       The index of the colon separating the variable name and type
	 * @return A VariableInfo object containing the variable name and type
	 */
	private VariableInfo parseTypedVariable(String variableNamePart, int colonIndex) {
		String variableName = variableNamePart.substring(0, colonIndex).trim();
		String type = variableNamePart.substring(colonIndex + 1).trim();
		return new VariableInfo(variableName, type);
	}

	/**
	 * Maps a Magma type to its corresponding C/C++ type.
	 *
	 * @param type The Magma type
	 * @return The corresponding C/C++ type
	 * @throws UnsupportedOperationException if the type is not supported
	 */
	private String mapTypeToCType(String type) {
		if (type.startsWith("U")) {
			return mapUnsignedType(type);
		} else if (type.startsWith("I")) {
			return mapSignedType(type);
		} else {
			throw new UnsupportedOperationException("Unsupported type: " + type);
		}
	}

	/**
	 * Maps an unsigned Magma type to its corresponding C/C++ type.
	 *
	 * @param type The unsigned Magma type
	 * @return The corresponding C/C++ type
	 * @throws UnsupportedOperationException if the type is not supported
	 */
	private String mapUnsignedType(String type) {
		if ("U8".equals(type)) return "uint8_t";
		if ("U16".equals(type)) return "uint16_t";
		if ("U32".equals(type)) return "uint32_t";
		if ("U64".equals(type)) return "uint64_t";

		throw new UnsupportedOperationException("Unsupported type: " + type);
	}

	/**
	 * Maps a signed Magma type to its corresponding C/C++ type.
	 *
	 * @param type The signed Magma type
	 * @return The corresponding C/C++ type
	 * @throws UnsupportedOperationException if the type is not supported
	 */
	private String mapSignedType(String type) {
		if ("I8".equals(type)) return "int8_t";
		if ("I16".equals(type)) return "int16_t";
		if ("I32".equals(type)) return "int32_t";
		if ("I64".equals(type)) return "int64_t";

		throw new UnsupportedOperationException("Unsupported type: " + type);
	}
}