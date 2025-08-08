package magma;

import java.util.Map;

/**
 * Helper class for processing while statements in the Magma compiler.
 */
public class WhileStatementHelper {

	/**
	 * Processes a while statement starting at the given position.
	 *
	 * @param code the source code
	 * @param env  the environment map
	 * @param out  the output builder
	 * @param i    the current position (points to 'w' in "while")
	 * @return the position after processing the while statement
	 * @throws CompileException if there's an error in the while statement
	 */
	public static int processWhileStatement(String code, Map<String, VarInfo> env, StringBuilder out, int i)
			throws CompileException {
		// Skip "while "
		i += 6;

		// Skip whitespace
		i = ControlStructureHelper.skipWhitespace(code, i);

		// Parse the condition in parentheses
		int condStart = ControlStructureHelper.expectOpenParenthesis(code, i, "while");
		int closeParenPos = ControlStructureHelper.findMatchingParenthesis(code, i);
		String condition = ControlStructureHelper.extractCondition(code, condStart, closeParenPos);

		// Resolve condition to a Declaration
		Declaration condDecl = ControlStructureHelper.resolveConditionExpression(condition, env);

		// Find the opening brace
		i = ControlStructureHelper.expectOpenBraceAfterCondition(code, closeParenPos + 1);

		// Generate output
		appendWhileStatement(out, condDecl.value());

		// Process the while block
		return Compiler.processCodeBlock(code, env, out, i);
	}

	private static void appendWhileStatement(StringBuilder out, String condition) {
		if (!out.isEmpty()) out.append(' ');
		out.append("while (").append(condition).append(") ");
	}
}