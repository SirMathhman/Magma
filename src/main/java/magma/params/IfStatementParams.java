package magma.params;

import magma.core.ValueProcessor;
import magma.validation.BooleanExpressionValidator;

import java.util.Map;

/**
 * Parameters for validating if statements.
 * Holds all the necessary parameters for validation.
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
	 * Extracts the body from an if statement.
	 * Assumes the format "if (condition) { body }".
	 *
	 * @return the body content without curly braces
	 */
	public String extractBody() {
		int openBraceIndex = statement.indexOf('{');
		int closeBraceIndex = statement.lastIndexOf('}');

		if (openBraceIndex == -1 || closeBraceIndex == -1) {
			return null;
		}

		return statement.substring(openBraceIndex + 1, closeBraceIndex).trim();
	}

	/**
	 * Checks if the if statement has valid structure with required parentheses and curly braces.
	 *
	 * @return true if the statement has valid structure
	 */
	public boolean hasValidStructure() {
		return statement.trim().startsWith("if (") && statement.contains(")") && statement.contains("{") &&
					 statement.contains("}");
	}
}