package com.magma;

import java.util.HashMap;
import java.util.Map;

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
	 * @param input The input string
	 * @return The transformed string, empty string if input is empty, or null if input is null
	 */
	public static String process(String input) {
		if (input == null) return null;
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
	private static String transformLetStatement(String input) {
		// Remove trailing semicolon if present
		if (input.endsWith(";")) input = input.substring(0, input.length() - 1);

		// Replace multiple whitespaces with a single space
		input = input.replaceAll("\\s+", " ");

		// Process type annotations and get the appropriate C type
		TypeInfo typeInfo = processTypeAnnotation(input);

		// Replace "let" with the C type
		String transformed = typeInfo.processedInput.replaceFirst("let ", typeInfo.cType + " ");
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
}