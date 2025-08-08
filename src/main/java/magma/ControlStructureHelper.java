package magma;

import java.util.Map;

/**
 * Helper class with common methods for processing control structures like if and while statements.
 */
public class ControlStructureHelper {

	/**
	 * Skips whitespace characters in the given code starting from the given position.
	 *
	 * @param code     the source code
	 * @param startPos the starting position
	 * @return the position of the first non-whitespace character, or the end of the code
	 */
	public static int skipWhitespace(String code, int startPos) {
		int i = startPos;
		while (i < code.length() && Character.isWhitespace(code.charAt(i))) i++;
		return i;
	}

	/**
	 * Expects an opening parenthesis at the given position in the code.
	 *
	 * @param code        the source code
	 * @param i           the current position
	 * @param keywordName the name of the keyword before the parenthesis (for error messages)
	 * @return the position after the opening parenthesis
	 * @throws CompileException if there's no opening parenthesis at the given position
	 */
	public static int expectOpenParenthesis(String code, int i, String keywordName) throws CompileException {
		if (i >= code.length() || code.charAt(i) != '(') {
			throw new CompileException("Expected opening parenthesis after '" + keywordName + "'", code.substring(i));
		}
		return i + 1; // Return position after '('
	}

	/**
	 * Extracts a condition from the code between the given start and end positions.
	 *
	 * @param code  the source code
	 * @param start the start position (after the opening parenthesis)
	 * @param end   the end position (before the closing parenthesis)
	 * @return the extracted and trimmed condition
	 * @throws CompileException if the condition is empty
	 */
	public static String extractCondition(String code, int start, int end) throws CompileException {
		String condition = code.substring(start, end).trim();
		if (condition.isEmpty()) {
			throw new CompileException("Empty condition", code.substring(start - 1, end + 1));
		}
		return condition;
	}

	/**
	 * Resolves a condition expression to a Declaration.
	 *
	 * @param condition the condition expression
	 * @param env       the environment map
	 * @return the resolved Declaration
	 * @throws CompileException if the condition is not a boolean expression
	 */
	public static Declaration resolveConditionExpression(String condition, Map<String, VarInfo> env)
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

	/**
	 * Expects an opening brace after a condition.
	 *
	 * @param code     the source code
	 * @param startPos the position to start looking for the opening brace
	 * @return the position of the opening brace
	 * @throws CompileException if there's no opening brace at the given position
	 */
	public static int expectOpenBraceAfterCondition(String code, int startPos) throws CompileException {
		int i = skipWhitespace(code, startPos);

		if (i >= code.length() || code.charAt(i) != '{') {
			throw new CompileException("Expected opening brace after condition",
																 code.substring(startPos, Math.min(startPos + 10, code.length())));
		}

		return i;
	}

	/**
	 * Finds the position of the matching closing parenthesis.
	 *
	 * @param code    the source code
	 * @param openIdx the position of the opening parenthesis
	 * @return the position of the matching closing parenthesis
	 * @throws CompileException if there's no matching closing parenthesis
	 */
	public static int findMatchingParenthesis(String code, int openIdx) throws CompileException {
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