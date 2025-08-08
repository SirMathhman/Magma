package magma;

import magma.node.FunctionParts;
import magma.node.Parameter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class for handling "class fn" syntax sugar in the Magma compiler.
 * <p>
 * The "class fn" syntax:
 * <p>
 * class fn Point(x : I32, y : I32) => {}
 * <p>
 * is sugar for:
 * <p>
 * struct Point {
 * x : I32,
 * y : I32
 * }
 * <p>
 * fn Point(x : I32, y : I32): Point => {
 * let this : Point = Point { x, y };
 * return this;
 * }
 */
public class ClassFunctionHelper {

	/**
	 * Process a class function declaration in the code.
	 *
	 * @param code The complete code string
	 * @param out  The StringBuilder to append compiled code to
	 * @param i    The current position in the code
	 * @return The new position after processing the class function
	 * @throws CompileException If there is an error in the class function declaration
	 */
	public static int processClassFunction(String code, StringBuilder out, int i) throws CompileException {
		System.out.println("[DEBUG_LOG] Processing class function at position " + i + ": " + code.substring(i));

		// Skip the "class fn " prefix
		i += "class fn ".length();

		// Extract class function parts (similar to function parts)
		FunctionParts parts = extractClassFunctionParts(code, i);

		// Generate struct declaration
		generateStructDeclaration(parts, out);

		// Generate constructor function
		generateConstructorFunction(parts, out);

		System.out.println("[DEBUG_LOG] Class function processed, new position: " + parts.closeBracePos + 1);
		System.out.println("[DEBUG_LOG] Current output: " + out);

		return parts.closeBracePos + 1;
	}

	/**
	 * Extract the parts of a class function declaration.
	 *
	 * @param code The complete code string
	 * @param i    The position after "class fn "
	 * @return A FunctionParts object containing the parts of the class function
	 * @throws CompileException If there is an error in the class function declaration
	 */
	private static FunctionParts extractClassFunctionParts(String code, int i) throws CompileException {
		// Create a new FunctionParts object
		FunctionParts parts = new FunctionParts();

		// Extract the function name
		Pattern namePattern = Pattern.compile("^([a-zA-Z_][a-zA-Z0-9_]*)\\s*\\(");
		Matcher nameMatcher = namePattern.matcher(code.substring(i));

		if (!nameMatcher.find()) {
			throw new CompileException("Invalid class function name", code.substring(i));
		}

		parts.functionName = nameMatcher.group(1);
		i += nameMatcher.end();

		// Find the opening parenthesis position
		int openParenPos = i - 1;

		// Find the closing parenthesis
		int closeParenPos = CodeUtils.findMatchingParenthesis(code, openParenPos);
		if (closeParenPos < 0) {
			throw new CompileException("Missing closing parenthesis in class function declaration", code.substring(i));
		}

		parts.closeParenPos = closeParenPos;

		// Extract parameter string (without parentheses)
		String paramString = code.substring(i, closeParenPos).trim();

		// Parse parameters
		parts.parameters = parseParameters(paramString);

		// Skip to after the closing parenthesis
		i = closeParenPos + 1;

		// Check for the arrow (=>)
		while (i < code.length() && Character.isWhitespace(code.charAt(i))) i++;
		if (!code.startsWith("=>", i)) {
			throw new CompileException("Expected '=>' in class function declaration", code.substring(i));
		}
		parts.arrowPos = i;
		i += 2;

		// Skip whitespace after =>
		while (i < code.length() && Character.isWhitespace(code.charAt(i))) i++;

		// Check for the opening brace
		if (i >= code.length() || code.charAt(i) != '{') {
			throw new CompileException("Expected '{' in class function declaration", code.substring(i));
		}

		parts.openBracePos = i;

		// Find the closing brace
		int closeBracePos = CodeUtils.findMatchingBrace(code, i);
		if (closeBracePos < 0) {
			throw new CompileException("Missing closing brace in class function declaration", code.substring(i));
		}

		parts.closeBracePos = closeBracePos;

		// Extract function body (with braces)
		parts.functionBody = code.substring(i, closeBracePos + 1);

		// Set the return type to the class name
		parts.returnType = parts.functionName;

		return parts;
	}

	/**
	 * Parses function parameters from the parameter string.
	 * This is a duplicate of FunctionHelper.parseParameters with public access.
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
	 * Generate the struct declaration for a class function.
	 *
	 * @param parts The parts of the class function
	 * @param out   The StringBuilder to append the struct declaration to
	 */
	private static void generateStructDeclaration(FunctionParts parts, StringBuilder out) {
		out.append("struct ").append(parts.functionName).append(" {");

		List<Parameter> parameters = parts.parameters;
		for (int i = 0; i < parameters.size(); i++) {
			Parameter param = parameters.get(i);
			out.append(param.type()).append(" ").append(param.name()).append(";");
			// Only add space if this is not the last parameter
			if (i < parameters.size() - 1) {
				out.append(" ");
			}
		}

		out.append("}");
	}

	/**
	 * Generate the constructor function for a class function.
	 *
	 * @param parts The parts of the class function
	 * @param out   The StringBuilder to append the constructor function to
	 */
	private static void generateConstructorFunction(FunctionParts parts, StringBuilder out) {
		// Append separator between struct and function
		out.append("; ");

		// Function return type and name
		out.append(parts.functionName).append(" ").append(parts.functionName).append("(");

		// Function parameters
		List<Parameter> parameters = parts.parameters;
		for (int i = 0; i < parameters.size(); i++) {
			if (i > 0) out.append(", ");
			Parameter param = parameters.get(i);
			out.append(param.type()).append(" ").append(param.name());
		}
		out.append(") {");

		// Extract user-provided body content (without braces)
		String userBody = parts.functionBody.substring(1, parts.functionBody.length() - 1).trim();

		// Add user body content if not empty
		if (!userBody.isEmpty()) {
			out.append("   ").append(userBody).append(" ");
		} else {
			// Add three spaces after opening brace to match expected format
			out.append("   ");
		}

		// Add struct initialization and return
		out.append(parts.functionName).append(" this = {");
		for (int i = 0; i < parameters.size(); i++) {
			if (i > 0) out.append(", ");
			out.append(parameters.get(i).name());
		}
		out.append("}; return this; }");
	}
}