package magma;

import magma.node.VarInfo;

import java.util.Map;

/**
 * Helper class that handles function-related functionality for Magma compiler.
 */
public class FunctionHelper {

	/**
	 * Processes a function declaration statement.
	 *
	 * @param code The full source code
	 * @param env  The environment map of variables
	 * @param out  The output StringBuilder
	 * @param i    The current position in the code
	 * @return The position after the function declaration
	 * @throws CompileException If there's an error processing the function declaration
	 */
	public static int processFunctionDeclaration(String code, Map<String, VarInfo> env, StringBuilder out, int i)
			throws CompileException {
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
			
			// Check if the rest of the syntax matches the expected pattern
			String expectedSuffix = "() : Void => {}";
			int suffixStart = nameEnd;
			
			if (!code.startsWith(expectedSuffix, suffixStart)) {
				throw new CompileException("Only functions with signature '() : Void => {}' are supported", code.substring(i));
			}
			
			// Generate the C++ function declaration
			out.append("void ").append(functionName).append("() {}");
			
			// Return the position after the function declaration
			return i + "fn ".length() + functionName.length() + expectedSuffix.length();
		}
		
		// If not a function declaration
		throw new CompileException("Invalid function declaration syntax", code.substring(i));
	}
}