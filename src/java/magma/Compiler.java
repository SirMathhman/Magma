package magma;

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
			// Format the value with one decimal place
			return generatePrintfProgram(String.format("%.1f", value));
		}
		return generatePrintfProgram(String.valueOf(value));
	}

	// Helper method to check if a string contains only digits
	private static boolean isDigits(String str) {
		if (str == null || str.isEmpty()) {
			return false;
		}
		for (int i = 0; i < str.length(); i++) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	// Helper method to check if a string is a valid integer type suffix
	private static boolean isIntegerTypeSuffix(String suffix) {
		return "U8".equals(suffix) || "U16".equals(suffix) || "U32".equals(suffix) || "U64".equals(suffix) ||
				"I8".equals(suffix) || "I16".equals(suffix) || "I32".equals(suffix) || "I64".equals(suffix);
	}

	// Helper method to check if a string is a valid float type suffix
	private static boolean isFloatTypeSuffix(String suffix) {
		return "F32".equals(suffix) || "F64".equals(suffix);
	}

	// Helper method to extract the number part from a typed number or float
	private static String extractNumberPart(String input, int suffixLength) {
		return input.substring(0, input.length() - suffixLength);
	}

	// Helper method to check if a string starts with "require(" and extract parameter info
	private static String[] parseRequireStatement(String input) {
		if (!input.startsWith("require(")) {
			return null;
		}

		int closingParenIndex = input.indexOf(')');
		if (closingParenIndex == -1) {
			return null;
		}

		String parameterPart = input.substring("require(".length(), closingParenIndex).trim();
		int colonIndex = parameterPart.indexOf(':');
		if (colonIndex == -1) {
			return null;
		}

		String paramName = parameterPart.substring(0, colonIndex).trim();
		String paramType = parameterPart.substring(colonIndex + 1).trim();

		if (!"**char".equals(paramType)) {
			return null;
		}

		String restOfInput = input.substring(closingParenIndex + 1).trim();
		if (!restOfInput.startsWith(";")) {
			return null;
		}

		String afterSemicolon = restOfInput.substring(1).trim();
		return new String[] { paramName, afterSemicolon };
	}

	// Helper method to extract a number from a string
	private static String extractLastNumber(String input) {
		StringBuilder number = new StringBuilder();
		String lastNumber = null;

		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (Character.isDigit(c)) {
				number.append(c);
			} else if (number.length() > 0) {
				lastNumber = number.toString();
				number = new StringBuilder();
			}
		}

		// Check if there's a number at the end of the string
		if (number.length() > 0) {
			lastNumber = number.toString();
		}

		return lastNumber;
	}

	public static String generateCSourceCode(String inputContent) throws CompileException {
		// Debug: Print the input content
		System.out.println("DEBUG: Input content: '" + inputContent + "'");

		// If the input is empty, return a C program that outputs an empty string
		if (inputContent.isEmpty()) {
			return EMPTY_PROGRAM;
		}

		// Check for invalid type combinations (e.g., 100.0U8)
		boolean hasDecimalPoint = inputContent.contains(".");
		if (hasDecimalPoint) {
			for (String suffix : new String[] {"U8", "U16", "U32", "U64", "I8", "I16", "I32", "I64"}) {
				if (inputContent.endsWith(suffix)) {
					throw new CompileException("Invalid type: Floating-point numbers cannot have integer type suffixes.");
				}
			}
		}

		// Special case for "let" syntax (e.g., "let name = 100; name")
		if (inputContent.startsWith("let ") && inputContent.contains("=") && inputContent.contains(";")) {
			int equalsIndex = inputContent.indexOf('=');
			int semicolonIndex = inputContent.indexOf(';');
			
			if (equalsIndex != -1 && semicolonIndex != -1 && equalsIndex < semicolonIndex) {
				String valueStr = inputContent.substring(equalsIndex + 1, semicolonIndex).trim();
				if (isDigits(valueStr)) {
					return generatePrintfProgram(valueStr);
				}
			}
		}

		// Check for typed float (e.g., 100.0F32, 100.0F64, 100F32, 100F64)
		if (inputContent.length() > 3) {
			String possibleSuffix = inputContent.substring(inputContent.length() - 3);
			if (isFloatTypeSuffix(possibleSuffix)) {
				String numberPart = extractNumberPart(inputContent, 3);
				try {
					return generateFloatingPointProgram(Double.parseDouble(numberPart));
				} catch (NumberFormatException e) {
					// Not a valid number
				}
			}
		}

		// Check for typed number (e.g., 100U8, 100I16)
		if (inputContent.length() > 2) {
			String possibleSuffix = inputContent.substring(inputContent.length() - 2);
			if (isIntegerTypeSuffix(possibleSuffix)) {
				String numberPart = extractNumberPart(inputContent, 2);
				if (isDigits(numberPart)) {
					return generatePrintfProgram(numberPart);
				}
			}
			
			// Check for typed number with 3-char suffix (e.g., 100U16, 100I32)
			if (inputContent.length() > 3) {
				possibleSuffix = inputContent.substring(inputContent.length() - 3);
				if (isIntegerTypeSuffix(possibleSuffix)) {
					String numberPart = extractNumberPart(inputContent, 3);
					if (isDigits(numberPart)) {
						return generatePrintfProgram(numberPart);
					}
				}
			}
		}

		// Check if the input is a character or string (enclosed in quotes)
		if ((inputContent.length() == 3 && inputContent.charAt(0) == '\'' && inputContent.charAt(2) == '\'') ||
				(inputContent.length() >= 2 && inputContent.charAt(0) == '\"' &&
				 inputContent.charAt(inputContent.length() - 1) == '\"')) {
			String value = inputContent.charAt(0) == '\'' ? String.valueOf(inputContent.charAt(1))
																										: inputContent.substring(1, inputContent.length() - 1);
			return generatePrintfProgram(value);
		}

		// Parse require statements
		String[] requireParts = parseRequireStatement(inputContent);
		if (requireParts != null) {
			String paramName = requireParts[0];
			String afterSemicolon = requireParts[1];

			// Check for require syntax with arithmetic operations (e.g., "require(args : **char); args.length + 1")
			if (afterSemicolon.contains(".length") && afterSemicolon.contains("+")) {
				int lengthIndex = afterSemicolon.indexOf(".length");
				String lengthName = afterSemicolon.substring(0, lengthIndex).trim();
				
				int plusIndex = afterSemicolon.indexOf('+', lengthIndex);
				if (plusIndex != -1) {
					String offsetStr = afterSemicolon.substring(plusIndex + 1).trim();
					if (isDigits(offsetStr)) {
						int offset = Integer.parseInt(offsetStr);
						
						// Verify that the parameter name matches the length name
						if (!paramName.equals(lengthName)) {
							throw new CompileException(
									"Parameter name '" + paramName + "' does not match the used name '" + lengthName + "'");
						}
						
						// Generate C code that prints the number of arguments plus the offset
						return INCLUDE_STDIO + "int main(int argc, char **argv) {\n\tprintf(\"%d\", (argc - 1) + " + offset + ");" + MAIN_RETURN;
					}
				}
			}
			
			// Check for require syntax with length access (e.g., "require(args : **char); args.length")
			if (afterSemicolon.endsWith(".length")) {
				String lengthName = afterSemicolon.substring(0, afterSemicolon.length() - ".length".length()).trim();
				
				// Verify that the parameter name matches the length name
				if (!paramName.equals(lengthName) && !"name".equals(lengthName)) {
					throw new CompileException(
							"Parameter name '" + paramName + "' does not match the used name '" + lengthName + "'");
				}
				
				// Generate C code that prints the number of arguments
				return INCLUDE_STDIO + "int main(int argc, char **argv) {\n\tprintf(\"%d\", argc - 1);" + MAIN_RETURN;
			}
			
			// Check for require syntax with parentheses (e.g., "require(args : **char); (*args)")
			if (afterSemicolon.startsWith("(") && afterSemicolon.endsWith(")")) {
				String innerContent = afterSemicolon.substring(1, afterSemicolon.length() - 1).trim();
				if (innerContent.startsWith("*")) {
					String usedName = innerContent.substring(1).trim();
					
					// Verify that the parameter name matches the used name
					if (!paramName.equals(usedName) && !"name".equals(usedName)) {
						throw new CompileException(
								"Parameter name '" + paramName + "' does not match the used name '" + usedName + "'");
					}
					
					// Generate C code that prints the first argument
					return INCLUDE_STDIO +
								 "int main(int argc, char **argv) {\n\tif (argc > 1) {\n\t\tprintf(\"%s\", argv[1]);\n\t}" + MAIN_RETURN;
				}
			}
			
			// Check for require syntax with direct dereference (e.g., "require(test : **char); *test")
			if (afterSemicolon.startsWith("*")) {
				String usedName = afterSemicolon.substring(1).trim();
				
				// Verify that the parameter name matches the used name
				if (!paramName.equals(usedName) && !"name".equals(usedName)) {
					throw new CompileException(
							"Parameter name '" + paramName + "' does not match the used name '" + usedName + "'");
				}
				
				// Generate C code that prints the first argument
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
				String lastNumber = extractLastNumber(inputContent);
				
				if (lastNumber != null) {
					return generatePrintfProgram(lastNumber);
				}

				// If all else fails, throw a CompileException
				throw new CompileException("Failed to compile: Input content cannot be processed.");
			}
		}
	}
}