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
		i = skipWhitespace(code, i);

		// Parse the condition in parentheses
		int condStart = expectOpenParenthesis(code, i);
		int closeParenPos = findMatchingParenthesis(code, i);
		String condition = extractCondition(code, condStart, closeParenPos);

		// Resolve condition to a Declaration
		Declaration condDecl = resolveConditionExpression(condition, env);

		// Find the opening brace
		i = expectOpenBraceAfterCondition(code, closeParenPos + 1);

		// Generate output
		appendIfStatement(out, condDecl.value());

		// Process the block
		return Compiler.processCodeBlock(code, env, out, i);
	}

	private static int skipWhitespace(String code, int startPos) {
		int i = startPos;
		while (i < code.length() && Character.isWhitespace(code.charAt(i))) i++;
		return i;
	}

	private static int expectOpenParenthesis(String code, int i) throws CompileException {
		if (i >= code.length() || code.charAt(i) != '(') {
			throw new CompileException("Expected opening parenthesis after 'if'", code.substring(i));
		}
		return i + 1; // Return position after '('
	}

	private static String extractCondition(String code, int start, int end) throws CompileException {
		String condition = code.substring(start, end).trim();
		if (condition.isEmpty()) {
			throw new CompileException("Empty condition in if statement", code.substring(start - 1, end + 1));
		}
		return condition;
	}

	private static Declaration resolveConditionExpression(String condition, Map<String, VarInfo> env)
			throws CompileException {
		// Try comparison first
		Declaration cmp = ValueResolver.tryParseComparison(condition, env, condition);
		if (cmp != null) {
			return cmp;
		}

		// Otherwise try as simple value
		Declaration result = ValueResolver.resolveSimpleValue(condition, env, condition);
		if (!"bool".equals(result.cType())) {
			throw new CompileException("Condition must be a boolean expression", condition);
		}

		return result;
	}

	private static int expectOpenBraceAfterCondition(String code, int startPos) throws CompileException {
		int i = skipWhitespace(code, startPos);

		if (i >= code.length() || code.charAt(i) != '{') {
			throw new CompileException("Expected opening brace after if condition",
																 code.substring(startPos, Math.min(startPos + 10, code.length())));
		}

		return i;
	}

	private static void appendIfStatement(StringBuilder out, String condition) {
		if (!out.isEmpty()) out.append(' ');
		out.append("if (").append(condition).append(") ");
	}

	private static int findMatchingParenthesis(String code, int openIdx) throws CompileException {
		int depth = 0;
		for (int j = openIdx; j < code.length(); j++) {
			char cj = code.charAt(j);
			if (cj == '(') depth++;
			else if (cj == ')') {
				depth--;
				if (depth == 0) return j;
			}
		}
		throw new CompileException("Unmatched '('", code);
	}
}