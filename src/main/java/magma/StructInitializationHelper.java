package magma;

import magma.node.Declaration;
import magma.node.StructInitInfo;
import magma.node.VarInfo;

import java.util.Map;

/**
 * Helper class for handling struct initialization in the Magma compiler.
 */
public class StructInitializationHelper {
	/**
	 * Process struct initialization.
	 *
	 * @param initExpr      The initialization expression (e.g., "Wrapper { 100 }")
	 * @param env           The environment with variable information
	 * @param stmt          The original statement for error reporting
	 * @param structMembers Map of struct members for validation
	 * @return The processed C initialization expression
	 * @throws CompileException If there is an error in the struct initialization
	 */
	public static Declaration processStructInitialization(String initExpr,
																												Map<String, VarInfo> env,
																												String stmt,
																												Map<String, String[]> structMembers) throws CompileException {
		// Parse struct initialization expression
		StructInitInfo initInfo = parseStructInitExpression(initExpr, stmt);
		String structName = initInfo.structName();
		String initBody = initInfo.initBody();

		// Verify struct exists
		validateStructExists(structName, stmt, structMembers);

		// Process the values
		String[] values = processInitValues(initBody, structName, env, stmt, structMembers);

		// Format as C initialization
		return new Declaration(structName, formatStructInitialization(values));
	}

	/**
	 * Parse a struct initialization expression.
	 *
	 * @param initExpr The initialization expression (e.g., "Wrapper { 100 }")
	 * @param stmt     The original statement for error reporting
	 * @return A StructInitInfo object with the parsed information
	 * @throws CompileException If the initialization expression is invalid
	 */
	public static StructInitInfo parseStructInitExpression(String initExpr, String stmt) throws CompileException {
		// Extract struct name and initialization body
		int bracePos = initExpr.indexOf('{');
		if (bracePos <= 0) {
			throw new CompileException("Invalid struct initialization", stmt);
		}

		String structName = initExpr.substring(0, bracePos).trim();

		// Find matching closing brace
		int closePos = initExpr.lastIndexOf('}');
		if (closePos <= bracePos) {
			throw new CompileException("Missing closing brace in struct initialization", stmt);
		}

		// Extract initialization values
		String initBody = initExpr.substring(bracePos + 1, closePos).trim();

		return new StructInitInfo(structName, initBody);
	}

	/**
	 * Validate that a struct exists.
	 *
	 * @param structName    The name of the struct to validate
	 * @param stmt          The original statement for error reporting
	 * @param structMembers Map of struct members for validation
	 * @throws CompileException If the struct does not exist
	 */
	public static void validateStructExists(String structName, String stmt, Map<String, String[]> structMembers)
			throws CompileException {
		if (!structMembers.containsKey(structName)) {
			throw new CompileException("Undefined struct: " + structName, stmt);
		}
	}

	/**
	 * Process initialization values for a struct.
	 *
	 * @param initBody      The initialization body (e.g., "100, 200")
	 * @param structName    The name of the struct being initialized
	 * @param env           The environment with variable information
	 * @param stmt          The original statement for error reporting
	 * @param structMembers Map of struct members for validation
	 * @return The processed values as C expressions
	 * @throws CompileException If there is an error in the initialization values
	 */
	public static String[] processInitValues(String initBody,
																					 String structName,
																					 Map<String, VarInfo> env,
																					 String stmt,
																					 Map<String, String[]> structMembers) throws CompileException {
		// Get struct members
		String[] members = structMembers.get(structName);

		// Split the initialization values
		String[] valueStrings = splitInitValues(initBody);

		// Check number of values matches number of members
		validateValueCount(valueStrings, members, stmt);

		// Process each value
		return processStructValues(valueStrings, members, env, stmt);
	}

	/**
	 * Split the initialization values.
	 *
	 * @param initBody The initialization body
	 * @return An array of value strings
	 */
	private static String[] splitInitValues(String initBody) {
		if (initBody.isEmpty()) {
			return new String[0];
		} else {
			return initBody.split(",");
		}
	}

	/**
	 * Validate that the number of values matches the number of members.
	 *
	 * @param valueStrings The value strings
	 * @param members      The member definitions
	 * @param stmt         The original statement for error reporting
	 * @throws CompileException If the counts don't match
	 */
	private static void validateValueCount(String[] valueStrings, String[] members, String stmt) throws CompileException {
		if (valueStrings.length != members.length) {
			throw new CompileException(
					"Struct initialization requires " + members.length + " values, got " + valueStrings.length, stmt);
		}
	}

	/**
	 * Process struct initialization values.
	 *
	 * @param valueStrings The value strings
	 * @param members      The member definitions
	 * @param env          The environment with variable information
	 * @param stmt         The original statement for error reporting
	 * @return The processed values
	 * @throws CompileException If a value is invalid
	 */
	private static String[] processStructValues(String[] valueStrings,
																							String[] members,
																							Map<String, VarInfo> env,
																							String stmt) throws CompileException {
		String[] processedValues = new String[valueStrings.length];

		for (int i = 0; i < valueStrings.length; i++) {
			String value = valueStrings[i].trim();

			// Get member type
			String memberDef = members[i];
			String[] parts = memberDef.split(":");
			String memberType = parts[1].trim();

			// Process value for the specific type
			processedValues[i] = StructMemberHelper.processValueForType(value, memberType, env, stmt);
		}

		return processedValues;
	}

	/**
	 * Format a struct initialization.
	 *
	 * @param values The values to include in the initialization
	 * @return The formatted initialization expression
	 */
	public static String formatStructInitialization(String[] values) {
		StringBuilder sb = new StringBuilder("{");
		for (int i = 0; i < values.length; i++) {
			sb.append(values[i]);
			if (i < values.length - 1) {
				sb.append(", ");
			}
		}
		sb.append("}");
		return sb.toString();
	}

	/**
	 * Check if a string is a valid struct reference.
	 *
	 * @param str           The string to check
	 * @param structMembers Map of struct members for validation
	 * @return True if the string is a valid struct reference, false otherwise
	 */
	public static boolean isValidStructReference(String str, Map<String, String[]> structMembers) {
		// Remove any whitespace
		str = str.trim();

		// Check if it's just a struct name
		return structMembers.containsKey(str);
	}
}