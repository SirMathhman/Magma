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

		// Transform "let x = 0" to "int32_t x = 0;"
		if (input.startsWith("let ")) {
			// Remove trailing semicolon if present
			if (input.endsWith(";")) input = input.substring(0, input.length() - 1);

			// Replace multiple whitespaces with a single space
			input = input.replaceAll("\\s+", " ");

			// Handle type annotations
			String cType = "int32_t"; // Default type

			for (Map.Entry<String, String> entry : TYPE_MAPPINGS.entrySet()) {
				String pattern = "\\s*:\\s*" + entry.getKey() + "\\s*";
				if (input.matches(".*" + pattern + ".*")) {
					input = input.replaceAll(pattern, " ");
					cType = entry.getValue();
					break;
				}
			}

			String transformed = input.replaceFirst("let ", cType + " ");
			return transformed + ";";
		}

		return input;
	}
}