package magma;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for string operations.
 * <p>
 * This class provides functionality to transform JavaScript and TypeScript syntax into C syntax.
 * It follows a non-static approach to promote better object-oriented design and testability.
 * <p>
 * The compiler supports variable declarations and assignments with the following features:
 * - JavaScript-style declarations (e.g., "let x = 0;")
 * - TypeScript-style type annotations (e.g., "let x : I32 = 0;")
 * - TypeScript-style type suffixes (e.g., "let x = 0I32;")
 * - Mutable variables (declared with "let mut") that can be reassigned
 * - Immutable variables (declared with "let") that cannot be reassigned
 * - Arithmetic operations with type checking:
 * - Addition (e.g., "let z = x + y;")
 * - Subtraction (e.g., "let z = x - y;")
 * - Multiplication (e.g., "let z = x * y;")
 * - Supports arbitrary composition of arithmetic operators (e.g., "let z = x + y + z;", "let z = x - y - z;", or "let z = x * y * z;")
 * - Ensures that all operands in arithmetic operations have the same type
 * - Throws CompileException if trying to operate on numbers of different types
 * - Boolean operations with logical OR (||) and logical AND (&&)
 * - Ensures that only Bool types are used with logical operators
 * - Throws CompileException if trying to use non-Bool types with logical operators
 */
public class Compiler {

	/**
	 * Record to hold parameters for arithmetic operands validation.
	 * Used to reduce parameter count in validateArithmeticOperands.
	 */
	private record ArithmeticOperandsParams(String[] operands, String expectedType, String operationName) {}

	/**
	 * Parameter record for arithmetic type checking operations.
	 * Used to reduce parameter count in methods.
	 */
	private record ArithmeticTypeCheckParams(String rawValue, String operator, String operationName) {}

	/**
	 * Tracks variable types across multiple statements.
	 * Maps variable names to their types (I8, I16, I32, I64, U8, U16, U32, U64).
	 */
	private final Map<String, String> variableTypes = new HashMap<>();
	/**
	 * Tracks variable mutability across multiple statements.
	 * Maps variable names to a boolean indicating whether they are mutable.
	 * A mutable variable (declared with 'mut') can be reassigned, while an immutable
	 * variable (declared without 'mut') cannot.
	 */
	private final Map<String, Boolean> variableMutability = new HashMap<>();
	private final ValueProcessor valueProcessor = new ValueProcessor();
	private final DeclarationProcessor declarationProcessor;

	/**
	 * Creates a new Compiler instance.
	 * Initializes the helper classes and connects them to share the variable types and mutability maps.
	 */
	public Compiler() {
		TypeMapper typeMapper = new TypeMapper();
		DeclarationConfig config = new DeclarationConfig(typeMapper, valueProcessor, variableTypes, variableMutability);
		this.declarationProcessor = new DeclarationProcessor(config);
	}

	/**
	 * Processes a statement as a variable reassignment if applicable.
	 * A reassignment is a statement that doesn't start with "let" and contains "=".
	 * Throws a CompileException if the variable being reassigned is immutable.
	 *
	 * @param statement the statement to process
	 * @return the C equivalent of the reassignment, or null if the statement is not a reassignment
	 * @throws CompileException if the variable is immutable
	 */
	private String processReassignment(String statement) {
		// Check if this is a reassignment
		if (!statement.trim().startsWith("let") && statement.contains("=")) {
			String variableName = statement.substring(0, statement.indexOf("=")).trim();

			// Check if the variable exists
			if (!variableTypes.containsKey(variableName)) {
				throw new CompileException("Cannot reassign undefined variable '" + variableName + "'");
			}

			// Check if the variable is mutable
			Boolean isMutable = variableMutability.get(variableName);
			if (isMutable == null || !isMutable) {
				throw new CompileException("Cannot reassign immutable variable '" + variableName + "'");
			}

			// If the variable is mutable, return the reassignment as is
			return statement;
		}

		// Not a reassignment
		return null;
	}

	/**
	 * Checks if a raw value contains an arithmetic operation (addition, subtraction, or multiplication) and verifies type compatibility.
	 * If the raw value contains an operator (+, -, or *), it extracts the operands and ensures they all have the same type.
	 * <p>
	 * This method enforces the requirement that numbers in an arithmetic operation must be of the same type,
	 * which prevents unintended type conversions and potential precision loss or overflow issues.
	 * <p>
	 * This method supports arbitrary composition of arithmetic operators (e.g., "3 + 5 + 7", "10 - 5 - 2", or "2 * 3 * 4").
	 * It also properly handles parenthesized expressions like "(3 + 4) * 7" by respecting the operator precedence
	 * established by parentheses.
	 *
	 * @param rawValue the raw value to check
	 * @throws CompileException if any operands have different types
	 */
	private void checkArithmeticTypeCompatibility(String rawValue) {
		// Look for operators
		if (rawValue.contains("+") || rawValue.contains("-") || rawValue.contains("*")) {
			// Validate the entire expression, respecting parentheses
			validateArithmeticExpression(rawValue);
		}
	}

	/**
	 * Validates an arithmetic expression, handling nested expressions with parentheses.
	 * This method recursively processes the expression to ensure all operands are of the same type.
	 *
	 * @param expression the arithmetic expression to validate
	 * @return the inferred type of the expression, or null if no type can be inferred
	 * @throws CompileException if any operands have different types
	 */
	private String validateArithmeticExpression(String expression) {
		// Trim the expression
		expression = expression.trim();

		// If the expression is enclosed in parentheses, validate the inner expression
		if (expression.startsWith("(") && expression.endsWith(")")) {
			// Remove the outer parentheses and validate the inner expression
			return validateArithmeticExpression(expression.substring(1, expression.length() - 1).trim());
		}

		// Look for addition (+) at the top level (not inside parentheses)
		int addIndex = findOperatorAtTopLevel(expression, "+");
		if (addIndex != -1) {
			return validateBinaryOperation(expression, addIndex, "+", "addition");
		}

		// Look for subtraction (-) at the top level (not inside parentheses)
		int subIndex = findOperatorAtTopLevel(expression, "-");
		if (subIndex != -1) {
			return validateBinaryOperation(expression, subIndex, "-", "subtraction");
		}

		// Look for multiplication (*) at the top level (not inside parentheses)
		int mulIndex = findOperatorAtTopLevel(expression, "*");
		if (mulIndex != -1) {
			return validateBinaryOperation(expression, mulIndex, "*", "multiplication");
		}

		// If no operators found at the top level, this is a leaf operand (variable or literal)
		return validateArithmeticLeafOperand(expression);
	}

	/**
	 * Validates a binary arithmetic operation, ensuring type compatibility between operands.
	 *
	 * @param expression    the full expression containing the operation
	 * @param operatorIndex the index of the operator in the expression
	 * @param operator      the operator symbol ("+", "-", "*")
	 * @param operationName the name of the operation for error messages ("addition", "subtraction", "multiplication")
	 * @return the inferred type of the operation result, or null if no type can be inferred
	 * @throws CompileException if operands have incompatible types
	 */
	private String validateBinaryOperation(String expression, int operatorIndex, String operator, String operationName) {
		// Split at the operator
		String leftOperand = expression.substring(0, operatorIndex).trim();
		String rightOperand = expression.substring(operatorIndex + operator.length()).trim();

		// Validate both sides and get their types
		String leftType = validateArithmeticExpression(leftOperand);
		String rightType = validateArithmeticExpression(rightOperand);

		// Check type compatibility between the operands
		return checkArithmeticTypeConsistency(leftOperand, rightOperand, leftType, rightType, operationName);
	}

	/**
	 * Validates a leaf operand in an arithmetic expression.
	 * For variables, ensures it is of a numeric type (I8, I16, I32, I64, U8, U16, U32, U64).
	 * For literals, ensures it is a valid number.
	 *
	 * @param operand the leaf operand to validate
	 * @return the inferred type of the operand, or null if no type can be inferred
	 * @throws CompileException if the operand is not a valid arithmetic value
	 */
	private String validateArithmeticLeafOperand(String operand) {
		// Trim the operand
		operand = operand.trim();

		// If the operand is a variable reference, check its type
		if (valueProcessor.isVariableReference(operand)) {
			return validateVariableTypeForArithmetic(operand);
		} else {
			// For non-variable references (literals), we'll return null
			// The type will be inferred from the context
			return null;
		}
	}

	/**
	 * Validates that a variable has a numeric type suitable for arithmetic operations.
	 *
	 * @param variableName the name of the variable to validate
	 * @return the type of the variable, or null if the variable is not defined
	 * @throws CompileException if the variable is not of a numeric type
	 */
	private String validateVariableTypeForArithmetic(String variableName) {
		String operandType = variableTypes.get(variableName);

		// Check if the variable is defined
		if (operandType == null) {
			// This will be caught elsewhere, so we don't need to throw here
			return null;
		}

		// Check if the variable has a numeric type
		boolean isNumericType = isNumericType(operandType);

		if (!isNumericType) {
			throw new CompileException(
					"Type mismatch in arithmetic expression: Cannot use " + operandType + " variable '" + variableName +
					"' in arithmetic operations. Only numeric types can be used.");
		}

		return operandType;
	}

	/**
	 * Checks if a type is a numeric type.
	 *
	 * @param type the type to check
	 * @return true if the type is numeric, false otherwise
	 */
	private boolean isNumericType(String type) {
		return type.equals("I8") || type.equals("I16") || type.equals("I32") || type.equals("I64") || type.equals("U8") ||
					 type.equals("U16") || type.equals("U32") || type.equals("U64");
	}

	/**
	 * Checks type consistency between two arithmetic operands.
	 * If both operands have types, ensures they have the same type.
	 *
	 * @param leftOperand   the left operand
	 * @param rightOperand  the right operand
	 * @param leftType      the type of the left operand, or null if unknown
	 * @param rightType     the type of the right operand, or null if unknown
	 * @param operationName the name of the operation (for error messages)
	 * @return the common type, or null if no common type can be determined
	 * @throws CompileException if operands have incompatible types
	 */
	private String checkArithmeticTypeConsistency(String leftOperand,
																								String rightOperand,
																								String leftType,
																								String rightType,
																								String operationName) {
		// If both types are known and they don't match, we have a type mismatch
		if (leftType != null && rightType != null && !leftType.equals(rightType)) {
			throw new CompileException(
					"Type mismatch in " + operationName + ": Cannot perform " + operationName + " with " + leftType + " and " +
					rightType + " variables in expression. All numbers in a " + operationName +
					" operation must be of the same type.");
		}

		// Return the known type, preferring leftType if both are known
		return (leftType != null) ? leftType : rightType;
	}


	/**
	 * Checks logical operations (|| and &&) in a raw value and verifies type compatibility.
	 * This method enforces that only boolean values can be used with logical operators.
	 * <p>
	 * For both OR and AND operations, this method:
	 * 1. Detects the presence of the operator
	 * 2. Validates the expression structure, including any nested expressions
	 * 3. Verifies that all operands are either boolean literals or Bool type variables
	 *
	 * @param rawValue the raw value to check
	 * @throws CompileException if any operand is not a Bool type
	 */
	private void checkLogicalOperations(String rawValue) {
		// Look for operators first
		if (rawValue.contains("||") || rawValue.contains("&&")) {
			// Validate the entire expression, respecting parentheses
			validateBooleanExpression(rawValue);
		}
	}

	/**
	 * Validates a boolean expression, handling nested expressions with parentheses.
	 * This method recursively processes the expression to ensure all operands are valid boolean values.
	 *
	 * @param expression the boolean expression to validate
	 * @throws CompileException if any operand is not a boolean value
	 */
	private void validateBooleanExpression(String expression) {
		// Trim the expression
		expression = expression.trim();

		// If the expression is enclosed in parentheses, validate the inner expression
		if (expression.startsWith("(") && expression.endsWith(")")) {
			// Remove the outer parentheses and validate the inner expression
			validateBooleanExpression(expression.substring(1, expression.length() - 1).trim());
			return;
		}

		// Look for logical OR (||) at the top level (not inside parentheses)
		int orIndex = findOperatorAtTopLevel(expression, "||");
		if (orIndex != -1) {
			// Split at the OR operator
			String leftOperand = expression.substring(0, orIndex).trim();
			String rightOperand = expression.substring(orIndex + 2).trim();

			// Validate both sides
			validateBooleanExpression(leftOperand);
			validateBooleanExpression(rightOperand);
			return;
		}

		// Look for logical AND (&&) at the top level (not inside parentheses)
		int andIndex = findOperatorAtTopLevel(expression, "&&");
		if (andIndex != -1) {
			// Split at the AND operator
			String leftOperand = expression.substring(0, andIndex).trim();
			String rightOperand = expression.substring(andIndex + 2).trim();

			// Validate both sides
			validateBooleanExpression(leftOperand);
			validateBooleanExpression(rightOperand);
			return;
		}

		// If no operators found at the top level, validate this as a boolean operand
		validateBooleanOperand(expression, "boolean expression");
	}

	/**
	 * Finds the index of an operator at the top level of an expression.
	 * This ensures operators inside parentheses are ignored.
	 *
	 * @param expression the expression to search
	 * @param operator   the operator to find ("||", "&&", "+", "-", "*")
	 * @return the index of the operator, or -1 if not found at the top level
	 */
	private int findOperatorAtTopLevel(String expression, String operator) {
		int parenthesesLevel = 0;
		char firstChar = operator.charAt(0);
		boolean isSingleCharOperator = operator.length() == 1;

		for (int i = 0; i < expression.length(); i++) {
			char currentChar = expression.charAt(i);

			// Track parentheses level
			if (currentChar == '(') {
				parenthesesLevel++;
			} else if (currentChar == ')') {
				parenthesesLevel--;
			}

			// Check for operator at the top level
			if (parenthesesLevel == 0 && currentChar == firstChar) {
				if (isSingleCharOperator) {
					// For single character operators (+, -, *), we've found a match
					return i;
				} else if (i < expression.length() - 1 && expression.charAt(i + 1) == firstChar) {
					// For double character operators (||, &&), check the next character too
					return i;
				}
			}
		}

		return -1; // Operator not found at the top level
	}


	/**
	 * Validates that an operand is a boolean value (literal or variable).
	 *
	 * @param operand      the operand to validate
	 * @param operatorName the name of the operator for error messages
	 * @throws CompileException if the operand is not a boolean value
	 */
	private void validateBooleanOperand(String operand, String operatorName) {
		// Check if operand is a boolean literal
		boolean isBooleanLiteral = operand.equals("true") || operand.equals("false");

		// If not a boolean literal, check if it's a variable reference with Bool type
		if (!isBooleanLiteral && valueProcessor.isVariableReference(operand)) {
			String operandType = variableTypes.get(operand);

			// Check if the variable is defined
			if (operandType == null) {
				throw new CompileException("Undefined variable '" + operand + "' used in " + operatorName + " operation");
			}

			// Check if the variable has Bool type
			if (!operandType.equals("Bool")) {
				throw new CompileException(
						"Type mismatch in " + operatorName + " operation: Cannot use " + operandType + " variable '" + operand +
						"'. Only Bool type can be used with " + operatorName + ".");
			}
		} else if (!isBooleanLiteral) {
			// If not a boolean literal or a variable reference, it's an invalid operand
			throw new CompileException("Invalid operand '" + operand + "' in " + operatorName +
																 " operation. Only Bool type or boolean literals can be used.");
		}
	}

	/**
	 * Processes a single statement and returns its C equivalent.
	 *
	 * @param statement the statement to process
	 * @return the C equivalent of the statement
	 */
	private String processStatement(String statement) {
		// Check if this is a reassignment
		String reassignmentResult = processReassignment(statement);
		if (reassignmentResult != null) {
			return reassignmentResult;
		}

		// Create a context from the statement
		DeclarationContext context = declarationProcessor.createContext(statement);
		String variableName = context.variableName();
		String valueSection = context.valueSection();

		// Extract the raw value
		String rawValue = valueProcessor.extractRawValue(valueSection);

		// Check for operations and verify type compatibility
		checkArithmeticTypeCompatibility(rawValue);
		checkLogicalOperations(rawValue);

		// Handle TypeScript-style declarations with type annotations (e.g., "let x : I32 = 0;")
		if (statement.contains(" : ")) {
			return declarationProcessor.processTypeScriptAnnotation(
					new TypeScriptAnnotationParams(statement, context, variableName, rawValue));
		}

		// Handle variable declarations with type suffixes
		if (context.typeSuffix() != null) {
			// Store the variable type
			variableTypes.put(variableName, context.typeSuffix());

			return declarationProcessor.processTypeSuffixDeclaration(context);
		}

		// Handle standard JavaScript declarations (e.g., "let x = 0;")
		return declarationProcessor.processStandardDeclaration(statement, variableName);
	}

	/**
	 * Echoes the input string, with special handling for JavaScript and TypeScript-style variable declarations.
	 * If the input is a JavaScript 'let' declaration or TypeScript typed declaration,
	 * it will be converted to a C fixed-width integer type declaration.
	 * Otherwise, returns the input string unchanged.
	 * <p>
	 * The method handles the following formats:
	 * - JavaScript: "let x = 0;" → "int32_t x = 0;"
	 * - TypeScript with type annotations: "let x : TYPE = 0;" → "c_type x = 0;"
	 * where TYPE can be I8, I16, I32, I64, U8, U16, U32, U64
	 * - TypeScript with type suffix: "let x = 0TYPE;" → "c_type x = 0;"
	 * where TYPE can be I8, I16, I32, I64, U8, U16, U32, U64
	 * - Multiple declarations: "let x = 0; let y = x;" → "int32_t x = 0; int32_t y = x;"
	 * - Variable references: "let y = x;" → "int32_t y = x;"
	 * - Mutable variables: "let mut x = 0;" → "int32_t x = 0;"
	 * - Variable reassignment (only for mutable variables): "x = 100;" → "x = 100;"
	 * <p>
	 * Mutable variables can be reassigned after declaration:
	 * "let mut x = 0; x = 100;" → "int32_t x = 0; x = 100;"
	 * <p>
	 * Immutable variables cannot be reassigned after declaration:
	 * "let x = 0; x = 100;" → CompileException("Cannot reassign immutable variable 'x'")
	 *
	 * @param input the string to echo
	 * @return the transformed string or the same string if no transformation is needed
	 * @throws CompileException if there is a type incompatibility between the declared type and the value type
	 * @throws CompileException if an attempt is made to reassign an immutable variable
	 */
	public String compile(String input) {
		if (input == null) {
			return null;
		}

		// Check if the input contains variable declarations
		if (!input.contains("let ")) {
			return input;
		}

		// Clear the variable type map before processing
		variableTypes.clear();

		// Handle multiple statements
		if (input.contains(";")) {
			String[] statements = input.split(";");
			StringBuilder result = new StringBuilder();

			for (int i = 0; i < statements.length; i++) {
				String statement = statements[i].trim();
				if (!statement.isEmpty()) {
					// Process the statement, but remove any trailing semicolon
					String processed = processStatement(statement);
					if (processed.endsWith(";")) {
						processed = processed.substring(0, processed.length() - 1);
					}

					result.append(processed);

					// Add semicolon and space if not the last statement
					if (i < statements.length - 1) {
						result.append("; ");
					} else {
						result.append(";");
					}
				}
			}

			return result.toString();
		}

		// Handle a single statement
		return processStatement(input);
	}
}