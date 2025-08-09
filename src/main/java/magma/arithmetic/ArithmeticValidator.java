package magma.arithmetic;

import magma.core.CompileException;
import magma.core.ValueProcessor;
import magma.operator.BinaryOperationParams;

import java.util.Map;

/**
 * Handles validation of arithmetic expressions and type checking for arithmetic operations.
 * This class extracts arithmetic validation functionality from the Compiler class to reduce
 * method count and improve organization.
 */
public class ArithmeticValidator {
	private final ValueProcessor valueProcessor;
	private final Map<String, String> variableTypes;

	/**
	 * Creates a new ArithmeticValidator.
	 *
	 * @param valueProcessor processor for variable values
	 * @param variableTypes  map of variable names to their types
	 */
	public ArithmeticValidator(ValueProcessor valueProcessor, Map<String, String> variableTypes) {
		this.valueProcessor = valueProcessor;
		this.variableTypes = variableTypes;
	}

	/**
	 * Checks arithmetic expressions in a raw value and verifies type compatibility.
	 *
	 * @param rawValue the raw value to check
	 * @throws CompileException if there is a type incompatibility
	 */
	public void checkArithmeticTypeCompatibility(String rawValue) {
		// Look for arithmetic operators
		// Validate the entire arithmetic expression
		if (rawValue.contains("+") || rawValue.contains("-") || rawValue.contains("*"))
			validateArithmeticExpression(rawValue);
	}

	/**
	 * Validates an arithmetic expression, handling nested expressions with parentheses.
	 *
	 * @param expression the arithmetic expression to validate
	 * @return the inferred type of the expression result, or null if no type can be inferred
	 * @throws CompileException if operands have incompatible types
	 */
	public String validateArithmeticExpression(String expression) {
		// Trim the expression
		expression = expression.trim();

		// If the expression is enclosed in parentheses, validate the inner expression
		// Remove the outer parentheses and validate the inner expression
		if (expression.startsWith("(") && expression.endsWith(")"))
			return validateArithmeticExpression(expression.substring(1, expression.length() - 1).trim());

		// Look for addition at the top level
		int additionIndex = findOperatorWithParenthesesTracking(expression, "+");
		if (additionIndex != -1) {
			BinaryOperationParams params = new BinaryOperationParams(expression, additionIndex, "+", "addition");
			return validateBinaryOperation(params);
		}

		// Look for subtraction at the top level
		int subtractionIndex = findOperatorWithParenthesesTracking(expression, "-");
		if (subtractionIndex != -1) {
			BinaryOperationParams params = new BinaryOperationParams(expression, subtractionIndex, "-", "subtraction");
			return validateBinaryOperation(params);
		}

		// Look for multiplication at the top level (but not dereference operations)
		int multiplicationIndex = findOperatorWithParenthesesTracking(expression, "*");
		if (multiplicationIndex != -1 && multiplicationIndex > 0) {
			// Only treat as multiplication if * is not at the beginning (which would be dereference)
			BinaryOperationParams params = new BinaryOperationParams(expression, multiplicationIndex, "*", "multiplication");
			return validateBinaryOperation(params);
		}

		// If no operators are found, validate this as a leaf operand
		return validateArithmeticLeafOperand(expression);
	}

	/**
	 * Validates a binary arithmetic operation, ensuring type compatibility between operands.
	 *
	 * @param params parameters for the binary operation
	 * @return the inferred type of the operation result, or null if no type can be inferred
	 * @throws CompileException if operands have incompatible types
	 */
	public String validateBinaryOperation(BinaryOperationParams params) {
		// Get operands from the params
		String leftOperand = params.leftOperand();
		String rightOperand = params.rightOperand();

		// Validate both sides and get their types
		String leftType = validateArithmeticExpression(leftOperand);
		String rightType = validateArithmeticExpression(rightOperand);

		// Check type compatibility between the operands
		ArithmeticTypeCheckParams typeParams =
				new ArithmeticTypeCheckParams(leftOperand, rightOperand, leftType, rightType, params.operationName());
		return checkArithmeticTypeConsistency(typeParams);
	}

	/**
	 * Validates a leaf operand in an arithmetic expression.
	 *
	 * @param operand the leaf operand to validate
	 * @return the inferred type of the operand, or null if no type can be inferred
	 * @throws CompileException if the operand is not a valid arithmetic value
	 */
	public String validateArithmeticLeafOperand(String operand) {
		// Trim the operand
		operand = operand.trim();

		// Check if this is a dereference operation (*variable)
		if (valueProcessor.isDereference(operand)) {
			return validateDereferenceForArithmetic(operand);
		}

		// If the operand is a variable reference, check its type
		// For non-variable references (literals), we'll return null
		// The type will be inferred from the context
		if (valueProcessor.isVariableReference(operand)) return validateVariableTypeForArithmetic(operand);
		return null;
	}

	/**
	 * Validates that a variable has a numeric type suitable for arithmetic operations.
	 *
	 * @param variableName the name of the variable to validate
	 * @return the type of the variable, or null if the variable is not defined
	 * @throws CompileException if the variable is not of a numeric type
	 */
	public String validateVariableTypeForArithmetic(String variableName) {
		String operandType = variableTypes.get(variableName);

		// Check if the variable is defined
		// This will be caught elsewhere, so we don't need to throw here
		if (operandType == null) return null;

		// Check if the variable has a numeric type
		boolean isNumericType = isNumericType(operandType);

		if (!isNumericType) throw new CompileException(
				"Type mismatch in arithmetic expression: Cannot use " + operandType + " variable '" + variableName +
				"' in arithmetic operations. Only numeric types can be used.");

		return operandType;
	}

	/**
	 * Validates a dereference operation for arithmetic expressions.
	 *
	 * @param dereferenceExpression the dereference expression to validate (*variable)
	 * @return the base type of the dereferenced pointer
	 * @throws CompileException if the dereferenced variable is not a pointer or base type is not numeric
	 */
	public String validateDereferenceForArithmetic(String dereferenceExpression) {
		// Extract the variable name from the dereference expression
		String variableName = valueProcessor.extractVariableFromDereference(dereferenceExpression);
		String pointerType = variableTypes.get(variableName);

		// Check if the variable is defined
		if (pointerType == null) {
			throw new CompileException("Variable '" + variableName + "' not found for dereference operation");
		}

		// Check if the variable is a pointer type
		if (!pointerType.startsWith("*")) {
			throw new CompileException(
				"Type mismatch: Cannot dereference non-pointer variable '" + variableName + "' of type " + pointerType);
		}

		// Extract the base type from the pointer type
		String baseType = pointerType.substring(1);

		// Check if the base type is numeric
		if (!isNumericType(baseType)) {
			throw new CompileException(
				"Type mismatch in arithmetic expression: Cannot use dereferenced " + baseType + " value in arithmetic operations. Only numeric types can be used.");
		}

		return baseType;
	}

	/**
	 * Checks if a type is a numeric type.
	 *
	 * @param type the type to check
	 * @return true if the type is numeric, false otherwise
	 */
	public boolean isNumericType(String type) {
		return type.equals("I8") || type.equals("I16") || type.equals("I32") || type.equals("I64") || type.equals("U8") ||
					 type.equals("U16") || type.equals("U32") || type.equals("U64") || type.equals("F32") || type.equals("F64");
	}

	/**
	 * Checks type consistency between two arithmetic operands.
	 *
	 * @param params parameters for type checking
	 * @return the common type, or null if no common type can be determined
	 * @throws CompileException if operands have incompatible types
	 */
	public String checkArithmeticTypeConsistency(ArithmeticTypeCheckParams params) {
		// If both types are known and they don't match, we have a type mismatch
		if (params.hasTypeMismatch()) throw new CompileException(params.createTypeMismatchMessage());

		// Return the known type, preferring leftType if both are known
		if (params.leftType() != null) return params.leftType();
		return params.rightType();
	}

	/**
	 * Finds an operator in an expression, respecting parentheses.
	 *
	 * @param expression the expression to search
	 * @param operator   the operator to find
	 * @return the index of the operator, or -1 if not found
	 */
	public int findOperatorWithParenthesesTracking(String expression, String operator) {
		int parenthesesCount = 0;

		for (int i = 0; i < expression.length() - operator.length() + 1; i++) {
			// Track parentheses to respect operator precedence
			if (expression.charAt(i) == '(') parenthesesCount++;
			else if (expression.charAt(i) == ')') parenthesesCount--;

			// Only consider operators at the top level (outside parentheses)
			// Check for the operator at this position
			if (parenthesesCount == 0) if (expression.startsWith(operator, i)) return i;
		}

		return -1;
	}
}
