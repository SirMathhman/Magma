package magma.control;

import magma.core.CompileException;

/**
 * Validator for while statements.
 * This class handles validation of while statements, ensuring they have correct structure
 * and that the condition expression is a valid boolean.
 * <p>
 * Key features:
 * - Validates structure of while statements
 * - Enforces required braces for while blocks
 * - Validates that conditions are boolean expressions
 * - Processes the body content of while blocks
 * - Ensures proper formatting with semicolons
 */
public class WhileStatementValidator {
    private final WhileStatementParams params;

    /**
     * Creates a new WhileStatementValidator.
     *
     * @param params parameters for while statement validation
     */
    public WhileStatementValidator(WhileStatementParams params) {
        this.params = params;
    }

    /**
     * Validates a while statement.
     * Checks that:
     * - The statement has required parentheses and curly braces
     * - The condition is a valid boolean expression
     *
     * @return the validated while statement
     * @throws CompileException if the statement is invalid
     */
    public String validateWhileStatement() {
        if (!params.hasValidStructure()) {
            throw new CompileException("Invalid while statement syntax. Required format: while (condition) { ... }");
        }

        // Extract condition
        String condition = params.extractCondition();
        // Extract body
        String body = params.extractBody();

        // Check that the condition is a boolean expression
        validateCondition(condition);

        // Process the body
        String processedBody = processBody(body);

        // Rebuild the while statement
        return "while (" + condition + ") { " + processedBody + "; }";
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
     * Processes the body of the while statement.
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