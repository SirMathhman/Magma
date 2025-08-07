package com.magma;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A simple class that processes strings by doing nothing to them.
 */
public class Compiler {
	private static final Map<String, String> TYPE_MAPPINGS = new HashMap<>();
	// Map to track defined variables and their C types
	private static final Map<String, String> definedVariables = new LinkedHashMap<>();

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
		// Clear the defined variables map at the beginning of processing
		definedVariables.clear();

		// Handle empty input
		if (input.isEmpty()) return "";

		// Trim leading and trailing whitespace
		input = input.trim();

		// Handle multiple statements separated by semicolons
		if (input.contains(";")) return processMultipleStatements(input);

		// Process single statement
		return processSingleStatement(input);
	}

	/**
	 * Processes multiple statements separated by semicolons.
	 *
	 * @param input The input string containing multiple statements
	 * @return The transformed string
	 * @throws CompileException if there's a compilation error in the input
	 */
	private static String processMultipleStatements(String input) throws CompileException {
		String[] statements = input.split(";");
		StringBuilder result = new StringBuilder();

		for (String statement : statements) {
			// Skip empty statements
			if (statement.trim().isEmpty()) continue;

			// Process single statement
			result.append(processSingleStatement(statement.trim()));
		}

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
		// Only transform "let" statements, otherwise return as is
		if (!input.startsWith("let ")) return input;

		// Transform "let x = 0" to "int32_t x = 0;"
		return transformLetStatement(input);
	}

	/**
	 * Transforms a "let" statement into a C-style declaration.
	 *
	 * @param input The input string starting with "let"
	 * @return The transformed C-style declaration
	 */
	private static String transformLetStatement(String input) throws CompileException {
		// Remove trailing semicolon if present
		if (input.endsWith(";")) input = input.substring(0, input.length() - 1);

		// Replace multiple whitespaces with a single space
		input = input.replaceAll("\\s+", " ");

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
			if (!definedVariables.containsKey(rightSide)) throw new CompileException("Undefined variable: " + rightSide);

			// Get the type of the referenced variable
			String referencedType = definedVariables.get(rightSide);

			// If there's an explicit type annotation, check for type compatibility
			if (!typeInfo.cType.equals(referencedType)) throw new CompileException(
					"Type mismatch: cannot assign " + getTypeNameFromCType(referencedType).orElse(referencedType) + " value to " +
					getTypeNameFromCType(typeInfo.cType).orElse(typeInfo.cType) + " variable");
		} else processedInput = processTypeSuffixes(processedInput, typeInfo.cType);

		// Replace "let" with the C type
		String transformed = processedInput.replaceFirst("let ", typeInfo.cType + " ");

		// Store the variable and its type
		definedVariables.put(variableName, typeInfo.cType);

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
		// Remove type suffixes from literals in the expression
		String processedExpression = expression;

		// Process nested expressions in parentheses first
		int openParenIndex = processedExpression.indexOf('(');
		while (openParenIndex != -1) {
			// Find the matching closing parenthesis
			int closeParenIndex = findMatchingClosingParen(processedExpression, openParenIndex);
			if (closeParenIndex == -1) throw new CompileException("Mismatched parentheses in expression: " + expression);

			// Extract the nested expression
			String nestedExpr = processedExpression.substring(openParenIndex + 1, closeParenIndex);

			// Process the nested expression recursively
			String processedNestedExpr = processArithmeticExpression(nestedExpr, expectedType);

			// Replace the nested expression (including parentheses) with the processed version
			processedExpression = processedExpression.substring(0, openParenIndex) + "(" + processedNestedExpr + ")" +
														processedExpression.substring(closeParenIndex + 1);

			// Look for the next opening parenthesis
			openParenIndex = processedExpression.indexOf('(', openParenIndex + 1);
		}

		// Check for type suffixes in the expression
		for (String typeName : TYPE_MAPPINGS.keySet()) {
			if (!processedExpression.contains(typeName)) continue;

			String cType = TYPE_MAPPINGS.get(typeName);
			if (!cType.equals(expectedType)) throw new CompileException(
					"Type mismatch in arithmetic expression: cannot mix " + typeName + " with " +
					getTypeNameFromCType(expectedType).orElse(expectedType));

			// Remove the type suffix
			processedExpression = processedExpression.replaceAll("(\\d+)" + typeName, "$1");
		}

		// Check for variable references in the expression
		for (Map.Entry<String, String> variable : definedVariables.entrySet()) {
			String varName = variable.getKey();
			String varType = variable.getValue();

			// Skip if variable not found in expression
			if (!processedExpression.matches(".*\\b" + varName + "\\b.*")) continue;

			// Check type compatibility
			if (!varType.equals(expectedType)) throw new CompileException(
					"Type mismatch in arithmetic expression: cannot mix " + getTypeNameFromCType(varType).orElse(varType) +
					" with " + getTypeNameFromCType(expectedType).orElse(expectedType));
		}

		return processedExpression;
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