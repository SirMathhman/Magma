package magma.params;

import magma.core.ValueProcessor;
import magma.validation.BooleanExpressionValidator;

import java.util.Map;

/**
 * Parameters for validating if statements and if-else statements.
 * Holds all the necessary parameters for validation.
 * <p>
 * This class provides methods to:
 * - Extract conditions from if statements
 * - Extract body content from if and else blocks
 * - Check if a statement has an else block
 * - Validate if-else statement structure including required braces
 */
public record IfStatementParams(String statement, ValueProcessor valueProcessor, Map<String, String> variableTypes,
																BooleanExpressionValidator booleanValidator) {
	/**
	 * Extracts the condition from an if statement.
	 * Assumes the format "if (condition) { ... }".
	 *
	 * @return the condition expression without parentheses
	 */
	public String extractCondition() {
		int openParenIndex = statement.indexOf('(');
		int closeParenIndex = statement.indexOf(')', openParenIndex);

		if (openParenIndex == -1 || closeParenIndex == -1) {
			return null;
		}

		return statement.substring(openParenIndex + 1, closeParenIndex).trim();
	}

	/**
	 * Extracts the if body from an if statement or if-else statement.
	 * Assumes the format "if (condition) { body }" or "if (condition) { body } else { ... }".
	 *
	 * @return the if body content without curly braces
	 */
	public String extractIfBody() {
		int openBraceIndex = statement.indexOf('{');

		// If there's an else block, find the closing brace before 'else'
		int elseIndex = statement.indexOf(" else ");
		int closeBraceIndex;

		if (elseIndex != -1) {
			// Search for the closing brace before the else
			closeBraceIndex = statement.lastIndexOf('}', elseIndex);
		} else {
			// No else block, use the last closing brace
			closeBraceIndex = statement.lastIndexOf('}');
		}

		if (openBraceIndex == -1 || closeBraceIndex == -1) {
			return null;
		}

		return statement.substring(openBraceIndex + 1, closeBraceIndex).trim();
	}

	/**
	 * Extracts the else body from an if-else statement.
	 * Assumes the format "if (condition) { ... } else { body }".
	 *
	 * @return the else body content without curly braces, or null if there's no else block
	 */
	public String extractElseBody() {
		int elseIndex = statement.indexOf(" else ");
		if (elseIndex == -1) {
			return null; // No else block
		}

		int openBraceIndex = statement.indexOf('{', elseIndex);
		int closeBraceIndex = statement.lastIndexOf('}');

		if (openBraceIndex == -1 || closeBraceIndex == -1) {
			return null;
		}

		return statement.substring(openBraceIndex + 1, closeBraceIndex).trim();
	}

	/**
	 * Checks if the statement contains an else block.
	 *
	 * @return true if the statement has an else block
	 */
	public boolean hasElseBlock() {
		return statement.contains(" else ");
	}

	/**
	 * Checks if the if statement has valid structure with required parentheses and curly braces.
	 *
	 * @return true if the statement has valid structure
	 */
	public boolean hasValidStructure() {
		boolean basicIfValid = statement.trim().startsWith("if (") && statement.contains(")") && statement.contains("{") &&
													 statement.contains("}");

		// If there's no else block, just check the if structure
		if (!hasElseBlock()) {
			return basicIfValid;
		}

		// For if-else, check that the else has proper braces
		int elseIndex = statement.indexOf(" else ");
		boolean elseHasOpenBrace = statement.indexOf('{', elseIndex) != -1;
		boolean elseHasCloseBrace = statement.lastIndexOf('}') > elseIndex;

		return basicIfValid && elseHasOpenBrace && elseHasCloseBrace;
	}

	/**
	 * Backward compatibility for extractBody().
	 *
	 * @return the if body content without curly braces
	 */
	public String extractBody() {
		return extractIfBody();
	}
}