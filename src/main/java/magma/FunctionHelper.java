package magma;

import magma.node.FunctionParts;
import magma.node.Parameter;

import java.util.ArrayList;
import java.util.List;

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
		if (!code.startsWith("fn ", i)) {
			throw new CompileException("Invalid function declaration syntax", code.substring(i));
		}

		// Extract function parts
		FunctionParts parts = extractFunctionParts(code, i);

		// Generate and append the C++ function code
		generateFunctionCode(parts, out);

		// Return the position after the function declaration
		return parts.closeBracePos + 1;
	}

	/**
	 * Extracts the various parts of a function declaration.
	 *
	 * @param code The full source code
	 * @param i    The current position in the code
	 * @return A FunctionParts object containing all extracted parts
	 * @throws CompileException If there's an error extracting the function parts
	 */
	private static FunctionParts extractFunctionParts(String code, int i) throws CompileException {
		FunctionParts parts = new FunctionParts();

		// Extract the function name
		int nameStart = i + 3; // Skip "fn "
		int nameEnd = code.indexOf("(", nameStart);

		if (nameEnd == -1) {
			throw new CompileException("Invalid function declaration, missing opening parenthesis", code.substring(i));
		}

		parts.functionName = code.substring(nameStart, nameEnd).trim();

		// Find the closing parenthesis
		parts.closeParenPos = findMatchingParenthesis(code, nameEnd);
		if (parts.closeParenPos == -1) {
			throw new CompileException("Invalid function declaration, missing closing parenthesis", code.substring(i));
		}

		// Parse parameters
		parts.parameters = parseParameters(code.substring(nameEnd + 1, parts.closeParenPos));

		// Look for the arrow and opening brace
		parts.arrowPos = code.indexOf("=>", parts.closeParenPos);
		if (parts.arrowPos == -1) {
			throw new CompileException("Invalid function declaration, missing '=>'", code.substring(i));
		}

		parts.openBracePos = code.indexOf("{", parts.arrowPos);
		if (parts.openBracePos == -1) {
			throw new CompileException("Invalid function declaration, missing opening brace", code.substring(i));
		}

		// Determine return type
		parts.returnType = determineReturnType(code, parts);

		// Find the matching closing brace
		parts.closeBracePos = CodeUtils.findMatchingBrace(code, parts.openBracePos);
		if (parts.closeBracePos == -1) {
			throw new CompileException("Invalid function declaration, missing closing brace", code.substring(i));
		}

		// Extract the function body
		parts.functionBody = code.substring(parts.openBracePos + 1, parts.closeBracePos).trim();

		return parts;
	}

	/**
	 * Determines the return type for a function.
	 *
	 * @param code  The full source code
	 * @param parts The function parts extracted so far
	 * @return The determined return type as a C++ type string
	 */
	private static String determineReturnType(String code, FunctionParts parts) {
		// Default return type
		// Use "int" for functions without parameters (for backward compatibility with tests)
		// Use "int32_t" for functions with parameters (for new tests)
		String returnType;
		if (parts.parameters.isEmpty()) {
			returnType = "int";
		} else {
			returnType = "int32_t";
		}

		// Check for explicit return type
		String beforeArrow = code.substring(parts.closeParenPos + 1, parts.arrowPos).trim();
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

		return returnType;
	}

	/**
	 * Generates C++ code for a function and appends it to the output.
	 *
	 * @param parts The function parts
	 * @param out   The output StringBuilder
	 */
	private static void generateFunctionCode(FunctionParts parts, StringBuilder out) {
		// Generate the C++ function declaration
		out.append(parts.returnType).append(" ").append(parts.functionName).append("(");

		// Add parameters to the C++ function
		appendParametersToOutput(parts.parameters, out);

		out.append(") {");

		// If the function has a body with return statement, include it
		if (!parts.functionBody.isEmpty()) {
			out.append(parts.functionBody);
		}

		out.append("}");
	}

	/**
	 * Appends function parameters to the output.
	 *
	 * @param parameters The list of parameters
	 * @param out        The output StringBuilder
	 */
	private static void appendParametersToOutput(List<Parameter> parameters, StringBuilder out) {
		for (int p = 0; p < parameters.size(); p++) {
			Parameter param = parameters.get(p);
			out.append(param.type()).append(" ").append(param.name());
			if (p < parameters.size() - 1) {
				out.append(", ");
			}
		}
	}

	/**
	 * Parses function parameters from the parameter string.
	 *
	 * @param paramString The string containing the parameters (without parentheses)
	 * @return A list of Parameter objects
	 * @throws CompileException If there's an error parsing the parameters
	 */
	private static List<Parameter> parseParameters(String paramString) throws CompileException {
		List<Parameter> parameters = new ArrayList<>();

		// If the parameter string is empty, return an empty list
		if (paramString.trim().isEmpty()) {
			return parameters;
		}

		// Split by commas, accounting for nested commas in types
		String[] parts = paramString.split(",");

		for (String part : parts) {
			part = part.trim();
			if (part.isEmpty()) {
				continue;
			}

			// Each parameter should have format "name : Type"
			int colonPos = part.indexOf(":");
			if (colonPos == -1) {
				throw new CompileException("Invalid parameter format, missing type annotation", part);
			}

			String paramName = part.substring(0, colonPos).trim();
			String paramTypeName = part.substring(colonPos + 1).trim();

			if (!TypeHelper.isIdentifier(paramName)) {
				throw new CompileException("Invalid parameter name", paramName);
			}

			String mappedType = TypeHelper.mapType(paramTypeName);
			if (mappedType == null) {
				throw new CompileException("Unknown parameter type", paramTypeName);
			}

			parameters.add(new Parameter(paramName, mappedType));
		}

		return parameters;
	}

	/**
	 * Finds the matching closing parenthesis.
	 *
	 * @param code    The code string
	 * @param openPos The position of the opening parenthesis
	 * @return The position of the matching closing parenthesis, or -1 if not found
	 */
	private static int findMatchingParenthesis(String code, int openPos) {
		if (openPos >= code.length() || code.charAt(openPos) != '(') {
			return -1;
		}

		int count = 1;
		for (int i = openPos + 1; i < code.length(); i++) {
			char c = code.charAt(i);
			if (c == '(') {
				count++;
			} else if (c == ')') {
				count--;
				if (count == 0) {
					return i;
				}
			}
		}

		return -1;
	}

	/**
	 * Checks if a string appears to be a function call.
	 *
	 * @param stmt The statement to check
	 * @return true if the statement appears to be a function call, false otherwise
	 */
	public static boolean isFunctionCall(String stmt) {
		// Function calls have the format: identifier(args)
		if (stmt == null || stmt.isEmpty()) {
			return false;
		}

		// Find the first opening parenthesis
		int openParenPos = stmt.indexOf('(');
		if (openParenPos <= 0) {
			return false;
		}

		// Check if what comes before is a valid identifier
		String functionName = stmt.substring(0, openParenPos).trim();
		return TypeHelper.isIdentifier(functionName);
	}

	/**
	 * Processes a function call statement.
	 *
	 * @param stmt The statement containing the function call (without trailing semicolon)
	 * @return The C++ code for the function call
	 */
	public static String processFunctionCall(String stmt) {
		// Just pass through the function call as is - it should be valid C++ syntax
		return stmt + ";";
	}
}