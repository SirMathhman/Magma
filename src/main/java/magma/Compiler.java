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
		String variablePart = extractVariablePart(input);
		int equalsIndex = variablePart.indexOf('=');
		if (equalsIndex <= 0) {
			throw new CompileException("Invalid variable declaration format");
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
	 * @throws CompileException if the type is not supported
	 */
	private String formatDeclaration(String variablePart, int equalsIndex) throws CompileException {
		String variableNamePart = variablePart.substring(0, equalsIndex).trim();
		String value = variablePart.substring(equalsIndex + 1).trim();
		
		String typeSuffix = extractTypeSuffix(value);
		String cleanValue = typeSuffix != null ? value.substring(0, value.length() - typeSuffix.length()) : value;
		VariableInfo varInfo = parseVariableInfo(variableNamePart);
		
		// Check for boolean literals when no explicit type is provided
		if ((typeSuffix == null && "I32".equals(varInfo.type())) && 
			("true".equals(value) || "false".equals(value))) {
			return "bool " + varInfo.name() + " = " + value + ";";
		}
		
		String type = typeSuffix != null ? typeSuffix : varInfo.type();
		String cType = mapTypeToCType(type);
		return cType + " " + varInfo.name() + " = " + cleanValue + ";";
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
	 * @throws CompileException if the type is not supported
	 */
	private String mapTypeToCType(String type) throws CompileException {
		String trimmedType = type.trim();
		if (trimmedType.startsWith("U")) {
			return mapUnsignedType(trimmedType);
		} else if (trimmedType.startsWith("I")) {
			return mapSignedType(trimmedType);
		} else if ("Bool".equalsIgnoreCase(trimmedType)) {
			return "bool";
		} else {
			throw new CompileException("Unsupported type: " + type);
		}
	}

	/**
	 * Maps an unsigned Magma type to its corresponding C/C++ type.
	 *
	 * @param type The unsigned Magma type
	 * @return The corresponding C/C++ type
	 * @throws CompileException if the type is not supported
	 */
	private String mapUnsignedType(String type) throws CompileException {
		if ("U8".equals(type)) return "uint8_t";
		if ("U16".equals(type)) return "uint16_t";
		if ("U32".equals(type)) return "uint32_t";
		if ("U64".equals(type)) return "uint64_t";

		throw new CompileException("Unsupported type: " + type);
	}

	/**
	 * Maps a signed Magma type to its corresponding C/C++ type.
	 *
	 * @param type The signed Magma type
	 * @return The corresponding C/C++ type
	 * @throws CompileException if the type is not supported
	 */
	private String mapSignedType(String type) throws CompileException {
		if ("I8".equals(type)) return "int8_t";
		if ("I16".equals(type)) return "int16_t";
		if ("I32".equals(type)) return "int32_t";
		if ("I64".equals(type)) return "int64_t";

		throw new CompileException("Unsupported type: " + type);
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