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

		// Get struct body (currently empty for this implementation)
		String structBody = code.substring(braceStart + 1, closeIdx).trim();

		// For now, we're only handling empty structs
		if (!structBody.isEmpty()) {
			throw new CompileException("Only empty structs are supported at this time", code.substring(braceStart));
		}

		// Output the struct declaration
		if (!out.isEmpty()) {
			out.append(' ');
		}
		out.append("struct ").append(structName).append(" {}");

		return closeIdx + 1;
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