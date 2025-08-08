package magma;

import magma.node.Declaration;
import magma.node.StructInitInfo;
import magma.node.VarInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Helper class for handling struct initialization in the Magma compiler.
 */
public class StructInitializationHelper {
	/**
	 * Process struct initialization.
	 *
	 * @param initExpr      The initialization expression (e.g., "Wrapper { 100 }" or "MyWrapper<I32> { 100 }")
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
		// Use an empty StringBuilder since we can't output declarations in this context
		return processStructInitialization(initExpr, env, stmt, structMembers, null);
	}
	
	/**
	 * Process struct initialization with an output buffer for declarations.
	 *
	 * @param initExpr      The initialization expression (e.g., "Wrapper { 100 }" or "MyWrapper<I32> { 100 }")
	 * @param env           The environment with variable information
	 * @param stmt          The original statement for error reporting
	 * @param structMembers Map of struct members for validation
	 * @param out           The StringBuilder to append any generated declarations to, or null if not needed
	 * @return The processed C initialization expression
	 * @throws CompileException If there is an error in the struct initialization
	 */
	public static Declaration processStructInitialization(String initExpr,
																												Map<String, VarInfo> env,
																												String stmt,
																												Map<String, String[]> structMembers,
																												StringBuilder out) throws CompileException {
		// Parse struct initialization expression
		StructInitInfo initInfo = parseStructInitExpression(initExpr, stmt);
		String structName = initInfo.structName();
		String initBody = initInfo.initBody();

		// Check if this is a generic struct reference
		String concreteStructName = structName;
		if (structName.contains("<")) {
			// Extract the base struct name and type arguments
			int angleBracketPos = structName.indexOf('<');
			String baseStructName = structName.substring(0, angleBracketPos).trim();
			
			// Check if this is a registered generic struct
			if (!GenericTypeHelper.isGenericStruct(baseStructName)) {
				throw new CompileException("Unknown generic struct: " + baseStructName, stmt);
			}
			
			// Parse the type arguments
			List<String> typeArgs = GenericTypeHelper.parseTypeArguments(structName);
			
			// Create the concrete struct name
			concreteStructName = GenericTypeHelper.createConcreteStructName(baseStructName, typeArgs);
			
			// Check if this concrete struct has already been instantiated
			if (!structMembers.containsKey(concreteStructName)) {
				// If we have an output buffer, instantiate the concrete struct
				if (out != null) {
					concreteStructName = StructHelper.instantiateGenericStruct(baseStructName, typeArgs, out);
				} else {
					// No output buffer, can't instantiate
					throw new CompileException("Concrete struct " + concreteStructName + 
						" must be declared before use", stmt);
				}
			}
		} else {
			// Regular struct, verify it exists
			if (!structMembers.containsKey(structName)) {
				throw new CompileException("Undefined struct: " + structName, stmt);
			}
		}

		// Process the values
		String[] values = processInitValues(initBody, concreteStructName, env, stmt, structMembers);

		// Format as C initialization
		return new Declaration(concreteStructName, formatStructInitialization(values));
	}

	/**
	 * Parse a struct initialization expression.
	 *
	 * @param initExpr The initialization expression (e.g., "Wrapper { 100 }" or "MyWrapper<I32> { 100 }")
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
		
		// Handle generic struct references
		// We leave the angle brackets in the struct name for now - they'll be handled by validateStructExists
		
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
	 * Validate that a struct exists. For generic structs, instantiate a concrete version.
	 *
	 * @param structName    The name of the struct to validate (may include generic type arguments)
	 * @param stmt          The original statement for error reporting
	 * @param structMembers Map of struct members for validation
	 * @return The concrete struct name (may be different from input for generic structs)
	 * @throws CompileException If the struct does not exist
	 */
	public static String validateStructExists(String structName, String stmt, Map<String, String[]> structMembers)
			throws CompileException {
		// Check if this is a generic struct reference (contains angle brackets)
		if (structName.contains("<")) {
			try {
				// Extract the base struct name and type arguments
				int angleBracketPos = structName.indexOf('<');
				String baseStructName = structName.substring(0, angleBracketPos).trim();
				
				// Check if this is a registered generic struct
				if (!GenericTypeHelper.isGenericStruct(baseStructName)) {
					throw new CompileException("Unknown generic struct: " + baseStructName, stmt);
				}
				
				// Parse the type arguments
				List<String> typeArgs = GenericTypeHelper.parseTypeArguments(structName);
				
				// Create the concrete struct name
				String concreteStructName = GenericTypeHelper.createConcreteStructName(baseStructName, typeArgs);
				
				// Check if this concrete struct has already been instantiated
				if (!structMembers.containsKey(concreteStructName)) {
					// For initialization, we don't have an output buffer to append the declaration to
					// We'll handle this in processStructInitialization
					throw new CompileException("Concrete struct " + concreteStructName + 
						" must be declared before use", stmt);
				}
				
				return concreteStructName;
			} catch (Exception e) {
				throw new CompileException("Invalid generic struct reference: " + structName, stmt);
			}
		} else {
			// Regular, non-generic struct
			if (!structMembers.containsKey(structName)) {
				throw new CompileException("Undefined struct: " + structName, stmt);
			}
			return structName;
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