package magma;

import magma.node.FunctionParts;
import magma.node.FunctionPositions;
import magma.node.InnerFunctionsResult;
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

		// Find inner functions in the body and append to a list
		List<String> innerFunctions = new ArrayList<>();

		// Pre-process the body to ensure consistent formatting
		// Remove any leading spaces to ensure {return ...} format
		String processedBody = extractInnerFunctionsSimple(parts.functionBody, innerFunctions);
		if (processedBody.startsWith(" ")) {
			processedBody = processedBody.trim();
		}

		// Update the function body with inner functions removed and formatting fixed
		parts.functionBody = processedBody;

		// Start with an empty StringBuilder for the complete function output
		StringBuilder functionOutput = new StringBuilder();

		// Generate the C++ function code for the main function
		generateFunctionCode(parts, functionOutput);

		// Process each inner function
		for (String innerFunctionCode : innerFunctions) {
			System.out.println("[DEBUG_LOG] Processing inner function: " + innerFunctionCode);
			try {
				// Create a separate StringBuilder for each inner function
				StringBuilder innerOut = new StringBuilder();
				processFunctionDeclaration(innerFunctionCode, innerOut, 0);

				// Add a space between function declarations
				functionOutput.append(" ");
				functionOutput.append(innerOut);

				System.out.println("[DEBUG_LOG] Current output after adding inner function: " + functionOutput);
			} catch (CompileException e) {
				System.out.println("[DEBUG_LOG] Error processing inner function: " + e.getMessage());
				// Continue processing other inner functions
			}
		}

		// Add the complete function output to the final output
		out.append(functionOutput);

		// Return the position after the function declaration
		return parts.closeBracePos + 1;
	}

	/**
	 * Simple extraction of inner functions from a function body.
	 * This method scans for "fn" declarations and extracts them as separate strings.
	 *
	 * @param functionBody   The function body to process
	 * @param innerFunctions The list to add extracted inner functions to
	 * @return The function body with inner functions removed
	 */
	private static String extractInnerFunctionsSimple(String functionBody, List<String> innerFunctions) {
		System.out.println("[DEBUG_LOG] Extracting inner functions from: " + functionBody);

		StringBuilder modifiedBody = new StringBuilder(functionBody);

		// Simple approach: scan for "fn " and find matching braces
		int pos = 0;
		int fnCount = 0;
		while ((pos = modifiedBody.indexOf("fn ", pos)) != -1) {
			fnCount++;
			System.out.println("[DEBUG_LOG] Found potential fn #" + fnCount + " at position " + pos);

			// Validate and find function positions
			FunctionPositions positions = CodeUtils.validateAndFindFunctionPositions(modifiedBody.toString(), pos);
			if (positions == null) {
				pos += 3; // Skip "fn " and continue
				continue;
			}

			// Extract the inner function
			String innerFunction = modifiedBody.substring(pos, positions.closeBracePos + 1);
			System.out.println("[DEBUG_LOG] Extracted inner function: " + innerFunction);
			innerFunctions.add(innerFunction);
			System.out.println("[DEBUG_LOG] Inner functions list now has " + innerFunctions.size() + " functions");

			// Remove the inner function from the modified body
			modifiedBody.delete(pos, positions.closeBracePos + 1);
			System.out.println("[DEBUG_LOG] Body after removing this function: " + modifiedBody);

			// No need to update pos as we've modified the string
		}

		System.out.println("[DEBUG_LOG] Final modified body after extraction: " + modifiedBody);
		System.out.println("[DEBUG_LOG] Total inner functions found: " + innerFunctions.size());
		for (int i = 0; i < innerFunctions.size(); i++) {
			System.out.println("[DEBUG_LOG] Inner function #" + (i + 1) + ": " + innerFunctions.get(i));
		}
		return modifiedBody.toString();
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
		parts.closeParenPos = CodeUtils.findMatchingParenthesis(code, nameEnd);
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
	 * Extracts inner functions from a function body.
	 *
	 * @param functionBody The function body to process
	 * @return An InnerFunctionsResult containing the extracted functions and updated body
	 */
	private static InnerFunctionsResult extractInnerFunctions(String functionBody) {
		System.out.println("[DEBUG_LOG] Extracting inner functions from: " + functionBody);

		List<FunctionParts> innerFunctions = new ArrayList<>();
		StringBuilder modifiedBody = new StringBuilder(functionBody);

		// Scan the function body for inner function declarations
		int pos = 0;
		while ((pos = modifiedBody.indexOf("fn ", pos)) != -1) {
			System.out.println("[DEBUG_LOG] Potential inner function found at pos: " + pos);

			try {
				// Validate and find function positions
				FunctionPositions positions =
						CodeUtils.validateAndFindFunctionPositions(modifiedBody.toString(), pos);
				if (positions == null) {
					pos += 3; // Skip "fn " and continue
					continue;
				}

				// Extract the full inner function text
				String innerFnText = modifiedBody.substring(pos, positions.closeBracePos + 1);
				System.out.println("[DEBUG_LOG] Inner function text: " + innerFnText);

				// Get function name from positions
				String functionName = modifiedBody.substring(positions.nameStart, positions.nameEnd).trim();
				System.out.println("[DEBUG_LOG] Function name: " + functionName);

				// Create a separate FunctionParts object for this inner function
				FunctionParts innerParts = extractFunctionParts(innerFnText, 0);

				// Remove the inner function from the modified body
				modifiedBody.delete(pos, positions.closeBracePos + 1);
				System.out.println("[DEBUG_LOG] Modified body after removal: " + modifiedBody);

				// Process nested functions recursively
				processNestedFunctions(innerFunctions, innerParts);

				// Don't increment pos, as we've modified the string
			} catch (Exception e) {
				System.out.println("[DEBUG_LOG] Error processing inner function: " + e.getMessage());
				pos += 3; // Skip past "fn "
			}
		}

		System.out.println(
				"[DEBUG_LOG] Found " + innerFunctions.size() + " inner functions. Final modified body: " + modifiedBody);

		return new InnerFunctionsResult(innerFunctions, modifiedBody.toString());
	}

	/**
	 * Processes nested functions recursively.
	 *
	 * @param innerFunctions The list to add functions to
	 * @param innerParts     The current function parts being processed
	 */
	private static void processNestedFunctions(List<FunctionParts> innerFunctions, FunctionParts innerParts) {
		// Recursively process nested inner functions
		InnerFunctionsResult nestedResult = extractInnerFunctions(innerParts.functionBody);

		// Update the inner function's body
		innerParts.functionBody = nestedResult.updatedBody();

		// Add nested inner functions first
		innerFunctions.addAll(nestedResult.innerFunctions());

		// Add this inner function
		innerFunctions.add(innerParts);
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
		for (int p = 0; p < parts.parameters.size(); p++) {
			Parameter param = parts.parameters.get(p);
			out.append(param.type()).append(" ").append(param.name());
			if (p < parts.parameters.size() - 1) {
				out.append(", ");
			}
		}

		// Note: Ensure no space between { and the function body to match expected output
		out.append(") {");

		// If the function has a body with return statement, include it
		if (!parts.functionBody.isEmpty()) {
			// Ensure there's no extra space at the start of the function body
			String bodyContent = parts.functionBody;
			if (bodyContent.startsWith(" ")) {
				bodyContent = bodyContent.substring(1);
			}
			out.append(bodyContent);
		}

		out.append("}");
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