package magma.validation;

import magma.core.CompileException;
import magma.params.IfStatementParams;

/**
 * Validator for if statements.
 * This class handles validation of if statements, ensuring they have correct structure
 * and that the condition expression is a valid boolean.
 */
public class IfStatementValidator {
	private final IfStatementParams params;

	/**
	 * Creates a new IfStatementValidator.
	 *
	 * @param params parameters for if statement validation
	 */
	public IfStatementValidator(IfStatementParams params) {
		this.params = params;
	}

	/**
	 * Validates an if statement.
	 * Checks that:
	 * - The statement has required parentheses and curly braces
	 * - The condition is a valid boolean expression
	 *
	 * @return the validated if statement
	 * @throws CompileException if the if statement is invalid
	 */
	public String validateIfStatement() {
		if (!params.hasValidStructure()) {
			throw new CompileException("Invalid if statement syntax. Required format: if (condition) { ... }");
		}

		// Extract condition
		String condition = params.extractCondition();
		// Extract body
		String body = params.extractBody();

		// Check that the condition is a boolean expression
		validateCondition(condition);

		// Process the body
		String processedBody = processBody(body);

		// Rebuild the if statement
		return "if (" + condition + ") { " + processedBody + " }";
	}

	/**
	 * Validates that the condition is a boolean expression.
	 *
	 * @param condition the condition to validate
	 * @throws CompileException if the condition is not a valid boolean expression
	 */
	private void validateCondition(String condition) {
		// If it's a boolean literal (true/false), it's valid
		if ("true".equals(condition) || "false".equals(condition)) {
			return;
		}

		// If it's a variable, check that it's a Bool
		String variableType = params.variableTypes().get(condition);
		if (variableType != null) {
			if (!"Bool".equals(variableType)) {
				throw new CompileException("Condition must be a boolean expression, but got " + variableType);
			}
			return;
		}

		// For boolean expressions, delegate to the boolean validator
		params.booleanValidator().checkLogicalOperations(condition);
	}

	/**
	 * Processes the body of the if statement.
	 *
	 * @param body the body content to process
	 * @return the processed body
	 */
	private String processBody(String body) {
		// If the body is empty, return it as is
		if (body == null || body.trim().isEmpty()) {
			return "";
		}

		return body;
	}
}