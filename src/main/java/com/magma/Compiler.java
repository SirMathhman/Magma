package com.magma;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;

/**
 * A simple class that processes strings by doing nothing to them.
 */
public class Compiler {
	private static final Map<String, String> TYPE_MAPPINGS = new HashMap<>();
	// Stack of maps to track variables with their TypeInfo in each scope
	private static final Stack<Map<String, TypeInfo>> scopeStack = new Stack<>();

	static {
		TYPE_MAPPINGS.put("I8", "int8_t");
		TYPE_MAPPINGS.put("I16", "int16_t");
		TYPE_MAPPINGS.put("I32", "int32_t");
		TYPE_MAPPINGS.put("I64", "int64_t");
		TYPE_MAPPINGS.put("U8", "uint8_t");
		TYPE_MAPPINGS.put("U16", "uint16_t");
		TYPE_MAPPINGS.put("U32", "uint32_t");
		TYPE_MAPPINGS.put("U64", "uint64_t");
	}

	/**
	 * Processes a string input and transforms it according to Magma language rules.
	 * Currently transforms "let" variable declarations to C-style declarations.
	 * This is a non-recursive implementation.
	 *
	 * @param input The input string (assumed to be non-null)
	 * @return The transformed string
	 * @throws CompileException if there's a compilation error in the input
	 */
	public static String process(String input) throws CompileException {
		// Clear the scope stack at the beginning of processing
		scopeStack.clear();

		// Initialize the global scope
		enterScope();

		// Handle empty input
		if (input.isEmpty()) return "";

		// Trim leading and trailing whitespace
		input = input.trim();

		// Check for mismatched braces in the entire input
		if (input.contains("{") || input.contains("}")) checkForMismatchedBraces(input);

		// Check for code blocks
		if (input.contains("{")) return processBlock(input);

		// Handle multiple statements separated by semicolons
		if (input.contains(";")) return processMultipleStatements(input);

		// Process single statement
		return processSingleStatement(input);
	}

	/**
	 * Enters a new scope for variable visibility.
	 */
	private static void enterScope() {
		scopeStack.push(new LinkedHashMap<>());
	}

	/**
	 * Exits the current scope and removes variables defined in it.
	 */
	private static void exitScope() {
		if (scopeStack.isEmpty()) return;

		// Just pop the current scope - variables in this scope will be removed automatically
		scopeStack.pop();
	}

	/**
	 * Adds a variable to the current scope.
	 *
	 * @param varName  The name of the variable
	 * @param typeInfo The type information for the variable
	 */
	private static void addVariableToCurrentScope(String varName, TypeInfo typeInfo) {
		if (scopeStack.isEmpty()) enterScope();

		scopeStack.peek().put(varName, typeInfo);
	}

	/**
	 * Gets a variable's TypeInfo from any scope, starting from the current scope
	 * and moving outward to parent scopes.
	 *
	 * @param varName The name of the variable to find
	 * @return The TypeInfo for the variable, or null if not found
	 */
	private static Optional<TypeInfo> getVariable(String varName) {
		// Search from the current (innermost) scope outward
		for (int i = scopeStack.size() - 1; i >= 0; i--) {
			Map<String, TypeInfo> scope = scopeStack.get(i);
			if (scope.containsKey(varName)) return Optional.of(scope.get(varName));
		}

		return Optional.empty();
	}

	/**
	 * Checks if a variable does not exists in any scope.
	 *
	 * @param varName The name of the variable to check
	 * @return true if the variable does not, false otherwise
	 */
	private static boolean isUndefined(String varName) {
		return getVariable(varName).isEmpty();
	}

	/**
	 * Processes a code block delimited by curly braces.
	 *
	 * @param input The input string containing a code block
	 * @return The transformed C code
	 * @throws CompileException if there's a compilation error in the input
	 */
	private static String processBlock(String input) throws CompileException {
		// Check for mismatched braces
		checkForMismatchedBraces(input);

		// Find the opening and closing braces of the outermost block
		int openBraceIndex = input.indexOf('{');
		int closeBraceIndex = findMatchingClosingBrace(input, openBraceIndex);

		// Extract the content inside the block
		String blockContent = input.substring(openBraceIndex + 1, closeBraceIndex).trim();

		// Enter a new scope for the block
		enterScope();

		// Process the content inside the block
		StringBuilder result = new StringBuilder();
		result.append("{");

		// If the block is not empty
		if (!blockContent.isEmpty()) {
			// Process the content as multiple statements
			String processedContent = processMultipleStatements(blockContent);
			result.append(processedContent);
		}

		result.append("}");

		// Exit the scope for the block
		exitScope();

		return result.toString();
	}

	/**
	 * Checks for mismatched braces in an expression.
	 *
	 * @param expression The expression to check
	 * @throws CompileException if there are mismatched braces
	 */
	private static void checkForMismatchedBraces(String expression) throws CompileException {
		int count = 0;
		char[] chars = expression.toCharArray();

		for (char c : chars) {
			if (c == '{') count++;
			if (c == '}') count--;
			if (count < 0) throw new CompileException("Mismatched braces: unexpected closing brace");
		}

		if (count > 0) throw new CompileException("Mismatched braces: missing closing brace");
	}

	/**
	 * Finds the matching closing brace for an opening brace.
	 *
	 * @param expression     The expression containing braces
	 * @param openBraceIndex The index of the opening brace
	 * @return The index of the matching closing brace, or -1 if not found
	 */
	private static int findMatchingClosingBrace(String expression, int openBraceIndex) {
		int count = 1;
		for (int i = openBraceIndex + 1; i < expression.length(); i++) {
			char c = expression.charAt(i);
			if (c == '{') count++;
			if (c == '}') count--;
			if (count == 0) return i;
		}
		return -1;
	}

	/**
	 * Processes multiple statements separated by semicolons.
	 *
	 * @param input The input string containing multiple statements
	 * @return The transformed string
	 * @throws CompileException if there's a compilation error in the input
	 */
	private static String processMultipleStatements(String input) throws CompileException {
		// If no blocks, process simple statements
		if (!input.contains("{")) return processSimpleStatements(input);

		return processInputWithBlocks(input);
	}

	private static String processInputWithBlocks(String input) throws CompileException {
		StringBuilder result = new StringBuilder();
		int currentPos = 0;

		while (currentPos < input.length()) {
			TokenInfo tokenInfo = findNextToken(input, currentPos);

			// No more tokens to process
			if (tokenInfo.isEndOfInput()) {
				processRemainingInput(input, currentPos, result);
				break;
			}

			// Process the found token
			currentPos = processToken(input, currentPos, tokenInfo, result);
		}

		return result.toString();
	}

	private static TokenInfo findNextToken(String input, int startPos) {
		int nextOpenBrace = input.indexOf('{', startPos);
		int nextSemicolon = input.indexOf(';', startPos);

		return new TokenInfo(nextOpenBrace, nextSemicolon);
	}

	private static void processRemainingInput(String input, int currentPos, StringBuilder result)
			throws CompileException {
		String remaining = input.substring(currentPos).trim();
		if (!remaining.isEmpty()) result.append(processSingleStatement(remaining));
	}

	private static int processToken(String input, int currentPos, TokenInfo tokenInfo, StringBuilder result)
			throws CompileException {
		if (tokenInfo.isSemicolonNext()) return processSemicolonToken(input, currentPos, tokenInfo.semicolonPos(), result);

		return processOpenBraceToken(input, currentPos, tokenInfo.openBracePos(), result);
	}

	private static int processSemicolonToken(String input, int currentPos, int nextSemicolon, StringBuilder result)
			throws CompileException {
		String statement = input.substring(currentPos, nextSemicolon).trim();
		if (!statement.isEmpty()) result.append(processSingleStatement(statement));
		return nextSemicolon + 1;
	}

	private static int processOpenBraceToken(String input, int currentPos, int nextOpenBrace, StringBuilder result)
			throws CompileException {
		processTextBeforeBlock(input, currentPos, nextOpenBrace, result);
		return processBlockAndMovePosition(input, nextOpenBrace, result);
	}

	private static int processBlockAndMovePosition(String input, int openBracePos, StringBuilder result)
			throws CompileException {
		int closeBrace = findMatchingClosingBrace(input, openBracePos);
		if (closeBrace == -1) throw new CompileException("Mismatched braces: missing closing brace");

		String block = input.substring(openBracePos, closeBrace + 1);
		result.append(processBlock(block));

		int newPos = closeBrace + 1;
		if (newPos < input.length() && input.charAt(newPos) == ';') newPos++;

		return newPos;
	}

	private static void processTextBeforeBlock(String input, int currentPos, int nextOpenBrace, StringBuilder result)
			throws CompileException {
		String beforeBlock = input.substring(currentPos, nextOpenBrace).trim();
		if (beforeBlock.isEmpty()) return;

		if (beforeBlock.contains(";")) {
			processStatementsWithSemicolons(beforeBlock, result);
			return;
		}

		result.append(processSingleStatement(beforeBlock));
	}

	private static void processStatementsWithSemicolons(String input, StringBuilder result) throws CompileException {
		String[] statements = input.split(";");
		for (String statement : statements) processNonEmptyStatement(statement, result);
	}

	private static void processNonEmptyStatement(String statement, StringBuilder result) throws CompileException {
		String trimmed = statement.trim();
		// Add semicolon back to the statement before processing
		if (!trimmed.isEmpty()) result.append(processSingleStatement(trimmed + ";"));
	}

	private static String processSimpleStatements(String input) throws CompileException {
		String[] statements = input.split(";");
		StringBuilder result = new StringBuilder();

		for (String statement : statements) processNonEmptyStatement(statement, result);

		return result.toString();
	}

	/**
	 * Processes a single statement.
	 *
	 * @param input The input string containing a single statement
	 * @return The transformed string
	 * @throws CompileException if there's a compilation error in the input
	 */
	private static String processSingleStatement(String input) throws CompileException {
		// Clean the input by removing any leading/trailing whitespace
		input = input.trim();

		// Handle "let" statements
		// Transform "let x = 0" to "int32_t x = 0;"
		if (input.startsWith("let ")) return transformLetStatement(input);

		// Handle assignment statements (e.g., "x = 10")
		if (input.contains("=") && !input.contains("==")) return transformAssignmentStatement(input);

		// Return other statements as is
		return input;
	}

	/**
	 * Transforms an assignment statement (e.g., "x = 10") into a C-style assignment.
	 *
	 * @param input The input string containing an assignment
	 * @return The transformed C-style assignment
	 * @throws CompileException if the variable is not defined or not mutable
	 */
	private static String transformAssignmentStatement(String input) throws CompileException {
		// Trim whitespace first
		input = input.trim();

		// Check if the statement ends with a semicolon
		if (!input.endsWith(";")) throw new CompileException("Missing semicolon at the end of assignment statement");

		// Remove trailing semicolon for processing
		input = input.substring(0, input.length() - 1);

		// Replace multiple whitespaces with a single space
		input = input.replaceAll("\\s+", " ");

		// Split the input by the equals sign
		String[] parts = input.split("=", 2);
		if (parts.length != 2) throw new CompileException("Invalid assignment statement: " + input);

		// Extract variable name and value
		String variableName = parts[0].trim();
		String value = parts[1].trim();

		// Check if the variable exists - remove any leading/trailing whitespace
		variableName = variableName.trim();
		if (isUndefined(variableName)) throw new CompileException("Cannot assign to undefined variable: " + variableName);

		// Check if the variable is mutable
		Optional<TypeInfo> varInfo = getVariable(variableName);
		if (varInfo.isEmpty()) throw new CompileException("Cannot assign to undefined variable: " + variableName);

		final var typeInfo = varInfo.get();
		if (!typeInfo.isMutable) throw new CompileException("Cannot assign to immutable variable: " + variableName);

		// Get the variable's type
		String variableType = typeInfo.cType;

		// Check for mismatched parentheses in the value
		checkForMismatchedParentheses(value);

		// Process the value (handle arithmetic expressions and type checking)
		if (value.contains("+") || value.contains("-") || value.contains("*"))
			value = processArithmeticExpression(value, variableType);
		else if (!value.matches(".*\\d+.*")) {
			// If the value is a variable reference
			if (isUndefined(value)) throw new CompileException("Undefined variable: " + value);

			// Check type compatibility
			final var variable = getVariable(value);
			if (variable.isEmpty()) throw new CompileException("Undefined variable: " + value);
			String valueType = variable.get().cType;
			if (!valueType.equals(variableType)) throw new CompileException(
					"Type mismatch: cannot assign " + getTypeNameFromCType(valueType).orElse(valueType) + " value to " +
					getTypeNameFromCType(variableType).orElse(variableType) + " variable");
		} else {
			// Process type suffixes for literals
			String processedInput = variableName + " = " + value;
			processedInput = processTypeSuffixes(processedInput, variableType);
			// Extract the processed value
			parts = processedInput.split("=", 2);
			value = parts[1].trim();
		}

		return variableName + " = " + value + ";";
	}

	/**
	 * Transforms a "let" statement into a C-style declaration.
	 *
	 * @param input The input string starting with "let"
	 * @return The transformed C-style declaration
	 */
	private static String transformLetStatement(String input) throws CompileException {
		// Trim whitespace first
		input = input.trim();

		// Check if the statement ends with a semicolon
		if (!input.endsWith(";")) throw new CompileException("Missing semicolon at the end of let statement");

		// Remove trailing semicolon for processing
		input = input.substring(0, input.length() - 1);

		// Replace multiple whitespaces with a single space
		input = input.replaceAll("\\s+", " ");

		// Check if the variable is mutable
		boolean isMutable = input.startsWith("let mut ");

		// Remove the "mut" keyword if present
		if (isMutable) input = "let " + input.substring(8);

		// Extract variable name
		String variableName = extractVariableName(input);
		if (variableName.isEmpty()) throw new CompileException("Invalid let statement: missing variable name");

		// Process type annotations and get the appropriate C type
		TypeInfo typeInfo = processTypeAnnotation(input);

		// Check if the right side of the assignment is a variable reference or expression
		String processedInput = typeInfo.processedInput;
		String rightSide = extractRightSide(processedInput);

		// Check for mismatched parentheses before processing
		checkForMismatchedParentheses(rightSide);

		// Check if the right side contains arithmetic operators
		if (rightSide.contains("+") || rightSide.contains("-") || rightSide.contains("*")) {
			// Process arithmetic expression
			rightSide = processArithmeticExpression(rightSide, typeInfo.cType);
			// Update the processed input with the processed right side
			int equalsPos = processedInput.indexOf('=');
			processedInput = processedInput.substring(0, equalsPos + 1) + " " + rightSide;
		}
		// If the right side is a variable reference (not a literal with digits)
		else if (!rightSide.matches(".*\\d+.*")) {
			// Check if the referenced variable exists
			if (isUndefined(rightSide)) throw new CompileException("Undefined variable: " + rightSide);

			// Get the type of the referenced variable
			Optional<TypeInfo> varInfo = getVariable(rightSide);
			if (varInfo.isEmpty()) throw new CompileException("Undefined variable: " + rightSide);
			String referencedType = varInfo.get().cType;

			// If there's an explicit type annotation, check for type compatibility
			if (!typeInfo.cType.equals(referencedType)) throw new CompileException(
					"Type mismatch: cannot assign " + getTypeNameFromCType(referencedType).orElse(referencedType) + " value to " +
					getTypeNameFromCType(typeInfo.cType).orElse(typeInfo.cType) + " variable");
		} else processedInput = processTypeSuffixes(processedInput, typeInfo.cType);

		// Replace "let" with the C type
		String transformed = processedInput.replaceFirst("let ", typeInfo.cType + " ");

		// Store the variable with its type and mutability status and add it to the current scope
		addVariableToCurrentScope(variableName, new TypeInfo(typeInfo.cType, typeInfo.processedInput, isMutable));

		return transformed + ";";
	}

	/**
	 * Checks for mismatched parentheses in an expression.
	 *
	 * @param expression The expression to check
	 * @throws CompileException if there are mismatched parentheses
	 */
	private static void checkForMismatchedParentheses(String expression) throws CompileException {
		int count = 0;
		for (char c : expression.toCharArray()) count = updateParenthesesCount(count, c);
		checkForMissingClosingParentheses(count);
	}

	private static int updateParenthesesCount(int count, char c) throws CompileException {
		if (c == '(') return count + 1;

		if (c == ')') {
			count--;
			checkForUnexpectedClosingParenthesis(count);
		}

		return count;
	}

	private static void checkForUnexpectedClosingParenthesis(int count) throws CompileException {
		if (count < 0) throw new CompileException("Mismatched parentheses: unexpected closing parenthesis");
	}

	private static void checkForMissingClosingParentheses(int count) throws CompileException {
		if (count > 0) throw new CompileException("Mismatched parentheses: missing closing parenthesis");
	}

	/**
	 * Processes type annotations in the input and returns the C type and processed input.
	 * Also infers type from literal suffixes if no explicit type annotation is provided.
	 *
	 * @param input The input string
	 * @return TypeInfo containing the C type and processed input
	 */
	private static TypeInfo processTypeAnnotation(String input) {
		// First check for explicit type annotations
		return findExplicitTypeAnnotation(input).orElseGet(() -> {
			// If no explicit type annotation, check for type suffixes in literals
			// Default type if no annotation is found
			final var cType = findTypeFromLiteralSuffix(input).orElse("int32_t");
			return new TypeInfo(cType, input);
		});
	}

	/**
	 * Searches for explicit type annotations in the input.
	 *
	 * @param input The input string
	 * @return TypeInfo if an explicit type is found, null otherwise
	 */
	private static Optional<TypeInfo> findExplicitTypeAnnotation(String input) {
		for (Map.Entry<String, String> entry : TYPE_MAPPINGS.entrySet()) {
			String pattern = "\\s*:\\s*" + entry.getKey() + "\\s*";
			if (input.matches(".*" + pattern + ".*")) {
				String processedInput = input.replaceAll(pattern, " ");
				return Optional.of(new TypeInfo(entry.getValue(), processedInput));
			}
		}
		return Optional.empty();
	}

	/**
	 * Infers type from literal suffixes in the input.
	 *
	 * @param input The input string
	 * @return Optional containing the C type if a type suffix is found, empty Optional otherwise
	 */
	private static Optional<String> findTypeFromLiteralSuffix(String input) {
		for (Map.Entry<String, String> entry : TYPE_MAPPINGS.entrySet())
			if (input.matches(".*\\s=\\s+\\d+" + entry.getKey() + ".*")) return Optional.of(entry.getValue());
		return Optional.empty();
	}

	/**
	 * Processes type suffixes in literals (e.g., 0U8, 42I16) and removes them.
	 * Also checks for type mismatches between variable type and literal type.
	 *
	 * @param input        The input string
	 * @param declaredType The declared type of the variable
	 * @return The processed input with type suffixes removed
	 * @throws CompileException if there's a type mismatch
	 */
	private static String processTypeSuffixes(String input, String declaredType) throws CompileException {
		// Find the Magma type name from the C type
		Optional<String> declaredTypeNameOpt = getTypeNameFromCType(declaredType);
		String declaredTypeName =
				declaredTypeNameOpt.orElseThrow(() -> new CompileException("Unknown C type: " + declaredType));

		// Check for type suffixes in literals and verify type compatibility
		checkTypeMismatch(input, declaredTypeName);

		// Replace literals with type suffixes (e.g., 0U8, 42I16)
		return removeTypeSuffixes(input);
	}

	/**
	 * Gets the Magma type name from the C type.
	 *
	 * @param cType The C type
	 * @return Optional containing the corresponding Magma type name, or empty Optional if not found
	 */
	private static Optional<String> getTypeNameFromCType(String cType) {
		for (Map.Entry<String, String> entry : TYPE_MAPPINGS.entrySet())
			if (entry.getValue().equals(cType)) return Optional.of(entry.getKey());
		return Optional.empty();
	}

	/**
	 * Checks for type mismatches between variable type and literal type.
	 *
	 * @param input            The input string
	 * @param declaredTypeName The declared type name
	 * @throws CompileException if there's a type mismatch
	 */
	private static void checkTypeMismatch(String input, String declaredTypeName) throws CompileException {
		for (String typeName : TYPE_MAPPINGS.keySet())
			if (input.matches(".*\\s=\\s+\\d+" + typeName + ".*") && !typeName.equals(declaredTypeName))
				throw new CompileException(
						"Type mismatch: cannot assign " + typeName + " value to " + declaredTypeName + " variable");
	}

	/**
	 * Removes type suffixes from literals.
	 *
	 * @param input The input string
	 * @return The input with type suffixes removed
	 */
	private static String removeTypeSuffixes(String input) {
		String result = input;
		for (String typeName : TYPE_MAPPINGS.keySet()) result = result.replaceAll("(\\d+)" + typeName, "$1");
		return result;
	}

	/**
	 * Extracts the variable name from a let statement.
	 *
	 * @param input The input string starting with "let"
	 * @return The variable name
	 */
	private static String extractVariableName(String input) {
		// Remove "let " prefix
		String withoutLet = input.substring(4).trim();

		// Find the position of the first space or colon
		int spacePos = withoutLet.indexOf(' ');
		int colonPos = withoutLet.indexOf(':');

		// Determine the end position of the variable name
		int endPos;
		if (spacePos == -1 && colonPos == -1) return ""; // Invalid format
		if (spacePos == -1) endPos = colonPos;
		else if (colonPos == -1) endPos = spacePos;
		else endPos = Math.min(spacePos, colonPos);

		return withoutLet.substring(0, endPos).trim();
	}

	/**
	 * Extracts the right side of the assignment from a let statement.
	 *
	 * @param input The input string starting with "let"
	 * @return The right side of the assignment
	 */
	private static String extractRightSide(String input) {
		// Find the position of the equals sign
		int equalsPos = input.indexOf('=');
		if (equalsPos == -1) return ""; // Invalid format

		// Return everything after the equals sign, trimmed
		return input.substring(equalsPos + 1).trim();
	}

	/**
	 * Processes an arithmetic expression, checking types and validating variables.
	 * Supports addition (+), subtraction (-), and multiplication (*) operations.
	 * Supports arbitrary nesting of expressions using parentheses.
	 *
	 * @param expression   The arithmetic expression to process
	 * @param expectedType The expected type of the result
	 * @return The processed expression
	 * @throws CompileException if there's a type mismatch or undefined variable
	 */
	private static String processArithmeticExpression(String expression, String expectedType) throws CompileException {
		// Process nested expressions in parentheses first
		String processedExpression = processNestedParentheses(expression, expectedType);

		// Process type suffixes in the expression
		processedExpression = processTypeSuffixesInExpression(processedExpression, expectedType);

		// Validate variable references in the expression
		validateVariableReferencesInExpression(processedExpression, expectedType);

		return processedExpression;
	}

	/**
	 * Processes nested expressions in parentheses.
	 *
	 * @param expression   The expression to process
	 * @param expectedType The expected type of the result
	 * @return The processed expression with nested expressions handled
	 * @throws CompileException if there's a syntax error in the expression
	 */
	private static String processNestedParentheses(String expression, String expectedType) throws CompileException {
		String processedExpression = expression;
		int openParenIndex = processedExpression.indexOf('(');

		while (openParenIndex != -1) {
			int closeParenIndex = findMatchingClosingParen(processedExpression, openParenIndex);
			if (closeParenIndex == -1) throw new CompileException("Mismatched parentheses in expression: " + expression);

			String nestedExpr = processedExpression.substring(openParenIndex + 1, closeParenIndex);
			String processedNestedExpr = processArithmeticExpression(nestedExpr, expectedType);

			processedExpression = processedExpression.substring(0, openParenIndex) + "(" + processedNestedExpr + ")" +
														processedExpression.substring(closeParenIndex + 1);

			openParenIndex = processedExpression.indexOf('(', openParenIndex + 1);
		}

		return processedExpression;
	}

	/**
	 * Processes type suffixes in an arithmetic expression.
	 *
	 * @param expression   The expression to process
	 * @param expectedType The expected type of the result
	 * @return The processed expression with type suffixes removed
	 * @throws CompileException if there's a type mismatch
	 */
	private static String processTypeSuffixesInExpression(String expression, String expectedType)
			throws CompileException {
		String processedExpression = expression;

		for (String typeName : TYPE_MAPPINGS.keySet()) {
			if (!processedExpression.contains(typeName)) continue;

			String cType = TYPE_MAPPINGS.get(typeName);
			if (!cType.equals(expectedType)) throw new CompileException(
					"Type mismatch in arithmetic expression: cannot mix " + typeName + " with " +
					getTypeNameFromCType(expectedType).orElse(expectedType));

			// Remove the type suffix
			processedExpression = processedExpression.replaceAll("(\\d+)" + typeName, "$1");
		}

		return processedExpression;
	}

	/**
	 * Validates variable references in an arithmetic expression.
	 *
	 * @param expression   The expression to validate
	 * @param expectedType The expected type of the result
	 * @throws CompileException if there's a type mismatch or undefined variable
	 */
	private static void validateVariableReferencesInExpression(String expression, String expectedType)
			throws CompileException {
		// Check each scope from innermost to outermost
		for (int i = scopeStack.size() - 1; i >= 0; i--)
			validateVariablesInScope(scopeStack.get(i), expression, expectedType);
	}

	/**
	 * Validates variables from a specific scope in an expression.
	 *
	 * @param scope        The scope containing variables to check
	 * @param expression   The expression to validate
	 * @param expectedType The expected type of the result
	 * @throws CompileException if there's a type mismatch
	 */
	private static void validateVariablesInScope(Map<String, TypeInfo> scope, String expression, String expectedType)
			throws CompileException {
		for (Map.Entry<String, TypeInfo> variable : scope.entrySet()) {
			String varName = variable.getKey();

			// Skip if variable not found in expression
			if (!expression.matches(".*\\b" + varName + "\\b.*")) continue;

			String varType = variable.getValue().cType;
			if (!varType.equals(expectedType)) throw new CompileException(
					"Type mismatch in arithmetic expression: cannot mix " + getTypeNameFromCType(varType).orElse(varType) +
					" with " + getTypeNameFromCType(expectedType).orElse(expectedType));
		}
	}

	/**
	 * Finds the matching closing parenthesis for an opening parenthesis.
	 *
	 * @param expression     The expression containing parentheses
	 * @param openParenIndex The index of the opening parenthesis
	 * @return The index of the matching closing parenthesis, or -1 if not found
	 */
	private static int findMatchingClosingParen(String expression, int openParenIndex) {
		int count = 1;
		for (int i = openParenIndex + 1; i < expression.length(); i++) {
			char c = expression.charAt(i);
			if (c == '(') {
				count++;
				continue;
			}

			if (c != ')') continue;

			count--;
			if (count == 0) return i;
		}
		return -1;
	}
}