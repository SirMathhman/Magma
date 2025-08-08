package magma;

import magma.node.Declaration;
import magma.node.StructIdentifier;
import magma.node.StructMember;
import magma.node.VarInfo;

import java.util.HashMap;
import java.util.Map;

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

		// Extract and validate struct name
		StructIdentifier parsedStructName = parseStructName(code, i);
		String structName = parsedStructName.name();
		i = parsedStructName.position();

		// Find and validate struct body
		int braceStart = i;
		int closeIdx = findAndValidateStructBraces(code, braceStart, structName);
		String structBody = code.substring(braceStart + 1, closeIdx).trim();

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

		structDeclaration.append("};");
		out.append(structDeclaration);

		return closeIdx + 1;
	}

	/**
	 * Parse and validate a struct name from the code.
	 *
	 * @param code     The complete code string
	 * @param startPos The starting position in the code
	 * @return A StructIdentifier containing the struct name and the new position
	 * @throws CompileException If the struct name is missing or invalid
	 */
	private static StructIdentifier parseStructName(String code, int startPos) throws CompileException {
		int i = startPos;
		int nameStart = i;

		// Find the end of the identifier
		while (i < code.length() && (Character.isLetterOrDigit(code.charAt(i)) || code.charAt(i) == '_')) {
			i++;
		}

		if (nameStart == i) {
			throw new CompileException("Missing struct name", code.substring(nameStart));
		}

		String structName = code.substring(nameStart, i);

		// Validate the struct name
		if (!TypeHelper.isIdentifier(structName)) {
			throw new CompileException("Invalid struct name", structName);
		}

		// Skip whitespace
		while (i < code.length() && Character.isWhitespace(code.charAt(i))) {
			i++;
		}

		return new StructIdentifier(structName, i);
	}

	/**
	 * Find and validate the braces for a struct declaration.
	 *
	 * @param code       The complete code string
	 * @param bracePos   The position where the opening brace should be
	 * @param structName The name of the struct (for error reporting)
	 * @return The position of the closing brace
	 * @throws CompileException If the braces are missing or invalid
	 */
	private static int findAndValidateStructBraces(String code, int bracePos, String structName) throws CompileException {
		// Expect opening brace
		if (bracePos >= code.length() || code.charAt(bracePos) != '{') {
			throw new CompileException("Expected '{' after struct name", structName);
		}

		// Find matching closing brace
		int closeIdx = CodeUtils.findMatchingBrace(code, bracePos);

		// Get struct body
		String structBody = code.substring(bracePos + 1, closeIdx).trim();

		// Check for trailing comma
		if (structBody.endsWith(",")) {
			throw new CompileException("Trailing comma not allowed in struct declaration", structBody);
		}

		return closeIdx;
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
	 * @param out        The StringBuilder to append the processed members to
	 * @throws CompileException If there is an error in the struct members
	 */
	private static void processStructMembers(String structBody, StringBuilder out) throws CompileException {
		// Split the body by commas
		String[] members = structBody.split(",");

		// Check for trailing comma
		StructMemberHelper.validateNoTrailingComma(members, structBody);

		for (int i = 0; i < members.length; i++) {
			String member = members[i].trim();
			if (member.isEmpty()) {
				continue; // Skip empty members (shouldn't happen with proper syntax)
			}

			// Process each member
			StructMember parsedMember = StructMemberHelper.parseStructMember(member, structMembers);

			// Add member to struct
			out.append(parsedMember.cType()).append(" ").append(parsedMember.name()).append(";");

			// Add space after semicolon except for the last member
			if (i < members.length - 1) {
				out.append(" ");
			}
		}
	}

	// Methods moved to StructMemberHelper class

	/**
	 * Convert a Magma type to its C equivalent.
	 *
	 * @param magmaType The Magma type to convert
	 * @return The C equivalent type
	 * @throws CompileException If the type is unknown
	 */
	private static String convertType(String magmaType) throws CompileException {
		return StructMemberHelper.convertType(magmaType, structMembers);
	}

	/**
	 * Process struct initialization.
	 *
	 * @param initExpr The initialization expression (e.g., "Wrapper { 100 }")
	 * @param env      The environment with variable information
	 * @param stmt     The original statement for error reporting
	 * @return The processed C initialization expression
	 * @throws CompileException If there is an error in the struct initialization
	 */
	public static Declaration processStructInitialization(String initExpr, Map<String, VarInfo> env, String stmt)
			throws CompileException {
		return StructInitializationHelper.processStructInitialization(initExpr, env, stmt, structMembers);
	}

	// Methods moved to StructInitializationHelper class

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
		return StructInitializationHelper.isValidStructReference(structName, structMembers);
	}

	/**
	 * Check if a string refers to a valid struct type.
	 *
	 * @param expr The expression to check
	 * @return true if the expression is a valid struct reference, false otherwise
	 */
	public static boolean isValidStructReference(String expr) {
		return StructInitializationHelper.isValidStructReference(expr, structMembers);
	}


}