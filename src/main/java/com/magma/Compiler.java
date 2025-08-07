package com.magma;

/**
 * A simple class that processes strings by doing nothing to them.
 */
public class Compiler {

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

		// Transform "let x = 0" to "int32_t x = 0;"
		if (input.startsWith("let ")) {
 		// Remove trailing semicolon if present
 		if (input.endsWith(";")) input = input.substring(0, input.length() - 1);
		
 		// Handle type annotations
 		String cType = "int32_t"; // Default type
		
 		if (input.contains(" : I8")) {
 			input = input.replace(" : I8", "");
 			cType = "int8_t";
 		} else if (input.contains(" : I16")) {
 			input = input.replace(" : I16", "");
 			cType = "int16_t";
 		} else if (input.contains(" : I32")) {
 			input = input.replace(" : I32", "");
 			cType = "int32_t";
 		} else if (input.contains(" : I64")) {
 			input = input.replace(" : I64", "");
 			cType = "int64_t";
 		} else if (input.contains(" : U8")) {
 			input = input.replace(" : U8", "");
 			cType = "uint8_t";
 		} else if (input.contains(" : U16")) {
 			input = input.replace(" : U16", "");
 			cType = "uint16_t";
 		} else if (input.contains(" : U32")) {
 			input = input.replace(" : U32", "");
 			cType = "uint32_t";
 		} else if (input.contains(" : U64")) {
 			input = input.replace(" : U64", "");
 			cType = "uint64_t";
 		}

 		String transformed = input.replaceFirst("let ", cType + " ");
			return transformed + ";";
		}

		return input;
	}
}