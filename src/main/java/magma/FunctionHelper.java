package magma;

/**
 * Helper class that handles function-related functionality for Magma compiler.
 */
public class FunctionHelper {

	/**
	 * Processes a function declaration statement.
	 *
	 * @param code The full source code
	 * @param out  The output StringBuilder
	 * @param i    The current position in the code
	 * @return The position after the function declaration
	 * @throws CompileException If there's an error processing the function declaration
	 */
	public static int processFunctionDeclaration(String code, StringBuilder out, int i) throws CompileException {
		System.out.println("[DEBUG_LOG] Processing function declaration: " + code.substring(i));

		// Check if the code starts with the function declaration pattern
		if (code.startsWith("fn ", i)) {
			// Extract the function name
			int nameStart = i + 3; // Skip "fn "
			int nameEnd = code.indexOf("(", nameStart);

			if (nameEnd == -1) {
				throw new CompileException("Invalid function declaration, missing opening parenthesis", code.substring(i));
			}

			String functionName = code.substring(nameStart, nameEnd).trim();

			// Look for the arrow and opening brace
			int arrowPos = code.indexOf("=>", nameEnd);
			if (arrowPos == -1) {
				throw new CompileException("Invalid function declaration, missing '=>'", code.substring(i));
			}

			int openBracePos = code.indexOf("{", arrowPos);
			if (openBracePos == -1) {
				throw new CompileException("Invalid function declaration, missing opening brace", code.substring(i));
			}

			// Default return type
			String returnType = "int"; // Default to int for numeric return values

			// Check for explicit return type
			String beforeArrow = code.substring(nameEnd, arrowPos).trim();
			if (beforeArrow.contains(":")) {
				// Extract the type name after the colon
				int typeStart = beforeArrow.indexOf(":") + 1;
				String typeName = beforeArrow.substring(typeStart).trim();

				// Map the Magma type to C++ type
				String mappedType = TypeHelper.mapType(typeName);
				if (mappedType != null) {
					returnType = mappedType;
				} else {
					System.out.println("[DEBUG_LOG] Unknown return type: " + typeName + ", defaulting to int");
				}
			}

			// Find the matching closing brace
			int closeBracePos = CodeUtils.findMatchingBrace(code, openBracePos);
			if (closeBracePos == -1) {
				throw new CompileException("Invalid function declaration, missing closing brace", code.substring(i));
			}

			// Extract the function body
			String functionBody = code.substring(openBracePos + 1, closeBracePos).trim();

			// Generate the C++ function declaration
			out.append(returnType).append(" ").append(functionName).append("() {");

			// If the function has a body with return statement, include it
			if (!functionBody.isEmpty()) {
				out.append(functionBody);
			}

			out.append("}");

			// Return the position after the function declaration
			return closeBracePos + 1;
		}

		// If not a function declaration
		throw new CompileException("Invalid function declaration syntax", code.substring(i));
	}
}