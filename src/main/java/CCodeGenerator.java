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
		addRequiredHeaders(cCode, magmaCode);
		cCode.append("\nint main() {\n");

		// Process each line for declarations and assignments
		Arrays.stream(magmaCode.split("\n")).forEach(line -> {
			// Process declarations (which may include multiple declarations in a single line)
			MagmaParser.processLineWithMultipleDeclarations(line, cCode);

			// Process assignments
			MagmaProcessor.processAssignment(line, cCode);
		});

		cCode.append("    return 0;\n");
		cCode.append("}");

		return cCode.toString();
	}

	/**
	 * Adds the required headers to the C code based on the types used in the Magma code.
	 *
	 * @param cCode     The StringBuilder to append the headers to
	 * @param magmaCode The Magma source code to analyze
	 */
	public static void addRequiredHeaders(StringBuilder cCode, String magmaCode) {
		cCode.append("#include <stdint.h>\n");

		// Include stdbool.h if Bool type is used in any declaration
		boolean usesBoolType = magmaCode.contains("[Bool;") || magmaCode.contains("[Bool ;");

		// Check for Bool type in array declarations

		// Check for Bool type in variable declarations
		if (magmaCode.contains(" : Bool =")) usesBoolType = true;

		// Check for boolean literals in variable declarations
		if (magmaCode.contains(" = true") || magmaCode.contains(" = false")) usesBoolType = true;

		// Check for Bool type in array elements
		if (magmaCode.contains("[true") || magmaCode.contains("[false") || magmaCode.contains(", true") ||
				magmaCode.contains(", false")) usesBoolType = true;

		// Check for comparison operators in variable declarations or assignments
		if (TypeHandler.containsComparisonOperators(magmaCode)) usesBoolType = true;

		if (usesBoolType) cCode.append("#include <stdbool.h>\n");
	}

	/**
	 * Generates C code for a variable declaration.
	 *
	 * @param cCode         The StringBuilder to append the code to
	 * @param cType         The C type of the variable
	 * @param variableName  The name of the variable
	 * @param variableValue The value of the variable
	 */
	public static void generateVariableCode(StringBuilder cCode,
																					String cType,
																					String variableName,
																					String variableValue) {
		cCode.append("    ")
				 .append(cType)
				 .append(" ")
				 .append(variableName)
				 .append(" = ")
				 .append(variableValue)
				 .append(";\n");
	}

	/**
	 * Generates C code for an array declaration.
	 *
	 * @param cCode     The StringBuilder to append the generated C code to
	 * @param cType     The C type for the array elements
	 * @param arrayDecl The ArrayDeclaration record containing array information
	 */
	public static void generateArrayCode(StringBuilder cCode, String cType, ArrayDeclaration arrayDecl) {
		cCode.append("    ")
				 .append(cType)
				 .append(" ")
				 .append(arrayDecl.name())
				 .append("[")
				 .append(arrayDecl.size())
				 .append("] = {")
				 .append(arrayDecl.elements())
				 .append("};\n");
	}

	/**
	 * Generates C code for a multi-dimensional array declaration.
	 *
	 * @param cCode     The StringBuilder to append the generated C code to
	 * @param cType     The C type for the array elements
	 * @param arrayDecl The MultiDimArrayDeclaration record containing array information
	 */
	public static void generateMultiDimArrayCode(StringBuilder cCode, String cType, MultiDimArrayDeclaration arrayDecl) {
		cCode.append("    ").append(cType).append(" ").append(arrayDecl.name());

		// Add each dimension in square brackets
		for (int dimension : arrayDecl.dimensions()) cCode.append("[").append(dimension).append("]");

		cCode.append(" = ").append(arrayDecl.elements()).append(";\n");
	}
}