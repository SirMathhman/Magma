/**
 * Handler for variable declarations and assignments in Magma code.
 * This class provides functionality to handle variable declarations and assignments.
 */
public class VariableHandler {
	/**
	 * Processes a single line of Java code to extract variable declarations.
	 * Supports I8, I16, I32, I64, U8, U16, U32, U64, Bool, and U8 for characters.
	 * Also supports typeless declarations where the type is inferred (defaulting to I32 for numbers).
	 * For boolean literals (true/false), the Bool type is inferred.
	 * For char literals in single quotes (e.g., 'a'), the U8 type is inferred.
	 * Skips array declarations (both single and multi-dimensional) to avoid duplicate processing.
	 * Validates that the type is valid.
	 *
	 * @param line The line of Java code to process
	 * @return The generated C code as a string
	 * @throws IllegalArgumentException if an invalid type is specified
	 */
	public static String processVariableDeclaration(String line) {
		var trimmedLine = line.trim();
		if (!trimmedLine.startsWith("let ")) return "";

		// Skip array declarations to avoid duplicate processing
		if (ArrayHandler.isArrayDeclaration(trimmedLine)) return "";

		// Check if this is a declaration with an explicit type
		if (trimmedLine.contains(" : ")) {
			// Extract the type
			String type = trimmedLine.substring(trimmedLine.indexOf(" : ") + 3, trimmedLine.indexOf(" = "));

			// Check if this is a valid type
			java.util.Optional<TypeMapper> matchedMapper = TypeHandler.findMatchingTypeMapper(trimmedLine);
			if (matchedMapper.isEmpty())
				throw new IllegalArgumentException("Invalid type: " + type + " is not a valid type. Line: " + line);

			// Process declaration with explicit type
			return processTypeMapper(matchedMapper.get(), trimmedLine);
		}  // Process typeless declaration
		if (trimmedLine.contains(" = ")) return processTypelessDeclaration(trimmedLine);

		return "";
	}

	/**
	 * Processes a variable declaration with an explicit type.
	 *
	 * @param matchedMapper The TypeMapper for the variable type
	 * @param trimmedLine   The line containing the declaration
	 * @return The generated C code as a string
	 * @throws IllegalArgumentException if the value type is incompatible with the variable type
	 */
	public static String processTypeMapper(TypeMapper matchedMapper, String trimmedLine) {
		// Extract variable information
		String variableName = extractVariableName(trimmedLine, " : " + matchedMapper.javaType() + " = ");
		String variableValue = extractVariableValue(trimmedLine);

		// Validate type compatibility
		validateTypeCompatibility(matchedMapper.javaType(), variableValue, trimmedLine);

		// Validate value range for numeric types
		if (matchedMapper.javaType().startsWith("I") || matchedMapper.javaType().startsWith("U"))
			TypeHandler.validateValueRange(matchedMapper.javaType(), variableValue, trimmedLine);

		// Generate C code for the variable declaration
		return CCodeGenerator.generateVariableCode(matchedMapper.cType(), variableName, variableValue);
	}

	/**
	 * Validates that the value type is compatible with the variable type.
	 *
	 * @param type  The Java type of the variable
	 * @param value The value being assigned
	 * @param line  The original line for error reporting
	 * @throws IllegalArgumentException if the value type is incompatible with the variable type
	 */
	private static void validateTypeCompatibility(String type, String value, String line) {
		// Check if the value is a boolean literal
		if ((value.equals("true") || value.equals("false")) && !type.equals("Bool")) throw new IllegalArgumentException(
				"Type mismatch: Cannot assign boolean value to " + type + " variable. Line: " + line);

		// Check if the value is a character literal
		if (value.startsWith("'") && value.endsWith("'") && !type.equals("U8") && !type.equals("Char"))
			throw new IllegalArgumentException(
					"Type mismatch: Cannot assign character value to " + type + " variable. Line: " + line);

		// Check if the value is a numeric literal with a type suffix
		java.util.Optional<TypeMapper> inferredType = TypeHandler.inferTypeFromValue(value);
		// Allow numeric types to be assigned to other numeric types (range will be checked separately)
		if (inferredType.isEmpty() || inferredType.get().javaType().equals(type) ||
				inferredType.get().javaType().equals("Bool") || type.equals("Bool") ||
				inferredType.get().javaType().equals("U8") || type.equals("U8") || type.equals("Char")) return;

		if (!inferredType.get().javaType().startsWith("I") && !inferredType.get().javaType().startsWith("U") ||
				!type.startsWith("I") && !type.startsWith("U")) throw new IllegalArgumentException(
				"Type mismatch: Cannot assign " + inferredType.get().javaType() + " value to " + type + " variable. Line: " +
				line);

		// This is fine, range check will happen later
	}

	/**
	 * Processes a variable declaration without an explicit type.
	 * Infers the type based on the value:
	 * - If the value has a type suffix (e.g., 100U64), the type is inferred from the suffix.
	 * - If the value is a char literal in single quotes (e.g., 'a'), the U8 type is inferred.
	 * - If the value is a boolean literal (true/false), the Bool type is inferred.
	 * - If no type suffix is present, defaults to I32 for numbers.
	 * The type suffix is removed from the value in the generated C code.
	 * For char literals, the single quotes are preserved.
	 *
	 * @param trimmedLine The line containing the declaration
	 * @return The generated C code as a string
	 */
	public static String processTypelessDeclaration(String trimmedLine) {
		// Extract variable name and value
		String variableName = extractTypelessVariableName(trimmedLine);
		String variableValue = extractVariableValue(trimmedLine);

		// Try to infer type from value suffix
		java.util.Optional<TypeMapper> inferredMapper = TypeHandler.inferTypeFromValue(variableValue);

		// Use inferred type or default to I32
		TypeMapper typeMapper = inferredMapper.orElseGet(TypeHandler::getDefaultTypeMapper);

		// Remove type suffix from value if present
		String cleanValue = TypeHandler.removeTypeSuffix(variableValue);

		// Generate C code for the variable declaration
		return CCodeGenerator.generateVariableCode(typeMapper.cType(), variableName, cleanValue);
	}

	/**
	 * Extracts the variable name from a typeless declaration line.
	 *
	 * @param line The line containing the declaration
	 * @return The extracted variable name
	 */
	public static String extractTypelessVariableName(String line) {
		return line.substring(4, line.indexOf(" = ")).trim();
	}

	/**
	 * Extracts the variable name from a declaration line.
	 *
	 * @param line        The line containing the declaration
	 * @param typePattern The type pattern to look for (e.g., " : I32 = ")
	 * @return The extracted variable name
	 */
	public static String extractVariableName(String line, String typePattern) {
		return line.substring(4, line.indexOf(typePattern)).trim();
	}

	/**
	 * Extracts the variable value from a declaration line.
	 * Handles declarations with or without semicolons at the end.
	 *
	 * @param line The line containing the declaration
	 * @return The extracted variable value
	 */
	public static String extractVariableValue(String line) {
		int startIndex = line.indexOf(" = ") + 3;
		int endIndex = line.indexOf(";");

		// If there's no semicolon, use the end of the line
		if (endIndex == -1) endIndex = line.length();

		return line.substring(startIndex, endIndex).trim();
	}

	/**
	 * Processes a single line of Magma code to extract assignments.
	 * Supports assignments in the format "variableName = value;".
	 * An assignment is identified by a line that doesn't start with "let " but contains an equals sign.
	 * Supports multiple assignments in a single line separated by semicolons.
	 *
	 * @param line The line of Magma code to process
	 * @return The generated C code as a string
	 */
	public static String processAssignment(String line) {
		var trimmedLine = line.trim();

		// Skip if not an assignment
		if (trimmedLine.startsWith("let ") || !trimmedLine.contains("=")) return "";

		StringBuilder result = new StringBuilder();

		// Split the line by semicolons to handle multiple assignments
		String[] assignments = trimmedLine.split(";");

		for (String assignment : assignments) {
			String trimmedAssignment = assignment.trim();

			// Skip empty assignments
			if (trimmedAssignment.isEmpty()) continue;

			// Extract variable name and value
			String[] parts = trimmedAssignment.split("=", 2);
			if (parts.length != 2) continue; // Not a valid assignment

			String variableName = parts[0].trim();
			String variableValue = parts[1].trim();

			// Remove comments from the variable value
			int commentIndex = variableValue.indexOf("//");
			if (commentIndex >= 0) variableValue = variableValue.substring(0, commentIndex).trim();

			// Generate C code for the assignment
			result.append("    ").append(variableName).append(" = ").append(variableValue).append(";\n");
		}

		return result.toString();
	}
}