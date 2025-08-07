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
			String transformed = input.replaceFirst("let ", "int32_t ");
			return transformed + ";";
		}

		return input;
	}
}