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
	 * @throws CompileException Thrown to indicate a compilation error
	 */
	public String process(String input) throws CompileException {
		if (input.isEmpty()) {
			return "";
		}

		// Handle variable declarations with pattern "let variableName = value;"
		if (input.startsWith("let ") && input.contains("=") && input.endsWith(";")) {
			return handleVariableDeclaration(input);
		}

		throw new CompileException("This method is not implemented yet");
	}

	/**
	 * Handles variable declarations with pattern "let variableName = value;" or "let variableName: Type = value;".
	 *
	 * @param input The variable declaration string
	 * @return The transformed variable declaration
	 * @throws CompileException if the variable declaration format is invalid
	 */
	private String handleVariableDeclaration(String input) throws CompileException {
		// Extract the variable part (everything between "let " and ";")
		String variablePart = input.substring(4, input.length() - 1);
		int equalsIndex = variablePart.indexOf('=');
		if (equalsIndex <= 0) {
			throw new CompileException("Invalid variable declaration format");
		}

		String variableNamePart = variablePart.substring(0, equalsIndex).trim();
		String value = variablePart.substring(equalsIndex + 1).trim();

		return formatDeclaration(variableNamePart, value);
	}

	/**
	 * Formats the declaration using the variable name part and value.
	 *
	 * @param variableNamePart The part containing variable name and optional type
	 * @param value            The value part of the declaration
	 * @return The formatted declaration
	 * @throws CompileException if the type is not supported
	 */
	private String formatDeclaration(String variableNamePart, String value) throws CompileException {
		String typeSuffix = extractTypeSuffix(value);
		String cleanValue = typeSuffix != null ? value.substring(0, value.length() - typeSuffix.length()) : value;
		VariableInfo varInfo = parseVariableInfo(variableNamePart);

		// Handle boolean literals with default type
		if (isBooleanValue(value) && isDefaultType(typeSuffix, varInfo.type())) {
			return "bool " + varInfo.name() + " = " + value + ";";
		}

		// Handle typed declarations
		String type = typeSuffix != null ? typeSuffix : varInfo.type();
		return mapTypeToCType(type) + " " + varInfo.name() + " = " + cleanValue + ";";
	}

	/**
	 * Checks if a value is a boolean literal.
	 */
	private boolean isBooleanValue(String value) {
		return "true".equals(value) || "false".equals(value);
	}

	/**
	 * Checks if using the default type (no suffix and I32 type).
	 */
	private boolean isDefaultType(String typeSuffix, String varType) {
		return typeSuffix == null && "I32".equals(varType);
	}

	/**
	 * Parses variable information from the variable name part of a declaration.
	 *
	 * @param variableNamePart The part of the declaration containing the variable name and optional type
	 * @return A VariableInfo object containing the variable name and type
	 */
	private VariableInfo parseVariableInfo(String variableNamePart) {
		int colonIndex = variableNamePart.indexOf(':');
		if (colonIndex <= 0) {
			return new VariableInfo(variableNamePart, "I32"); // Default type if not specified
		}

		String variableName = variableNamePart.substring(0, colonIndex).trim();
		String type = variableNamePart.substring(colonIndex + 1).trim();
		return new VariableInfo(variableName, type);
	}

	/**
	 * Maps a Magma type to its corresponding C/C++ type.
	 *
	 * @param type The Magma type
	 * @return The corresponding C/C++ type
	 * @throws CompileException if the type is not supported
	 */
	private String mapTypeToCType(String type) throws CompileException {
		String trimmedType = type.trim();

		if ("Bool".equalsIgnoreCase(trimmedType)) {
			return "bool";
		}

		return mapNumericType(trimmedType);
	}

	/**
	 * Maps numeric types (both signed and unsigned).
	 */
	private String mapNumericType(String type) throws CompileException {
		char prefix = type.charAt(0);
		String bitWidth = type.substring(1);

		if (prefix == 'U' && isValidBitWidth(bitWidth)) return "uint" + bitWidth + "_t";
		if (prefix == 'I' && isValidBitWidth(bitWidth)) return "int" + bitWidth + "_t";

		throw new CompileException("Unsupported type: " + type);
	}

	/**
	 * Checks if the bit width is valid (8, 16, 32, or 64).
	 */
	private boolean isValidBitWidth(String bitWidth) {
		return "8".equals(bitWidth) || "16".equals(bitWidth) || "32".equals(bitWidth) || "64".equals(bitWidth);
	}

	/**
	 * Extracts the type suffix from a literal value if present.
	 *
	 * @param value The literal value to check for a type suffix
	 * @return The type suffix if present, null otherwise
	 */
	private String extractTypeSuffix(String value) {
		String[] suffixes = {"U8", "U16", "U32", "U64", "I8", "I16", "I32", "I64", "Bool"};
		for (String suffix : suffixes) {
			if (value.endsWith(suffix)) return suffix;
		}
		return null;
	}
}