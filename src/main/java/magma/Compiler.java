package magma;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple class that processes strings but is stubbed to always throw an error.
 */
public class Compiler {
	// Map to store declared variables (name -> VariableInfo)
	private final Map<String, VariableInfo> declaredVariables = new HashMap<>();

	/**
	 * Processes the input string.
	 *
	 * @param input The string to process
	 * @return An empty string if the input is empty, otherwise the processed result
	 * @throws CompileException Thrown to indicate a compilation error
	 */
	public String process(String input) throws CompileException {
		if (input.isEmpty()) {
			return "";
		}

		// Handle code blocks enclosed in curly braces
		if (input.startsWith("{") && input.endsWith("}")) {
			return processCodeBlock(input);
		}

		// Handle variable declarations with pattern "let variableName = value;"
		if (input.startsWith("let ") && input.contains("=") && input.endsWith(";")) {
			return handleVariableDeclaration(input);
		}
		
		// Handle variable reassignment with pattern "variableName = value;"
		if (input.contains("=") && input.endsWith(";") && !input.startsWith("let ")) {
			return handleVariableReassignment(input);
		}

		throw new CompileException("This method is not implemented yet");
	}

	/**
	 * Processes a code block enclosed in curly braces.
	 *
	 * @param input The code block string (including the curly braces)
	 * @return The processed code block
	 * @throws CompileException if there's an error processing the code block
	 */
	private String processCodeBlock(String input) throws CompileException {
		// Remove the curly braces
		String blockContent = input.substring(1, input.length() - 1).trim();
		if (blockContent.isEmpty()) {
			return "{\n}";
		}

		// Create a backup of the current variable state
		Map<String, VariableInfo> outerScopeVariables = new HashMap<>(declaredVariables);

		// Split the block content into individual statements
		List<String> statements = splitIntoStatements(blockContent);
		List<String> processedStatements = new ArrayList<>();

		try {
			// Process each statement
			for (String statement : statements) {
				String processedStatement = process(statement);
				processedStatements.add(processedStatement);
			}

			// Combine the processed statements into a single string
			StringBuilder result = new StringBuilder("{\n");
			for (String processedStatement : processedStatements) {
				result.append("    ").append(processedStatement).append("\n");
			}
			result.append("}");

			return result.toString();
		} finally {
			// Always restore the outer scope variables (discard variables declared inside the block)
			declaredVariables.clear();
			declaredVariables.putAll(outerScopeVariables);
		}
	}

	/**
	 * Splits a string into individual statements separated by semicolons.
	 *
	 * @param input The string to split
	 * @return A list of individual statements
	 */
	private List<String> splitIntoStatements(String input) {
		List<String> statements = new ArrayList<>();
		int start = 0;

		for (int i = 0; i < input.length(); i++) {
			if (input.charAt(i) == ';') {
				String statement = input.substring(start, i + 1).trim();
				if (!statement.isEmpty()) {
					statements.add(statement);
				}
				start = i + 1;
			}
		}

		// Add the last statement if it doesn't end with a semicolon
		String lastStatement = input.substring(start).trim();
		if (!lastStatement.isEmpty()) {
			statements.add(lastStatement);
		}

		return statements;
	}

	/**
	 * Handles variable declarations with pattern "let variableName = value;" or "let variableName: Type = value;".
	 *
	 * @param input The variable declaration string
	 * @return The transformed variable declaration
	 * @throws CompileException if the variable declaration format is invalid
	 */
	private String handleVariableDeclaration(String input) throws CompileException {
		// Extract parts of the variable declaration
		String[] parts = extractDeclarationParts(input);
		String variableNamePart = parts[0];
		String value = parts[1];

		// Parse and store variable info
		VariableInfo varInfo = parseVariableInfo(variableNamePart);
		declaredVariables.put(varInfo.name(), varInfo);

		return formatDeclaration(variableNamePart, value);
	}

	/**
	 * Extracts the variable name part and value from a variable declaration.
	 *
	 * @param input The variable declaration string
	 * @return Array with [variableNamePart, value]
	 * @throws CompileException if the format is invalid
	 */
	private String[] extractDeclarationParts(String input) throws CompileException {
		String variablePart = input.substring(4, input.length() - 1);
		int equalsIndex = variablePart.indexOf('=');
		if (equalsIndex <= 0) {
			throw new CompileException("Invalid variable declaration format");
		}

		String variableNamePart = variablePart.substring(0, equalsIndex).trim();
		String value = variablePart.substring(equalsIndex + 1).trim();

		return new String[]{variableNamePart, value};
	}

	/**
	 * Formats the declaration using the variable name part and value.
	 *
	 * @param variableNamePart The part containing variable name and optional type
	 * @param value            The value part of the declaration
	 * @return The formatted declaration
	 * @throws CompileException if the type is not supported or if a referenced variable is not found
	 */
	private String formatDeclaration(String variableNamePart, String value) throws CompileException {
		VariableInfo varInfo = parseVariableInfo(variableNamePart);
		String typeSuffix = extractTypeSuffix(value);

		// Check if the value is a variable reference and verify it exists
		if (!isBooleanValue(value) && typeSuffix == null && !value.matches("\\d+") &&
				!declaredVariables.containsKey(value)) {
			throw new CompileException("Undefined variable: " + value);
		}

		// Handle boolean literals
		if (isBooleanValue(value) && isDefaultType(typeSuffix, varInfo.type()))
			return "bool " + varInfo.name() + " = " + value + ";";

		// Determine type and value
		String type;
		if (declaredVariables.containsKey(value)) {
			type = varInfo.type();
		} else {
			if (typeSuffix != null) type = typeSuffix;
			else type = varInfo.type();
		}

		String cleanValue;
		if (typeSuffix != null && !declaredVariables.containsKey(value))
			cleanValue = value.substring(0, value.length() - typeSuffix.length());
		else cleanValue = value;

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
	 * @return A VariableInfo object containing the variable name, type, and mutability
	 */
	private VariableInfo parseVariableInfo(String variableNamePart) {
		// Check if the variable is mutable
		boolean isMutable = false;
		String namePart = variableNamePart;
		
		if (variableNamePart.startsWith("mut ")) {
			isMutable = true;
			namePart = variableNamePart.substring(4).trim(); // Remove "mut " prefix
		}
		
		int colonIndex = namePart.indexOf(':');
		if (colonIndex <= 0) {
			return new VariableInfo(namePart, "I32", isMutable); // Default type if not specified
		}

		String variableName = namePart.substring(0, colonIndex).trim();
		String type = namePart.substring(colonIndex + 1).trim();
		return new VariableInfo(variableName, type, isMutable);
	}

	/**
	 * Handles variable reassignment with pattern "variableName = value;".
	 *
	 * @param input The variable reassignment string
	 * @return The transformed variable reassignment
	 * @throws CompileException if the variable is not declared, not mutable, or the format is invalid
	 */
	private String handleVariableReassignment(String input) throws CompileException {
		// Remove the trailing semicolon
		String statement = input.substring(0, input.length() - 1).trim();
		
		// Split the statement into variable name and value
		int equalsIndex = statement.indexOf('=');
		if (equalsIndex <= 0) {
			throw new CompileException("Invalid assignment format");
		}
		
		String variableName = statement.substring(0, equalsIndex).trim();
		String value = statement.substring(equalsIndex + 1).trim();
		
		// Check if the variable exists
		if (!declaredVariables.containsKey(variableName)) {
			throw new CompileException("Undefined variable: " + variableName);
		}
		
		// Check if the variable is mutable
		VariableInfo varInfo = declaredVariables.get(variableName);
		if (!varInfo.mutable()) {
			throw new CompileException("Cannot reassign to immutable variable: " + variableName);
		}
		
		// Check if the value is a variable reference and verify it exists
		String typeSuffix = extractTypeSuffix(value);
		if (!isBooleanValue(value) && typeSuffix == null && !value.matches("\\d+") &&
				!declaredVariables.containsKey(value)) {
			throw new CompileException("Undefined variable: " + value);
		}
		
		// Format the assignment
		String cleanValue;
		if (typeSuffix != null && !declaredVariables.containsKey(value)) {
			cleanValue = value.substring(0, value.length() - typeSuffix.length());
		} else {
			cleanValue = value;
		}
		
		return variableName + " = " + cleanValue + ";";
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
		if ("Bool".equalsIgnoreCase(trimmedType)) return "bool";

		// Handle numeric types (both signed and unsigned)
		char prefix = trimmedType.charAt(0);
		String bitWidth = trimmedType.substring(1);

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