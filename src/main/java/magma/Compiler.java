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
 * - Addition operations with type checking (e.g., "let z = x + y;")
 * - Ensures that both operands in an addition have the same type
 * - Throws CompileException if trying to add numbers of different types
 * - Boolean operations with logical OR (||) and logical AND (&&)
 * - Ensures that only Bool types are used with logical operators
 * - Throws CompileException if trying to use non-Bool types with logical operators
 */
public class Compiler {
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
	 * Checks if a raw value contains an addition operation and verifies type compatibility.
	 * If the raw value contains an addition (+), it extracts the operands and ensures they have the same type.
	 * <p>
	 * This method enforces the requirement that numbers being added must be of the same type,
	 * which prevents unintended type conversions and potential precision loss or overflow issues.
	 * The type checking works by:
	 * 1. Detecting the presence of a '+' operator in the value
	 * 2. Extracting the left and right operands
	 * 3. Checking if both operands are variable references (not literals)
	 * 4. Retrieving the types of both operands from the variableTypes map
	 * 5. Comparing the types and throwing an exception if they don't match
	 *
	 * @param rawValue the raw value to check
	 * @throws CompileException if the operands have different types
	 */
	private void checkAdditionTypeCompatibility(String rawValue) {
		if (rawValue.contains("+")) {
			String[] operands = rawValue.split("\\+");
			if (operands.length == 2) {
				String leftOperand = operands[0].trim();
				String rightOperand = operands[1].trim();

				// Check if both operands are variables (not literals)
				if (valueProcessor.isVariableReference(leftOperand) && valueProcessor.isVariableReference(rightOperand)) {
					// Get the types of both operands
					String leftType = variableTypes.get(leftOperand);
					String rightType = variableTypes.get(rightOperand);

					// Check if both types are defined and matching
					if (leftType != null && rightType != null && !leftType.equals(rightType)) {
						throw new CompileException(
								"Type mismatch in addition: Cannot add " + leftType + " variable '" + leftOperand + "' to " +
								rightType + " variable '" + rightOperand + "'. The two numbers must be of the same type.");
					}
				}
			}
		}
	}

	/**
	 * Checks if a raw value contains a boolean operation (|| or &&) and verifies type compatibility.
	 * This method extracts the operands and ensures they are both Bool type.
	 * <p>
	 * This method enforces the requirement that only boolean values can be used with logical operators,
	 * which prevents type errors and ensures logical operations behave as expected.
	 * The type checking works by:
	 * 1. Detecting the presence of the operator in the value
	 * 2. Extracting the left and right operands
	 * 3. Checking if the operands are variable references or boolean literals
	 * 4. For variable references, ensuring they have Bool type
	 *
	 * @param rawValue the raw value to check
	 * @param params   the parameters for the boolean operation
	 * @throws CompileException if any operand is not a Bool type
	 */
	private void checkBooleanOperationTypeCompatibility(String rawValue, BooleanOperationParams params) {
		String operator = params.operator();
		String operatorName = params.operatorName();

		if (rawValue.contains(operator)) {
			String[] operands;
			// Handle different operators with appropriate splitting
			if ("||".equals(operator)) {
				operands = rawValue.split("\\|\\|");
			} else if ("&&".equals(operator)) {
				operands = rawValue.split("&&");
			} else {
				return; // Unsupported operator
			}

			if (operands.length == 2) {
				String leftOperand = operands[0].trim();
				String rightOperand = operands[1].trim();

				// Check operands - either they are boolean literals or Bool type variables
				checkBooleanOperand(leftOperand, operatorName);
				checkBooleanOperand(rightOperand, operatorName);
			}
		}
	}

	/**
	 * Checks if a raw value contains a logical OR operation (||) and verifies type compatibility.
	 *
	 * @param rawValue the raw value to check
	 * @throws CompileException if any operand is not a Bool type
	 */
	private void checkLogicalOrTypeCompatibility(String rawValue) {
		checkBooleanOperationTypeCompatibility(rawValue, BooleanOperationParams.logicalOr());
	}

	/**
	 * Checks if a raw value contains a logical AND operation (&&) and verifies type compatibility.
	 *
	 * @param rawValue the raw value to check
	 * @throws CompileException if any operand is not a Bool type
	 */
	private void checkLogicalAndTypeCompatibility(String rawValue) {
		checkBooleanOperationTypeCompatibility(rawValue, BooleanOperationParams.logicalAnd());
	}

	/**
	 * Helper method to check if an operand is a boolean value (either a boolean literal or a Bool type variable).
	 *
	 * @param operand      the operand to check
	 * @param operatorName the name of the operator being used (for error messages)
	 * @throws CompileException if the operand is not a boolean value
	 */
	private void checkBooleanOperand(String operand, String operatorName) {
		// Check if operand is a boolean literal
		boolean isBooleanLiteral = operand.equals("true") || operand.equals("false");

		// If not a boolean literal, check if it's a variable reference
		if (!isBooleanLiteral && valueProcessor.isVariableReference(operand)) {
			String operandType = variableTypes.get(operand);

			// Check if the variable is defined and has Bool type
			if (operandType == null) {
				throw new CompileException("Undefined variable '" + operand + "' used in " + operatorName + " operation");
			}

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
		checkAdditionTypeCompatibility(rawValue);
		checkLogicalOrTypeCompatibility(rawValue);
		checkLogicalAndTypeCompatibility(rawValue);

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