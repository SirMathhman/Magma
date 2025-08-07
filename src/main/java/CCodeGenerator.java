import java.util.Arrays;

/**
 * Generator for C code from Magma code.
 * This class provides functionality to generate C code from parsed Magma code.
 */
public class CCodeGenerator {
	/**
	 * Generates C code for a program with declarations (array or variable) and assignments.
	 * Handles array declarations in the format "let x : [Type; Size] = [val1, val2, ...];"
	 * and variable declarations in the format "let x : Type = value;" or "let x = value;".
	 * Also handles assignments in the format "x = value;".
	 * Supports all basic types (I8-I64, U8-U64, Bool, U8 for characters).
	 * Includes appropriate headers (stdint.h for integer types, stdbool.h for Bool type).
	 * Supports multiple declarations in a single line, separated by semicolons.
	 *
	 * @param magmaCode The Magma source code containing declarations and assignments
	 * @return C code for a program with declarations and assignments
	 */
	public static String generateDeclarationCCode(String magmaCode) {
		StringBuilder cCode = new StringBuilder();
		cCode.append(generateRequiredHeaders(magmaCode));
		cCode.append("\nint main() {\n");

		// Process each line for declarations and assignments
		Arrays.stream(magmaCode.split("\n")).forEach(line -> {
			// Process declarations (which may include multiple declarations in a single line)
			String declarations = MagmaParser.processLineWithMultipleDeclarations(line);
			if (!declarations.isEmpty()) cCode.append(declarations);

			// Process assignments
			String assignments = MagmaProcessor.processAssignment(line);
			if (!assignments.isEmpty()) cCode.append(assignments);
		});

		cCode.append("    return 0;\n");
		cCode.append("}");

		return cCode.toString();
	}

	/**
	 * Generates the required headers for the C code based on the types used in the Magma code.
	 *
	 * @param magmaCode The Magma source code to analyze
	 * @return The headers as a string
	 */
	public static String generateRequiredHeaders(String magmaCode) {
		StringBuilder headers = new StringBuilder();
		headers.append("#include <stdint.h>\n");

		// Include stdbool.h if Bool type is used in any declaration
		boolean usesBoolType = magmaCode.contains("[Bool;") || magmaCode.contains("[Bool ;");

		// Check for Bool type in variable declarations
		if (magmaCode.contains(" : Bool =")) usesBoolType = true;

		// Check for boolean literals in variable declarations
		if (magmaCode.contains(" = true") || magmaCode.contains(" = false")) usesBoolType = true;

		// Check for Bool type in array elements
		if (magmaCode.contains("[true") || magmaCode.contains("[false") || magmaCode.contains(", true") ||
				magmaCode.contains(", false")) usesBoolType = true;

		// Check for comparison operators in variable declarations or assignments
		if (TypeHandler.containsComparisonOperators(magmaCode)) usesBoolType = true;

		if (usesBoolType) headers.append("#include <stdbool.h>\n");

		return headers.toString();
	}

	/**
	 * Generates C code for a variable declaration.
	 *
	 * @param cType         The C type of the variable
	 * @param variableName  The name of the variable
	 * @param variableValue The value of the variable
	 * @return The generated C code as a string
	 */
	public static String generateVariableCode(String cType, String variableName, String variableValue) {
		return "    " + cType + " " + variableName + " = " + variableValue + ";\n";
	}
}