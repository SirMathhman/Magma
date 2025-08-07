package com.magma;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A simple class that processes strings by doing nothing to them.
 */
public class Compiler {
	private static final Map<String, String> TYPE_MAPPINGS = new HashMap<>();

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
	 *
	 * @param input The input string (assumed to be non-null)
	 * @return The transformed string
	 * @throws CompileException if there's a compilation error in the input
	 */
	public static String process(String input) throws CompileException {
		if (input.isEmpty()) return "";

		// Trim leading and trailing whitespace
		input = input.trim();

		// Only transform "let" statements
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

		// Process type annotations and get the appropriate C type
		TypeInfo typeInfo = processTypeAnnotation(input);

		// Process type suffixes in literals
		String processedInput = processTypeSuffixes(typeInfo.processedInput, typeInfo.cType);

		// Replace "let" with the C type
		String transformed = processedInput.replaceFirst("let ", typeInfo.cType + " ");
		return transformed + ";";
	}

	/**
	 * Processes type annotations in the input and returns the C type and processed input.
	 *
	 * @param input The input string
	 * @return TypeInfo containing the C type and processed input
	 */
	private static TypeInfo processTypeAnnotation(String input) {
		// Default type if no annotation is found
		String cType = "int32_t";
		String processedInput = input;

		for (Map.Entry<String, String> entry : TYPE_MAPPINGS.entrySet()) {
			String pattern = "\\s*:\\s*" + entry.getKey() + "\\s*";
			if (processedInput.matches(".*" + pattern + ".*")) {
				processedInput = processedInput.replaceAll(pattern, " ");
				cType = entry.getValue();
				break;
			}
		}

		return new TypeInfo(cType, processedInput);
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
}