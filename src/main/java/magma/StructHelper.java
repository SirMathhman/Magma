package magma;

/**
 * Helper class that handles struct-related functionality for Magma compiler.
 */
public class StructHelper {

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
		
		// Process struct members if the body is not empty
		if (!structBody.isEmpty()) {
			processStructMembers(structBody, structDeclaration);
		}
		
		structDeclaration.append("}");
		out.append(structDeclaration);

		return closeIdx + 1;
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
				throw new CompileException("Unknown type: " + magmaType, magmaType);
		}
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