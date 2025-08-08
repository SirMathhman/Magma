package magma;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Helper class that handles struct-related functionality for Magma compiler.
 */
public class StructHelper {
    // Keep track of struct definitions and their members
    private static final Map<String, String[]> structMembers = new HashMap<>();

	/**
	 * Process struct declaration in the code.
	 *
	 * @param code The complete code string
	 * @param out  The StringBuilder to append compiled code to
	 * @param i    The current position in the code
	 * @return The new position after processing the struct declaration
	 * @throws CompileException If there is an error in the struct declaration
	 */
	public static int processStructDeclaration(String code, StringBuilder out, int i) throws CompileException {
		// Skip "struct " keyword
		i += "struct ".length();

		// Extract struct name
		int nameStart = i;
		while (i < code.length() && isValidIdentifierChar(code.charAt(i), i == nameStart)) {
			i++;
		}

		if (nameStart == i) {
			throw new CompileException("Missing struct name", code.substring(nameStart));
		}

		String structName = code.substring(nameStart, i);

		// Skip whitespace
		while (i < code.length() && Character.isWhitespace(code.charAt(i))) {
			i++;
		}

		// Expect opening brace
		if (i >= code.length() || code.charAt(i) != '{') {
			throw new CompileException("Expected '{' after struct name", code.substring(nameStart));
		}

		// Find matching closing brace
		int braceStart = i;
		int closeIdx = findMatchingBrace(code, braceStart);

		// Get struct body
		String structBody = code.substring(braceStart + 1, closeIdx).trim();
		
		// Check for trailing comma
		if (structBody.endsWith(",")) {
			throw new CompileException("Trailing comma not allowed in struct declaration", structBody);
		}

		// Output the struct declaration
		if (!out.isEmpty()) {
			out.append(' ');
		}
		
		StringBuilder structDeclaration = new StringBuilder("struct ").append(structName).append(" {");
		
		// Register the struct type
		TypeHelper.registerStructType(structName);
		
		// Process and store struct members if the body is not empty
		if (!structBody.isEmpty()) {
			String[] members = extractStructMembers(structBody);
			structMembers.put(structName, members);
			
			// Process for output
			processStructMembers(structBody, structDeclaration);
		} else {
			structMembers.put(structName, new String[0]);
		}
		
		structDeclaration.append("}");
		out.append(structDeclaration);

		return closeIdx + 1;
	}
	
	/**
	 * Extract struct members into an array of member definitions.
	 *
	 * @param structBody The body of the struct to process
	 * @return An array of member definitions (name:type)
	 * @throws CompileException If there is an error in the struct members
	 */
	private static String[] extractStructMembers(String structBody) throws CompileException {
		// Split the body by commas
		String[] members = structBody.split(",");
		
		// Check for trailing comma
		if (members.length > 0 && members[members.length - 1].trim().isEmpty()) {
			throw new CompileException("Trailing comma not allowed in struct declaration", structBody);
		}
		
		// Clean up member strings
		for (int i = 0; i < members.length; i++) {
			members[i] = members[i].trim();
		}
		
		return members;
	}
	
	/**
	 * Process the members of a struct.
	 *
	 * @param structBody The body of the struct to process
	 * @param out The StringBuilder to append the processed members to
	 * @throws CompileException If there is an error in the struct members
	 */
	private static void processStructMembers(String structBody, StringBuilder out) throws CompileException {
		// Split the body by commas
		String[] members = structBody.split(",");
		
		// Check for trailing comma
		if (members.length > 0 && members[members.length - 1].trim().isEmpty()) {
			throw new CompileException("Trailing comma not allowed in struct declaration", structBody);
		}
		
		for (int i = 0; i < members.length; i++) {
			String member = members[i].trim();
			if (member.isEmpty()) {
				continue; // Skip empty members (shouldn't happen with proper syntax)
			}
			
			// Parse member: name : type
			String[] parts = member.split(":");
			if (parts.length != 2) {
				throw new CompileException("Invalid struct member format", member);
			}
			
			String memberName = parts[0].trim();
			String memberType = parts[1].trim();
			
			// Validate member name
			if (!isValidIdentifier(memberName)) {
				throw new CompileException("Invalid member name", member);
			}
			
			// Convert Magma type to C type
			String cType = convertType(memberType);
			
			// Add member to struct
			out.append(cType).append(" ").append(memberName).append(";");
			
			// Add space after semicolon except for the last member
			if (i < members.length - 1) {
				out.append(" ");
			}
		}
	}
	
	/**
	 * Convert a Magma type to its C equivalent.
	 *
	 * @param magmaType The Magma type to convert
	 * @return The C equivalent type
	 * @throws CompileException If the type is unknown
	 */
	private static String convertType(String magmaType) throws CompileException {
		switch (magmaType) {
			case "I8": return "int8_t";
			case "I16": return "int16_t";
			case "I32": return "int32_t";
			case "I64": return "int64_t";
			case "U8": return "uint8_t";
			case "U16": return "uint16_t";
			case "U32": return "uint32_t";
			case "U64": return "uint64_t";
			case "Bool": return "bool";
			default:
				// Check if it's a struct type
				if (structMembers.containsKey(magmaType)) {
					return magmaType;
				}
				throw new CompileException("Unknown type: " + magmaType, magmaType);
		}
	}
	
	/**
	 * Process struct initialization.
	 *
	 * @param initExpr The initialization expression (e.g., "Wrapper { 100 }")
	 * @param env The environment with variable information
	 * @param stmt The original statement for error reporting
	 * @return The processed C initialization expression
	 * @throws CompileException If there is an error in the struct initialization
	 */
	public static Declaration processStructInitialization(String initExpr, Map<String, VarInfo> env, String stmt) 
			throws CompileException {
		// Extract struct name and initialization body
		int bracePos = initExpr.indexOf('{');
		if (bracePos <= 0) {
			throw new CompileException("Invalid struct initialization", stmt);
		}
		
		String structName = initExpr.substring(0, bracePos).trim();
		
		// Verify struct exists
		if (!structMembers.containsKey(structName)) {
			throw new CompileException("Undefined struct: " + structName, stmt);
		}
		
		// Find matching closing brace
		int closePos = initExpr.lastIndexOf('}');
		if (closePos <= bracePos) {
			throw new CompileException("Missing closing brace in struct initialization", stmt);
		}
		
		// Extract initialization values
		String initBody = initExpr.substring(bracePos + 1, closePos).trim();
		
		// Process the values
		String[] values = processInitValues(initBody, structName, env, stmt);
		
		// Format as C initialization
		return new Declaration(structName, formatStructInitialization(values));
	}
	
	/**
	 * Process initialization values for a struct.
	 *
	 * @param initBody The initialization body (e.g., "100, 200")
	 * @param structName The name of the struct being initialized
	 * @param env The environment with variable information
	 * @param stmt The original statement for error reporting
	 * @return The processed values as C expressions
	 * @throws CompileException If there is an error in the initialization values
	 */
	private static String[] processInitValues(String initBody, String structName, Map<String, VarInfo> env, String stmt) 
			throws CompileException {
		// Get struct members
		String[] members = structMembers.get(structName);
		
		// Split the initialization values
		String[] valueStrings;
		if (initBody.isEmpty()) {
			valueStrings = new String[0];
		} else {
			valueStrings = initBody.split(",");
		}
		
		// Check number of values matches number of members
		if (valueStrings.length != members.length) {
			throw new CompileException(
				"Struct initialization requires " + members.length + " values, got " + valueStrings.length, stmt);
		}
		
		// Process each value
		String[] processedValues = new String[valueStrings.length];
		for (int i = 0; i < valueStrings.length; i++) {
			String value = valueStrings[i].trim();
			
			// Get member type
			String memberDef = members[i];
			String[] parts = memberDef.split(":");
			String memberType = parts[1].trim();
			
			// Resolve value according to member type
			if (memberType.equals("Bool")) {
				if (value.equals("true") || value.equals("false")) {
					processedValues[i] = value;
				} else {
					throw new CompileException("Invalid boolean value: " + value, stmt);
				}
			} else if (memberType.startsWith("I") || memberType.startsWith("U")) {
				// Numeric types
				if (value.matches("\\d+")) {
					processedValues[i] = value;
				} else if (TypeHelper.isIdentifier(value)) {
					VarInfo varInfo = env.get(value);
					if (varInfo == null) {
						throw new CompileException("Undefined variable: " + value, stmt);
					}
					processedValues[i] = value;
				} else {
					throw new CompileException("Invalid numeric value: " + value, stmt);
				}
			} else {
				// Custom struct types not supported for now
				throw new CompileException("Nested struct initialization not supported", stmt);
			}
		}
		
		return processedValues;
	}
	
	/**
	 * Format a struct initialization as a C expression.
	 *
	 * @param values The values to use in the initialization
	 * @return The formatted C initialization expression
	 */
	private static String formatStructInitialization(String[] values) {
		StringBuilder result = new StringBuilder("{");
		for (int i = 0; i < values.length; i++) {
			result.append(values[i]);
			if (i < values.length - 1) {
				result.append(", ");
			}
		}
		result.append("}");
		return result.toString();
	}
	
	/**
	 * Check if a string is a struct initialization expression.
	 *
	 * @param expr The expression to check
	 * @return true if the expression is a struct initialization, false otherwise
	 */
	public static boolean isStructInitialization(String expr) {
		// Simple check: struct name followed by opening brace
		int bracePos = expr.indexOf('{');
		if (bracePos <= 0) {
			return false;
		}
		
		String structName = expr.substring(0, bracePos).trim();
		return structMembers.containsKey(structName);
	}
	
	/**
	 * Check if a string refers to a valid struct type.
	 * 
	 * @param expr The expression to check
	 * @return true if the expression is a valid struct reference, false otherwise
	 */
	public static boolean isValidStructReference(String expr) {
		// Clean up the expression
		String clean = expr.trim();
		
		// Check if it's a direct struct name
		if (structMembers.containsKey(clean)) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Checks if a string is a valid identifier.
	 *
	 * @param identifier The string to check
	 * @return true if the string is a valid identifier, false otherwise
	 */
	private static boolean isValidIdentifier(String identifier) {
		if (identifier.isEmpty()) {
			return false;
		}
		
		char first = identifier.charAt(0);
		if (!isValidIdentifierChar(first, true)) {
			return false;
		}
		
		for (int i = 1; i < identifier.length(); i++) {
			if (!isValidIdentifierChar(identifier.charAt(i), false)) {
				return false;
			}
		}
		
		return true;
	}

	/**
	 * Checks if a character is valid in an identifier.
	 *
	 * @param c       The character to check
	 * @param isFirst Whether this is the first character of the identifier
	 * @return true if the character is valid, false otherwise
	 */
	private static boolean isValidIdentifierChar(char c, boolean isFirst) {
		if (isFirst) {
			return Character.isLetter(c) || c == '_';
		} else {
			return Character.isLetterOrDigit(c) || c == '_';
		}
	}
	

	/**
	 * Find the matching closing brace for the opening brace at the given position.
	 *
	 * @param code    The code string
	 * @param openIdx The position of the opening brace
	 * @return The position of the matching closing brace
	 * @throws CompileException If there is no matching closing brace
	 */
	private static int findMatchingBrace(String code, int openIdx) throws CompileException {
		int depth = 0;
		for (int j = openIdx; j < code.length(); j++) {
			char cj = code.charAt(j);
			if (cj == '{') depth++;
			else if (cj == '}') {
				depth--;
				if (depth == 0) return j;
			}
		}
		throw new CompileException("Unmatched '{'", code);
	}
}