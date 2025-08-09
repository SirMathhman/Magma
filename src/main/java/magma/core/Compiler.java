package magma.core;

import magma.declaration.DeclarationConfig;
import magma.declaration.DeclarationContext;
import magma.declaration.DeclarationProcessor;
import magma.params.ComparisonValidatorParams;
import magma.params.IfStatementParams;
import magma.params.TypeScriptAnnotationParams;
import magma.validation.ArithmeticValidator;
import magma.validation.BooleanExpressionValidator;
import magma.validation.ComparisonValidator;
import magma.validation.IfStatementValidator;
import magma.validation.OperatorChecker;

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
 * - Boolean operations with logical OR (||) and logical AND (&&):
 * - Ensures that only Bool types are used with logical operators
 * - Throws CompileException if trying to use non-Bool types with logical operators
 * - Comparison operations (==, !=, <, >, <=, >=):
 * - Equality operators (== and !=) work with both numeric and boolean types
 * - Relational operators (<, >, <=, >=) work only with numeric types
 * - All comparison operators return a Bool result
 * - Ensures that operands being compared are of the same type
 * - Throws CompileException if trying to compare values of different types
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

	// Validator classes for different types of operations
	private final ArithmeticValidator arithmeticValidator;
	private final BooleanExpressionValidator booleanValidator;
	private final ComparisonValidator comparisonValidator;

	/**
	 * Creates a new Compiler instance.
	 * Initializes the helper classes and connects them to share the variable types and mutability maps.
	 */
	public Compiler() {
		TypeMapper typeMapper = new TypeMapper();
		DeclarationConfig config = new DeclarationConfig(typeMapper, valueProcessor, variableTypes, variableMutability);
		this.declarationProcessor = new DeclarationProcessor(config);

		// Initialize validator classes
		OperatorChecker operatorChecker = new OperatorChecker();
		this.arithmeticValidator = new ArithmeticValidator(valueProcessor, variableTypes);
		this.booleanValidator = new BooleanExpressionValidator(valueProcessor, variableTypes);

		// Create parameters for comparison validator
		ComparisonValidatorParams comparisonParams =
				new ComparisonValidatorParams(valueProcessor, variableTypes, arithmeticValidator);
		this.comparisonValidator = new ComparisonValidator(comparisonParams);
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
		// Skip if statements - they're not reassignments
		if (statement.trim().startsWith("if (")) {
			return null;
		}
		
		// Check if this is a reassignment
		if (!statement.trim().startsWith("let") && statement.contains("=")) {
			String variableName = statement.substring(0, statement.indexOf("=")).trim();

			// Check if the variable exists
			if (!variableTypes.containsKey(variableName))
				throw new CompileException("Cannot reassign undefined variable '" + variableName + "'");

			// Check if the variable is mutable
			Boolean isMutable = variableMutability.get(variableName);
			if (isMutable == null || !isMutable)
				throw new CompileException("Cannot reassign immutable variable '" + variableName + "'");

			// If the variable is mutable, return the reassignment as is
			return statement;
		}

		// Not a reassignment
		return null;
	}


	/**
	 * Processes a single statement and returns its C equivalent.
	 *
	 * @param statement the statement to process
	 * @return the C equivalent of the statement
	 */
	private String processStatement(String statement) {
		System.out.println("[DEBUG_LOG] Processing statement: " + statement);
		
		// Check if this is a reassignment
		String reassignmentResult = processReassignment(statement);
		if (reassignmentResult != null) return reassignmentResult;
		
		// Check if this is an if statement
		if (statement.trim().startsWith("if (")) {
			System.out.println("[DEBUG_LOG] Detected if statement, processing...");
			return processIfStatement(statement);
		}

		// Create a context from the statement
		DeclarationContext context = declarationProcessor.createContext(statement);
		String variableName = context.variableName();
		String valueSection = context.valueSection();

		// Extract the raw value
		String rawValue = valueProcessor.extractRawValue(valueSection);

		// Check for operations and verify type compatibility using validator classes
		arithmeticValidator.checkArithmeticTypeCompatibility(rawValue);
		booleanValidator.checkLogicalOperations(rawValue);
		comparisonValidator.checkComparisonOperations(rawValue);

		// Handle TypeScript-style declarations with type annotations (e.g., "let x : I32 = 0;")
		if (statement.contains(" : ")) return declarationProcessor.processTypeScriptAnnotation(
				new TypeScriptAnnotationParams(statement, context, variableName, rawValue));

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
	 * Processes an if statement or if-else statement.
	 * Validates the statement syntax and processes the body.
	 *
	 * @param statement the if statement or if-else statement to process
	 * @return the processed statement
	 * @throws CompileException if the statement is invalid
	 */
	private String processIfStatement(String statement) {
		System.out.println("[DEBUG_LOG] Processing if statement: " + statement);
		
		// Create parameters for the if statement validator
		IfStatementParams params = new IfStatementParams(
			statement,
			valueProcessor,
			variableTypes,
			booleanValidator
		);
		
		// Create validator and validate the if statement
		IfStatementValidator validator = new IfStatementValidator(params);
		String validatedIfStatement = validator.validateIfStatement();
		
		System.out.println("[DEBUG_LOG] Validated if statement: " + validatedIfStatement);
		
		boolean hasElseBlock = params.hasElseBlock();
		
		// Extract the if body
		String ifBody = params.extractIfBody();
		System.out.println("[DEBUG_LOG] Extracted if body: " + ifBody);
		
		// Check if the if body contains variable declarations or other statements that need processing
		if (ifBody != null && ifBody.contains("let ")) {
			// Process the if body
			String processedIfBody = processBodyStatements(ifBody);
			
			// If there's an else block, process it too
			if (hasElseBlock) {
				String elseBody = params.extractElseBody();
				System.out.println("[DEBUG_LOG] Extracted else body: " + elseBody);
				
				String processedElseBody = elseBody != null && elseBody.contains("let ") 
					? processBodyStatements(elseBody) 
					: elseBody;
				
				// Rebuild the if-else statement with processed bodies
				return "if (" + params.extractCondition() + ") { " + processedIfBody + "; } else { " + processedElseBody + "; }";
			}
			
			// Rebuild the if statement with processed body (no else)
			return "if (" + params.extractCondition() + ") { " + processedIfBody + "; }";
		}
		
		return validatedIfStatement;
	}
	
	/**
	 * Processes statements in the body of an if or else block.
	 *
	 * @param body the body containing statements to process
	 * @return the processed body content
	 */
	private String processBodyStatements(String body) {
		// Process each statement in the body
		String[] bodyStatements = body.split(";");
		StringBuilder processedBody = new StringBuilder();
		
		for (String bodyStatement : bodyStatements) {
			String trimmed = bodyStatement.trim();
			if (!trimmed.isEmpty()) {
				// Process the statement
				String processed = processStatement(trimmed);
				// Remove any trailing semicolon
				if (processed.endsWith(";")) {
					processed = processed.substring(0, processed.length() - 1);
				}
				processedBody.append(processed).append("; ");
			}
		}
		
		// Remove the last space and semicolon if present
		String processedBodyString = processedBody.toString().trim();
		if (processedBodyString.endsWith(";")) {
			processedBodyString = processedBodyString.substring(0, processedBodyString.length() - 1);
		}
		
		return processedBodyString;
	}
	
	/**
	 * Processes if statements or if-else statements within a larger syntax.
	 * This method handles cases where if statements or if-else statements appear inside other code.
	 * 
	 * Key capabilities:
	 * - Handles standalone if statements or if-else statements
	 * - Processes complex inputs with variable declarations followed by if-else statements
	 * - Properly maintains semicolons and formatting in multi-statement code
	 * - Ensures proper nesting of if-else blocks
	 * - Handles recursive processing of statements within if and else blocks
	 *
	 * @param input the input containing if statements or if-else statements
	 * @return the processed input with compiled if statements or if-else statements
	 */
	private String processIfStatementSyntax(String input) {
		System.out.println("[DEBUG_LOG] Processing if statement syntax: " + input);
		
		// If the entire input is a single if statement, process it directly
		if (input.trim().startsWith("if (")) {
			return processIfStatement(input);
		}
		
		// Special handling for inputs with variable declarations followed by if statements
		if (input.contains("let ") && input.contains("if (")) {
			// For compound statements like "let x : Bool = true; if (x) { ... }"
			if (input.contains(";")) {
				String[] parts = input.split(";");
				StringBuilder result = new StringBuilder();
				StringBuilder ifStatementBuilder = new StringBuilder();
				boolean ifStarted = false;
				
				for (int i = 0; i < parts.length; i++) {
					String part = parts[i].trim();
					if (part.isEmpty()) continue;
					
					if (part.startsWith("if (") || ifStarted) {
						// If we've already started building an if statement, append this part
						ifStarted = true;
						ifStatementBuilder.append(part);
						// If this part ends with a closing brace, it might be the end of the if or else block
						if (part.endsWith("}")) {
							// Check if this is the end of an if-else statement
							boolean hasElse = false;
							for (int j = i + 1; j < parts.length; j++) {
								if (parts[j].trim().startsWith("else ")) {
									hasElse = true;
									break;
								}
							}
							
							if (!hasElse) {
								// This is the end of the if statement
								String processedIf = processIfStatement(ifStatementBuilder.toString());
								result.append(processedIf);
								ifStarted = false;
								ifStatementBuilder = new StringBuilder();
								
								// Add semicolon if not the last part
								if (i < parts.length - 1) {
									result.append("; ");
								}
							} else {
								// Continue building the if-else statement
								ifStatementBuilder.append("; ");
							}
						} else {
							// Continue building the if statement
							ifStatementBuilder.append("; ");
						}
					} else {
						// Process variable declarations or other statements
						String processed = processStatement(part);
						
						// Remove any trailing semicolons to avoid double semicolons
						if (processed.endsWith(";")) {
							processed = processed.substring(0, processed.length() - 1);
						}
						
						result.append(processed);
						
						// Add semicolon if not the last part
						if (i < parts.length - 1) {
							result.append("; ");
						}
					}
				}
				
				return result.toString();
			}
		}
		
		// For more complex inputs with multiple statements including if statements,
		// we need to parse them one by one
		String[] statements = input.split(";");
		StringBuilder result = new StringBuilder();
		
		for (int i = 0; i < statements.length; i++) {
			String statement = statements[i].trim();
			if (statement.isEmpty()) continue;
			
			String processed;
			if (statement.startsWith("if (")) {
				processed = processIfStatement(statement);
			} else {
				processed = processStatement(statement);
			}
			
			// Remove any trailing semicolon
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
		
		return result.toString();
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
		if (input == null) return null;

		System.out.println("[DEBUG_LOG] Compiling: " + input);
		
		// Register boolean literals as Bool type
		variableTypes.put("true", "Bool");
		variableTypes.put("false", "Bool");

		// Clear other variable types before processing
		variableTypes.keySet().removeIf(key -> !key.equals("true") && !key.equals("false"));
		
		// Process if statements (standalone or with variable declarations)
		if (input.contains("if (") && input.contains("{") && input.contains("}")) {
			return processIfStatementSyntax(input);
		}

		// Handle multiple statements
		if (input.contains(";")) {
			String[] statements = input.split(";");
			StringBuilder result = new StringBuilder();

			for (int i = 0; i < statements.length; i++) {
				String statement = statements[i].trim();
				if (!statement.isEmpty()) {
					// Process the statement, but remove any trailing semicolon
					String processed = processStatement(statement);
					if (processed.endsWith(";")) processed = processed.substring(0, processed.length() - 1);

					result.append(processed);

					// Add semicolon and space if not the last statement
					if (i < statements.length - 1) result.append("; ");
					else result.append(";");
				}
			}

			return result.toString();
		}

		// Handle a single statement
		return processStatement(input);
	}
}
