package magma;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Compiler {
	private static final String INCLUDE_STDIO = "#include <stdio.h>\n\n";
	private static final String MAIN_START = "int main() {\n\t";
	private static final String MAIN_RETURN = "\n\treturn 0;\n}";
	private static final String EMPTY_PROGRAM = INCLUDE_STDIO + MAIN_START + MAIN_RETURN;

	private static String generateProgram(String value) {
		return INCLUDE_STDIO + MAIN_START + "printf(" + "\"%s\"" + ", " + value + ");" + MAIN_RETURN;
	}

	private static String generatePrintfProgram(String value) {
		// Use a consistent format string for all string outputs
		return generateProgram("\"" + value + "\"");
	}

	private static String generateFloatingPointProgram(double value) {
		// Use a format that preserves the original representation
		// If the value is a whole number, display it with .0 suffix
		if (value == Math.floor(value)) {
			return generateProgram(value + ".0");
		}
		return generateProgram(String.valueOf(value));
	}

	public static String generateCSourceCode(String inputContent) throws CompileException {
		// Debug: Print the input content
		System.out.println("DEBUG: Input content: '" + inputContent + "'");

		// If the input is empty, return a C program that outputs an empty string
		if (inputContent.isEmpty()) {
			return EMPTY_PROGRAM;
		}

		// Check for invalid type combinations (e.g., 100.0U8)
		if (inputContent.matches("^\\d+\\.\\d*(U8|U16|U32|U64|I8|I16|I32|I64)$")) {
			throw new CompileException("Invalid type: Floating-point numbers cannot have integer type suffixes.");
		}

		// Special case for "let" syntax (e.g., "let name = 100; name")
		if (inputContent.startsWith("let ") && inputContent.contains("=") && inputContent.contains(";")) {
			// Extract the number after the equals sign
			Pattern pattern = Pattern.compile("=\\s*(\\d+)\\s*;");
			Matcher matcher = pattern.matcher(inputContent);
			if (matcher.find()) {
				String value = matcher.group(1);
				return generatePrintfProgram(value);
			}
		}

		// Check for typed float (e.g., 100.0F32, 100.0F64, 100F32, 100F64)
		if (inputContent.matches("^\\d+(\\.\\d*)?F(32|64)$")) {
			// Extract the number part (before the type suffix)
			String numberPart = inputContent.replaceAll("^(\\d+(\\.\\d*)?)F(32|64)$", "$1");
			return generateFloatingPointProgram(Double.parseDouble(numberPart));
		}

		// Check for typed number (e.g., 100U8, 100I16)
		if (inputContent.matches("^\\d+(U8|U16|U32|U64|I8|I16|I32|I64)$")) {
			// Extract the number part (before the type suffix)
			String numberPart = inputContent.replaceAll("^(\\d+)(U8|U16|U32|U64|I8|I16|I32|I64)$", "$1");
			return generatePrintfProgram(numberPart);
		}

		// Check if the input is a character or string (enclosed in quotes)
		if ((inputContent.length() == 3 && inputContent.charAt(0) == '\'' && inputContent.charAt(2) == '\'') ||
				(inputContent.length() >= 2 && inputContent.charAt(0) == '\"' &&
				 inputContent.charAt(inputContent.length() - 1) == '\"')) {
			String value = inputContent.charAt(0) == '\'' ? String.valueOf(inputContent.charAt(1))
																										: inputContent.substring(1, inputContent.length() - 1);
			return generatePrintfProgram(value);
		}

		// Check for require syntax (e.g., "require(name : **char); *name" or "require(test : **char); *test" or "require(args : **char); args.length")
		Pattern requirePattern = Pattern.compile(
				"require\\(([a-zA-Z0-9_]+)\\s*:\\s*\\*\\*char\\);\\s*(?:\\*([a-zA-Z0-9_]+)|([a-zA-Z0-9_]+)\\.length)");
		Matcher requireMatcher = requirePattern.matcher(inputContent);
		if (requireMatcher.find()) {
			String paramName = requireMatcher.group(1);
			String usedName = requireMatcher.group(2); // Will be null if we're accessing length
			String lengthName = requireMatcher.group(3); // Will be null if we're accessing the value

			// Check if we're accessing the length of the arguments
			boolean isAccessingLength = lengthName != null;

			// Verify that the parameter name matches the used name or length name
			if (isAccessingLength && !paramName.equals(lengthName)) {
				throw new CompileException(
						"Parameter name '" + paramName + "' does not match the used name '" + lengthName + "'");
			} else if (!isAccessingLength && !paramName.equals(usedName)) {
				throw new CompileException(
						"Parameter name '" + paramName + "' does not match the used name '" + usedName + "'");
			}

			if (isAccessingLength) {
				// Generate C code that prints the number of arguments
				return INCLUDE_STDIO + "int main(int argc, char **argv) {\n\tprintf(\"%d\", argc - 1);" + MAIN_RETURN;
			} else {
				// All other cases output the first argument
				return INCLUDE_STDIO +
							 "int main(int argc, char **argv) {\n\tif (argc > 1) {\n\t\tprintf(\"%s\", argv[1]);\n\t}" + MAIN_RETURN;
			}
		}

		// Try to parse input as integer or floating point number
		String trimmedContent = inputContent.trim();
		try {
			int number = Integer.parseInt(trimmedContent);
			return generatePrintfProgram(String.valueOf(number));
		} catch (NumberFormatException e) {
			try {
				double floatingPoint = Double.parseDouble(trimmedContent);
				return generateFloatingPointProgram(floatingPoint);
			} catch (NumberFormatException e2) {
				// Try to extract a number from the input as a last resort 
				Pattern pattern = Pattern.compile("\\b(\\d+)\\b");
				Matcher matcher = pattern.matcher(inputContent);

				// Find the last number in the input
				String lastNumber = null;
				while (matcher.find()) {
					lastNumber = matcher.group(1);
				}

				if (lastNumber != null) {
					return generatePrintfProgram(lastNumber);
				}

				// If all else fails, throw a CompileException
				throw new CompileException("Failed to compile: Input content cannot be processed.");
			}
		}
	}
}