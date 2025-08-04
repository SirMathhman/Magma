package magma;

import java.io.IOException;

public class Compiler {
	/**
	 * Helper function that generates C source code based on the input content.
	 *
	 * @param inputContent The content of the input file
	 * @return The generated C source code as a String
	 * @throws IOException If the input is not empty and not a number
	 */
	public static String generateCSourceCode(String inputContent) throws IOException {
		// If the input is empty, return a C program that outputs an empty string
		if (inputContent.isEmpty()) {
			return "#include <stdio.h>\n\nint main() {\n\treturn 0;\n}";
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
			return "#include <stdio.h>\n\nint main() {\n\tprintf(\"%s\", \"" + number + "\");\n\treturn 0;\n}";
		} catch (NumberFormatException e) {
			// Check if the input is a character enclosed in single quotes (e.g., 'a')
			if (inputContent.length() == 3 && inputContent.charAt(0) == '\'' && inputContent.charAt(2) == '\'') {
				char character = inputContent.charAt(1);
				return "#include <stdio.h>\n\nint main() {\n\tprintf(\"%s\", \"" + character + "\");\n\treturn 0;\n}";
			}
			// Check if the input is a string enclosed in double quotes (e.g., "first")
			if (inputContent.length() >= 2 && inputContent.charAt(0) == '\"' && inputContent.charAt(inputContent.length() - 1) == '\"') {
				String string = inputContent.substring(1, inputContent.length() - 1);
				return "#include <stdio.h>\n\nint main() {\n\tprintf(\"%s\", \"" + string + "\");\n\treturn 0;\n}";
			}
			// If the input is not a number, character, or string, throw an IOException
			throw new IOException("Input file is not empty. Cannot proceed.");
		}
	}
}
