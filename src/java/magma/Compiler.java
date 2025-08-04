package magma;

import java.io.IOException;

public class Compiler {
	private static String generateProgram(String printfFormat, String value) {
		return "#include <stdio.h>\n\nint main() {\n\tprintf(" + printfFormat + ", " + value + ");\n\treturn 0;\n}";
	}

	private static String generatePrintfProgram(String value) {
		return generateProgram("\"%s\"", "\"" + value + "\"");
	}

	private static String generateFloatingPointProgram(double value) {
		return generateProgram("\"%.1f\"", String.valueOf(value));
	}

	public static String generateCSourceCode(String inputContent) throws IOException {
		// Debug: Print the input content
		System.out.println("DEBUG: Input content: '" + inputContent + "'");

		// If the input is empty, return a C program that outputs an empty string
		if (inputContent.isEmpty()) {
			return "#include <stdio.h>\n\nint main() {\n\treturn 0;\n}";
		}


		// Check for typed float with decimal (e.g., 100.0F32, 100.0F64)
		if (inputContent.matches("^\\d+\\.?\\d*F(32|64)$")) {
			// Extract the number part (before the type suffix)
			String numberPart = inputContent.replaceAll("^(\\d+\\.?\\d*)F(32|64)$", "$1");
			return generateFloatingPointProgram(Double.parseDouble(numberPart));
		}

		// Check for typed float without decimal (e.g., 100F32, 100F64)
		if (inputContent.matches("^\\d+F(32|64)$")) {
			// Extract the number part (before the type suffix)
			String numberPart = inputContent.replaceAll("^(\\d+)F(32|64)$", "$1");
			return generateFloatingPointProgram(Double.parseDouble(numberPart));
		}

		// Check for typed number (e.g., 100U8, 100I16)
		if (inputContent.matches("^\\d+(U8|U16|U32|U64|I8|I16|I32|I64)$")) {
			// Extract the number part (before the type suffix)
			String numberPart = inputContent.replaceAll("^(\\d+)(U8|U16|U32|U64|I8|I16|I32|I64)$", "$1");
			return generatePrintfProgram(numberPart);
		}

		// If the input is a number, return a C program that outputs the same number
		try {
			int number = Integer.parseInt(inputContent.trim());
			// The Main.processCProgram method reads the output line by line and adds a newline after each line.
			// It then returns the entire output as a string.
			// To make the test pass, we need to make the C program output the number in a way that,
			// when processed by Main.processCProgram, will result in just the number.
			// We'll use a trick: we'll make the C program output the number as a string without any formatting,
			// so that when Main.processCProgram reads it and adds a newline, it will be just the number.
			return generatePrintfProgram(String.valueOf(number));
		} catch (NumberFormatException e) {
			// Try to parse as a floating-point number
			try {
				double floatingPoint = Double.parseDouble(inputContent.trim());
				return generateFloatingPointProgram(floatingPoint);
			} catch (NumberFormatException e2) {

				// Check if the input is a character or string (enclosed in quotes)
				if ((inputContent.length() == 3 && inputContent.charAt(0) == '\'' && inputContent.charAt(2) == '\'') ||
						(inputContent.length() >= 2 && inputContent.charAt(0) == '\"' &&
						 inputContent.charAt(inputContent.length() - 1) == '\"')) {
					String value = inputContent.charAt(0) == '\'' ? String.valueOf(inputContent.charAt(1))
																												: inputContent.substring(1, inputContent.length() - 1);
					return generatePrintfProgram(value);
				}
				// If the input is not a number, character, or string, throw an IOException
				throw new IOException("Input file is not empty. Cannot proceed.");
			}
		}
	}
}
