package magma;

import java.util.Map;

/**
 * Helper class for processing if statements in the Magma compiler.
 */
public class IfStatementHelper {

	/**
	 * Processes an if statement starting at the given position.
	 *
	 * @param code the source code
	 * @param env  the environment map
	 * @param out  the output builder
	 * @param i    the current position (points to 'i' in "if")
	 * @return the position after processing the if statement
	 * @throws CompileException if there's an error in the if statement
	 */
	public static int processIfStatement(String code, Map<String, VarInfo> env, StringBuilder out, int i)
			throws CompileException {
		// Skip "if "
		i += 3;

		// Skip whitespace
		i = ControlStructureHelper.skipWhitespace(code, i);

		// Parse the condition in parentheses
		int condStart = ControlStructureHelper.expectOpenParenthesis(code, i, "if");
		int closeParenPos = ControlStructureHelper.findMatchingParenthesis(code, i);
		String condition = ControlStructureHelper.extractCondition(code, condStart, closeParenPos);

		// Resolve condition to a Declaration
		Declaration condDecl = ControlStructureHelper.resolveConditionExpression(condition, env);

		// Find the opening brace
		i = ControlStructureHelper.expectOpenBraceAfterCondition(code, closeParenPos + 1);

		// Generate output
		appendIfStatement(out, condDecl.value());

		// Process the if block
		int afterIfBlock = Compiler.processCodeBlock(code, env, out, i);

		// Check for else statement
		return checkAndProcessElseStatement(code, env, out, afterIfBlock);
	}

	private static void appendIfStatement(StringBuilder out, String condition) {
		if (!out.isEmpty()) out.append(' ');
		out.append("if (").append(condition).append(") ");
	}

	/**
	 * Checks for and processes an else statement that may follow an if statement.
	 *
	 * @param code the source code
	 * @param env  the environment map
	 * @param out  the output builder
	 * @param i    the current position (after the if block)
	 * @return the position after processing the else statement, or the original position if no else statement
	 * @throws CompileException if there's an error in the else statement
	 */
	private static int checkAndProcessElseStatement(String code, Map<String, VarInfo> env, StringBuilder out, int i)
			throws CompileException {
		// Skip whitespace
		i = ControlStructureHelper.skipWhitespace(code, i);

		// Check if we've reached the end of the code or if there's no else statement
		if (i >= code.length() || !code.startsWith("else", i)) {
			return i;
		}

		// Skip "else"
		i += 4;

		// Skip whitespace
		i = ControlStructureHelper.skipWhitespace(code, i);

		// Ensure the else statement has an opening brace
		if (i >= code.length() || code.charAt(i) != '{') {
			throw new CompileException("Expected opening brace after 'else'",
																 code.substring(i, Math.min(i + 10, code.length())));
		}

		// Append else statement to output
		out.append(" else ");

		// Process the else block
		return Compiler.processCodeBlock(code, env, out, i);
	}
}