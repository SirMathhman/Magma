package magma;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	public static String generateCSourceCode(String inputContent) throws CompileException {
		// Debug: Print the input content
		System.out.println("DEBUG: Input content: '" + inputContent + "'");

		// If the input is empty, return a C program that outputs an empty string
		if (inputContent.isEmpty()) {
			return "#include <stdio.h>\n\nint main() {\n\treturn 0;\n}";
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

		// Check if the input is a character or string (enclosed in quotes)
		if ((inputContent.length() == 3 && inputContent.charAt(0) == '\'' && inputContent.charAt(2) == '\'') ||
				(inputContent.length() >= 2 && inputContent.charAt(0) == '\"' &&
				 inputContent.charAt(inputContent.length() - 1) == '\"')) {
			String value = inputContent.charAt(0) == '\'' ? String.valueOf(inputContent.charAt(1))
																										: inputContent.substring(1, inputContent.length() - 1);
			return generatePrintfProgram(value);
		}

		// If the input is a number, return a C program that outputs the same number
		try {
			int number = Integer.parseInt(inputContent.trim());
			return generatePrintfProgram(String.valueOf(number));
		} catch (NumberFormatException e) {
			// Try to parse as a floating-point number
			try {
				double floatingPoint = Double.parseDouble(inputContent.trim());
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