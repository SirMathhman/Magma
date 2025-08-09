package magma;

import java.util.Map;

/**
 * Handles validation of comparison expressions and type checking for comparison operations.
 * This class extracts comparison validation functionality from the Compiler class to reduce
 * method count and improve organization.
 */
public class ComparisonValidator {
    private final ValueProcessor valueProcessor;
    private final Map<String, String> variableTypes;
    private final ArithmeticValidator arithmeticValidator;

    /**
     * Creates a new ComparisonValidator.
     *
     * @param params the parameters for the validator
     */
    public ComparisonValidator(ComparisonValidatorParams params) {
        this.valueProcessor = params.valueProcessor();
        this.variableTypes = params.variableTypes();
        this.arithmeticValidator = params.arithmeticValidator();
    }

    /**
     * Checks comparison operations in a raw value and verifies type compatibility.
     *
     * @param rawValue the raw value to check
     * @throws CompileException if there is a type incompatibility
     */
    public void checkComparisonOperations(String rawValue) {
        // Look for comparison operators
			// Validate the entire comparison expression
			if (rawValue.contains("==") || rawValue.contains("!=") ||
            rawValue.contains("<") || rawValue.contains(">") || 
            rawValue.contains("<=") || rawValue.contains(">=")) validateComparisonExpression(rawValue);
    }

    /**
     * Validates a comparison expression, handling nested expressions with parentheses.
     *
     * @param expression the comparison expression to validate
     * @throws CompileException if operands have incompatible types
     */
    public void validateComparisonExpression(String expression) {
        // Trim the expression
        expression = expression.trim();
        
        // If the expression is enclosed in parentheses, validate the inner expression
        if (expression.startsWith("(") && expression.endsWith(")")) {
            // Remove the outer parentheses and validate the inner expression
            validateComparisonExpression(expression.substring(1, expression.length() - 1).trim());
            return; // Comparison expressions always return Bool
        }
        
        // Check for different comparison operators
        String result = checkEqualityOperators(expression);
        if (result != null) return;
        
        result = checkRelationalOperators(expression);
        if (result != null) return;
        
        // If no comparison operators are found at the top level, validate as a simple value
        arithmeticValidator.validateArithmeticLeafOperand(expression);
    }
    
    /**
     * Checks for equality operators (== and !=) in an expression.
     *
     * @param expression the expression to check
     * @return "Bool" if an equality operator is found and validated, null otherwise
     */
    private String checkEqualityOperators(String expression) {
        // Look for equality operators (== and !=) at the top level
        int equalsIndex = findOperatorAtTopLevel(expression, "==");
        if (equalsIndex != -1) {
            BinaryOperationParams params = new BinaryOperationParams(
                    expression, equalsIndex, "==", "equality");
            return validateEqualityOperation(params);
        }
        
        int notEqualsIndex = findOperatorAtTopLevel(expression, "!=");
        if (notEqualsIndex != -1) {
            BinaryOperationParams params = new BinaryOperationParams(
                    expression, notEqualsIndex, "!=", "inequality");
            return validateEqualityOperation(params);
        }
        
        return null;
    }
    
    /**
     * Checks for relational operators (<, >, <=, >=) in an expression.
     *
     * @param expression the expression to check
     * @return "Bool" if a relational operator is found and validated, null otherwise
     */
    private String checkRelationalOperators(String expression) {
        // Look for relational operators (<, >, <=, >=) at the top level
        int lessThanIndex = findOperatorAtTopLevel(expression, "<");
        if (lessThanIndex != -1 && 
            (lessThanIndex == expression.length() - 1 || expression.charAt(lessThanIndex + 1) != '=')) {
            BinaryOperationParams params = new BinaryOperationParams(
                    expression, lessThanIndex, "<", "less than");
            return validateRelationalOperation(params);
        }
        
        int greaterThanIndex = findOperatorAtTopLevel(expression, ">");
        if (greaterThanIndex != -1 && 
            (greaterThanIndex == expression.length() - 1 || expression.charAt(greaterThanIndex + 1) != '=')) {
            BinaryOperationParams params = new BinaryOperationParams(
                    expression, greaterThanIndex, ">", "greater than");
            return validateRelationalOperation(params);
        }
        
        int lessThanEqualIndex = findOperatorAtTopLevel(expression, "<=");
        if (lessThanEqualIndex != -1) {
            BinaryOperationParams params = new BinaryOperationParams(
                    expression, lessThanEqualIndex, "<=", "less than or equal");
            return validateRelationalOperation(params);
        }
        
        int greaterThanEqualIndex = findOperatorAtTopLevel(expression, ">=");
        if (greaterThanEqualIndex != -1) {
            BinaryOperationParams params = new BinaryOperationParams(
                    expression, greaterThanEqualIndex, ">=", "greater than or equal");
            return validateRelationalOperation(params);
        }
        
        return null;
    }

    /**
     * Validates a binary operation with equality operators (== and !=).
     *
     * @param params parameters for the binary operation
     * @return the type of the result (always "Bool" for comparison expressions)
     * @throws CompileException if operands have incompatible types
     */
    public String validateEqualityOperation(BinaryOperationParams params) {
        // Get operands from the params
        String leftOperand = params.leftOperand();
        String rightOperand = params.rightOperand();
        
        // Validate both operands
        String leftType = validateComparisonOperand(leftOperand, params.operationName());
        String rightType = validateComparisonOperand(rightOperand, params.operationName());
        
        // Check type compatibility
        if (leftType != null && rightType != null && !leftType.equals(rightType)) throw new CompileException(
						"Type mismatch in " + params.operationName() + " operation: Cannot compare " + leftType + " and " +
						rightType + " values. Operands must be of the same type.");
        
        // Equality comparisons always return Bool
        return "Bool";
    }

    /**
     * Validates a binary operation with relational operators (<, >, <=, >=).
     *
     * @param params parameters for the binary operation
     * @return the type of the result (always "Bool" for comparison expressions)
     * @throws CompileException if operands have incompatible types or are not numeric
     */
    public String validateRelationalOperation(BinaryOperationParams params) {
        // Get operands from the params
        String leftOperand = params.leftOperand();
        String rightOperand = params.rightOperand();
        
        // Validate both operands
        String leftType = validateComparisonOperand(leftOperand, params.operationName());
        String rightType = validateComparisonOperand(rightOperand, params.operationName());
        
        // Check that both operands are numeric
        if (leftType != null && !arithmeticValidator.isNumericType(leftType)) throw new CompileException(
						"Type error in " + params.operationName() + " operation: Cannot use " + leftType +
						" with relational operators. Only numeric types can be used.");
        
        if (rightType != null && !arithmeticValidator.isNumericType(rightType)) throw new CompileException(
						"Type error in " + params.operationName() + " operation: Cannot use " + rightType +
						" with relational operators. Only numeric types can be used.");
        
        // Check type compatibility between operands
        if (leftType != null && rightType != null && !leftType.equals(rightType)) throw new CompileException(
						"Type mismatch in " + params.operationName() + " operation: Cannot compare " + leftType + " and " +
						rightType + " values. Operands must be of the same type.");
        
        // Relational comparisons always return Bool
        return "Bool";
    }

    /**
     * Validates an operand in a comparison expression.
     *
     * @param operand the operand to validate
     * @param operationName the name of the operation for error messages
     * @return the type of the operand, or null if the operand is a literal
     * @throws CompileException if the operand is invalid
     */
    public String validateComparisonOperand(String operand, String operationName) {
        // Trim the operand
        operand = operand.trim();
        
        // Check if the operand is a boolean literal
        if (operand.equals("true") || operand.equals("false")) return "Bool";
        
        // Check if the operand is a variable reference
        if (valueProcessor.isVariableReference(operand)) {
            String operandType = variableTypes.get(operand);
            
            // Check if the variable is defined
            if (operandType == null) throw new CompileException(
								"Undefined variable '" + operand + "' used in " + operationName + " operation");
            
            return operandType;
        }
        
        // If the operand is not a variable reference, assume it's a numeric literal
        // The type will be inferred from the context
        return null;
    }

    /**
     * Finds an operator at the top level of an expression (not inside parentheses).
     *
     * @param expression the expression to search
     * @param operator the operator to find
     * @return the index of the operator, or -1 if not found
     */
    public int findOperatorAtTopLevel(String expression, String operator) {
        // For single-character operators, use direct search
        if (operator.length() == 1)
					return arithmeticValidator.findOperatorWithParenthesesTracking(expression, operator);
        
        // For multi-character operators, we need to check each position
        int parenthesesCount = 0;
        
        for (int i = 0; i < expression.length() - operator.length() + 1; i++) {
            char currentChar = expression.charAt(i);
            
            // Track parentheses to respect operator precedence
            if (currentChar == '(') parenthesesCount++;
						else if (currentChar == ')') parenthesesCount--;
            
            // Only consider operators at the top level (outside parentheses)
					// For operators that are 2 characters long, check both characters
					if (parenthesesCount == 0) if (operator.length() == 2 && i < expression.length() - 1 &&
																				 expression.substring(i, i + 2).equals(operator)) return i;
        }
        
        return -1;
    }
}