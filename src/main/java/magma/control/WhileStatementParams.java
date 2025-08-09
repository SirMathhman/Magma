package magma.control;

import magma.bool.BooleanExpressionValidator;
import magma.core.ValueProcessor;

import java.util.Map;

/**
 * Parameters for validating while statements.
 * Holds all the necessary parameters for validation.
 * <p>
 * This class provides methods to:
 * - Extract conditions from while statements
 * - Extract body content from while blocks
 * - Validate while statement structure including required braces
 */
public record WhileStatementParams(String statement, ValueProcessor valueProcessor, Map<String, String> variableTypes,
                                 BooleanExpressionValidator booleanValidator) {
    /**
     * Extracts the condition from a while statement.
     * Assumes the format "while (condition) { ... }".
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
     * Extracts the body from a while statement.
     * Assumes the format "while (condition) { body }".
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
     * Checks if the while statement has valid structure with required parentheses and curly braces.
     *
     * @return true if the statement has valid structure
     */
    public boolean hasValidStructure() {
        return statement.trim().startsWith("while (") && statement.contains(")") && statement.contains("{") &&
               statement.contains("}");
    }
}