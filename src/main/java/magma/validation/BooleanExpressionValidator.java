package magma.validation;

import magma.core.CompileException;
import magma.core.ValueProcessor;

import java.util.Map;

/**
 * Handles validation of boolean expressions and type checking for logical operations.
 * This class extracts boolean expression validation functionality from the Compiler class
 * to reduce method count and improve organization.
 */
public class BooleanExpressionValidator {
    private final ValueProcessor valueProcessor;
    private final Map<String, String> variableTypes;

    /**
     * Creates a new BooleanExpressionValidator.
     *
     * @param valueProcessor processor for variable values
     * @param variableTypes map of variable names to their types
     */
    public BooleanExpressionValidator(ValueProcessor valueProcessor, Map<String, String> variableTypes) {
        this.valueProcessor = valueProcessor;
        this.variableTypes = variableTypes;
    }

    /**
     * Checks logical operations (|| and &&) in a raw value and verifies type compatibility.
     * This method enforces that only boolean values can be used with logical operators.
     *
     * @param rawValue the raw value to check
     * @throws CompileException if any operand is not a Bool type
     */
    public void checkLogicalOperations(String rawValue) {
        // Look for operators first
			// Validate the entire expression, respecting parentheses
			if (rawValue.contains("||") || rawValue.contains("&&")) validateBooleanExpression(rawValue);
    }

    /**
     * Validates a boolean expression, handling nested expressions with parentheses.
     * This method recursively processes the expression to ensure all operands are boolean type.
     *
     * @param expression the boolean expression to validate
     * @throws CompileException if any operand is not a Bool type
     */
    public void validateBooleanExpression(String expression) {
        // Trim the expression
        expression = expression.trim();
        
        // If the expression is enclosed in parentheses, validate the inner expression
        if (expression.startsWith("(") && expression.endsWith(")")) {
            // Remove the outer parentheses and validate the inner expression
            validateBooleanExpression(expression.substring(1, expression.length() - 1).trim());
            return;
        }
        
        // Look for OR operator (||) at the top level
        int orIndex = findOperatorWithParenthesesTracking(expression, "||");
        if (orIndex != -1) {
            // Split the expression and validate both sides
            String leftOperand = expression.substring(0, orIndex).trim();
            String rightOperand = expression.substring(orIndex + 2).trim();
            
            validateBooleanOperand(leftOperand, "logical OR");
            validateBooleanOperand(rightOperand, "logical OR");
            return;
        }
        
        // Look for AND operator (&&) at the top level
        int andIndex = findOperatorWithParenthesesTracking(expression, "&&");
        if (andIndex != -1) {
            // Split the expression and validate both sides
            String leftOperand = expression.substring(0, andIndex).trim();
            String rightOperand = expression.substring(andIndex + 2).trim();
            
            validateBooleanOperand(leftOperand, "logical AND");
            validateBooleanOperand(rightOperand, "logical AND");
            return;
        }
        
        // If no logical operators are found, validate as a single boolean operand
        validateBooleanOperand(expression, "boolean expression");
    }

    /**
     * Validates that an operand is a boolean value (literal, variable, or parenthesized expression).
     *
     * @param operand the operand to validate
     * @param operatorName the name of the operator for error messages
     * @throws CompileException if the operand is not a boolean value
     */
    public void validateBooleanOperand(String operand, String operatorName) {
        // Trim the operand
        operand = operand.trim();
        
        // Check if operand is a boolean literal
        boolean isBooleanLiteral = operand.equals("true") || operand.equals("false");
        
        // Check if operand is a parenthesized expression
        if (!isBooleanLiteral && operand.startsWith("(") && operand.endsWith(")")) {
            // Recursively validate the inner expression
            validateBooleanExpression(operand);
            return;
        }
        
        // If not a boolean literal or parenthesized expression, check if it's a variable reference with Bool type
        if (!isBooleanLiteral && valueProcessor.isVariableReference(operand)) {
            String operandType = variableTypes.get(operand);
            
            // Check if the variable is defined
            if (operandType == null)
							throw new CompileException("Undefined variable '" + operand + "' used in " + operatorName + " operation");
            
            // Check if the variable has Bool type
            if (!operandType.equals("Bool")) throw new CompileException(
								"Type mismatch in " + operatorName + " operation: Cannot use " + operandType + " variable '" + operand +
								"'. Only Bool type can be used with " + operatorName + ".");
        } else // If not a boolean literal, parenthesized expression, or a variable reference, it's an invalid operand
					if (!isBooleanLiteral) throw new CompileException("Invalid operand '" + operand + "' in " + operatorName +
																														" operation. Only Bool type or boolean literals can be used.");
    }

    /**
     * Finds an operator in an expression, respecting parentheses.
     *
     * @param expression the expression to search
     * @param operator the operator to find
     * @return the index of the operator, or -1 if not found
     */
    public int findOperatorWithParenthesesTracking(String expression, String operator) {
        int parenthesesCount = 0;
        
        for (int i = 0; i < expression.length() - operator.length() + 1; i++) {
            // Track parentheses to respect operator precedence
            if (expression.charAt(i) == '(') parenthesesCount++;
						else if (expression.charAt(i) == ')') parenthesesCount--;
            
            // Only consider operators at the top level (outside parentheses)
					// For operators that are 2 characters long, check both characters
					if (parenthesesCount == 0) if (operator.length() == 2 && i < expression.length() - 1 &&
																				 expression.substring(i, i + 2).equals(operator)) return i;
        }
        
        return -1;
    }
}
