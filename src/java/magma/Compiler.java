package magma;

public class Compiler {
	/**
	 * Helper function that generates C source code based on the input content.
	 *
	 * @param inputContent The content of the input file
	 * @return The generated C source code as a String
	 */
	public static String generateCSourceCode(String inputContent) {
		// If the input is empty, return a C program that outputs an empty string
		if (inputContent.isEmpty()) {
			return "#include <stdio.h>\n\nint main() {\n\treturn 0;\n}";
		}

		// If the input is a number, return a C program that outputs the same number
		try {
			int number = Integer.parseInt(inputContent.trim());
			return "#include <stdio.h>\n\nint main() {\n\tprintf(\"%d\", " + number + ");\n\treturn 0;\n}";
		} catch (NumberFormatException e) {
			// If the input is not a number, return a default C program
			return "#include <stdio.h>\n\nint main() {\n\treturn 0;\n}";
		}
	}
}
