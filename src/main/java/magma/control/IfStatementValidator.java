package magma.control;

import magma.core.CompileException;

/**
 * Validator for if statements and if-else statements.
 * This class handles validation of if statements and if-else statements, ensuring they have correct structure
 * and that the condition expression is a valid boolean.
 * <p>
 * Key features:
 * - Validates structure of both if statements and if-else statements
 * - Enforces required braces for both if and else blocks
 * - Validates that conditions are boolean expressions
 * - Processes the body content of both if and else blocks
 * - Ensures proper formatting with semicolons
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
	 * Validates an if statement or if-else statement.
	 * Checks that:
	 * - The statement has required parentheses and curly braces
	 * - The condition is a valid boolean expression
	 * - If there's an else block, it has the required curly braces
	 *
	 * @return the validated if statement or if-else statement
	 * @throws CompileException if the statement is invalid
	 */
	public String validateIfStatement() {
		if (!params.hasValidStructure()) {
			if (params.hasElseBlock()) {
				throw new CompileException(
						"Invalid if-else statement syntax. Required format: if (condition) { ... } else { ... }");
			} else {
				throw new CompileException("Invalid if statement syntax. Required format: if (condition) { ... }");
			}
		}

		// Extract condition
		String condition = params.extractCondition();
		// Extract if body
		String ifBody = params.extractIfBody();

		// Check that the condition is a boolean expression
		validateCondition(condition);

		// Process the if body
		String processedIfBody = processBody(ifBody);

		// If there's an else block, process it
		if (params.hasElseBlock()) {
			String elseBody = params.extractElseBody();
			String processedElseBody = processBody(elseBody);

			// Rebuild the if-else statement
			return "if (" + condition + ") { " + processedIfBody + "; } else { " + processedElseBody + "; }";
		}

		// Rebuild the if statement (no else)
		return "if (" + condition + ") { " + processedIfBody + "; }";
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
	 * Processes the body of the if or else statement.
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