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
	 * Simple record to hold logical operator details.
	 * Used to reduce parameter count in validateBooleanOperation.
	 */
	private record LogicalOperator(String symbol, String name) {
		static LogicalOperator OR = new LogicalOperator("||", "OR");
		static LogicalOperator AND = new LogicalOperator("&&", "AND");
	}

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
	 * Checks if a statement is a variable reassignment.
	 * A reassignment is a statement that doesn't start with "let" and contains "=".
	 *
	 * @param statement the statement to check
	 * @return true if the statement is a reassignment, false otherwise
	 */
	private boolean isReassignment(String statement) {
		return !statement.trim().startsWith("let") && statement.contains("=");
	}

	/**
	 * Processes a reassignment statement and returns its C equivalent.
	 * Throws a CompileException if the variable being reassigned is immutable.
	 *
	 * @param statement the reassignment statement to process
	 * @return the C equivalent of the reassignment
	 * @throws CompileException if the variable is immutable
	 */
	private String processReassignment(String statement) {
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
		// Check for addition
		if (rawValue.contains("+")) {
			checkOperationTypeCompatibility(new ArithmeticTypeCheckParams(rawValue, "+", "addition"));
		}

		// Check for subtraction
		if (rawValue.contains("-")) {
			checkOperationTypeCompatibility(new ArithmeticTypeCheckParams(rawValue, "-", "subtraction"));
		}

		// Check for multiplication
		if (rawValue.contains("*")) {
			checkOperationTypeCompatibility(new ArithmeticTypeCheckParams(rawValue, "*", "multiplication"));
		}
	}

	/**
	 * Helper method to check type compatibility for a specific arithmetic operation.
	 *
	 * @param params parameters for the operation check
	 * @throws CompileException if any operands have different types
	 */
	private void checkOperationTypeCompatibility(ArithmeticTypeCheckParams params) {
		// Need to escape the operator if it's a special character in regex
		String escapedOperator;
		if ("+".equals(params.operator())) {
			escapedOperator = "\\+";
		} else if ("*".equals(params.operator())) {
			escapedOperator = "\\*";
		} else {
			escapedOperator = params.operator();
		}

		// Split by operator and check if we have multiple operands
		String[] operands = params.rawValue().split(escapedOperator);
		if (operands.length <= 1) {
			return;
		}

		// Find the expected type (first variable reference with a known type)
		String expectedType = null;
		for (String operand : operands) {
			String trimmedOperand = operand.trim();
			// Skip non-variable references
			if (!valueProcessor.isVariableReference(trimmedOperand)) {
				continue;
			}

			// Check if this variable has a known type
			String operandType = variableTypes.get(trimmedOperand);
			if (operandType != null) {
				expectedType = operandType;
				break;
			}
		}

		// If we found an expected type, validate all variable references against it
		if (expectedType != null) {
			validateArithmeticOperands(new ArithmeticOperandsParams(operands, expectedType, params.operationName()));
		}
	}

	/**
	 * Validates that all variable references in an arithmetic expression have the expected type.
	 * This method is kept separate to maintain low cyclomatic complexity.
	 *
	 * @param params parameters for the validation
	 * @throws CompileException if any variable has a different type
	 */
	private void validateArithmeticOperands(ArithmeticOperandsParams params) {
		for (String operand : params.operands()) {
			String trimmedOperand = operand.trim();
			// Skip non-variable references
			if (!valueProcessor.isVariableReference(trimmedOperand)) {
				continue;
			}

			// Check type compatibility
			String operandType = variableTypes.get(trimmedOperand);
			if (operandType != null && !operandType.equals(params.expectedType())) {
				throw new CompileException(
						"Type mismatch in " + params.operationName() + ": Cannot perform " + params.operationName() + " with " +
						params.expectedType() + " and " + operandType + " variables in expression. All numbers in a " +
						params.operationName() + " operation must be of the same type.");
			}
		}
	}

	/**
	 * Checks logical operations (|| and &&) in a raw value and verifies type compatibility.
	 * This method enforces that only boolean values can be used with logical operators.
	 * <p>
	 * For both OR and AND operations, this method:
	 * 1. Detects the presence of the operator
	 * 2. Extracts the operands
	 * 3. Verifies that all operands are either boolean literals or Bool type variables
	 *
	 * @param rawValue the raw value to check
	 * @throws CompileException if any operand is not a Bool type
	 */
	private void checkLogicalOperations(String rawValue) {
		// Check for logical OR operation
		if (rawValue.contains("||")) {
			validateBooleanOperation(rawValue, LogicalOperator.OR);
		}

		// Check for logical AND operation
		if (rawValue.contains("&&")) {
			validateBooleanOperation(rawValue, LogicalOperator.AND);
		}
	}

	/**
	 * Validates a boolean operation by checking that all operands are boolean values.
	 *
	 * @param rawValue the raw value containing the operation
	 * @param operator the logical operator details (symbol and name)
	 * @throws CompileException if any operand is not a boolean value
	 */
	private void validateBooleanOperation(String rawValue, LogicalOperator operator) {
		String[] operands;

		// Split by the appropriate operator
		if ("||".equals(operator.symbol())) {
			operands = rawValue.split("\\|\\|");
		} else if ("&&".equals(operator.symbol())) {
			operands = rawValue.split("&&");
		} else {
			return; // Unsupported operator
		}

		// Verify that we have exactly two operands (binary operation)
		if (operands.length == 2) {
			String leftOperand = operands[0].trim();
			String rightOperand = operands[1].trim();

			// Validate each operand
			validateBooleanOperand(leftOperand, operator.name());
			validateBooleanOperand(rightOperand, operator.name());
		}
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
		if (isReassignment(statement)) {
			return processReassignment(statement);
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