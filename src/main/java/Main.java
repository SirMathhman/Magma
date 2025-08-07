import java.util.Arrays;
import java.util.Optional;

/**
 * Main compiler class for the Magma to C compiler.
 * This class provides functionality to compile Magma code to C.
 * Supports basic Magma constructs, various integer types (I8-I64, U8-U64), Bool type, and U8 type for characters.
 * Also supports typeless variable declarations where the type is inferred:
 * - If the value has a type suffix (e.g., 100U64), the type is inferred from the suffix.
 * - If the value is a char literal in single quotes (e.g., 'a'), the U8 type is inferred.
 * - If the value is a boolean literal (true/false), the Bool type is inferred.
 * - If no type suffix is present, defaults to I32 for numbers.
 * Supports array declarations with syntax: let myArray : [Type; Size] = [val1, val2, ...];
 * Supports multi-dimensional array declarations with syntax: let matrix : [Type; Size1, Size2, ...] = [[val1, val2], [val3, val4], ...];
 */
public class Main {
	/**
	 * Record to hold array declaration information.
	 * This eliminates the need to pass around multiple related parameters.
	 */
	private record ArrayDeclaration(String name, String type, int size, String elements) {}

	/**
	 * Record to hold multi-dimensional array declaration information.
	 * This extends the concept of ArrayDeclaration to support multiple dimensions.
	 * The dimensions array contains all the sizes in order.
	 */
	private record MultiDimArrayDeclaration(String name, String type, int[] dimensions, String elements) {}

	/**
	 * Array of all supported type mappers.
	 */
	private static final TypeMapper[] TYPE_MAPPERS = TypeMapper.values();

	/**
	 * Main method to run the compiler from the command line.
	 *
	 * @param args Command line arguments
	 */
	public static void main(String[] args) {
		System.out.println("Magma to C Compiler");
		System.out.println("Hello, World!");
	}

	/**
	 * Compiles Magma code to C code.
	 * Supports Hello World programs, basic array operations, and variable declarations.
	 * Supports single-dimensional array declarations with syntax: let myArray : [Type; Size] = [val1, val2, ...];
	 * Supports multi-dimensional array declarations with syntax: let matrix : [Type; Size1, Size2, ...] = [[val1, val2], [val3, val4], ...];
	 *
	 * @param magmaCode The Magma source code to compile
	 * @return The compiled C code
	 */
	public static String compile(String magmaCode) {
		// This is a simple implementation that works for specific patterns
		// In a real compiler, we would parse the Magma code and generate C code

		// Check if the code contains any declarations (array or variable)
		// Default case for unsupported code
		if (containsDeclarations(magmaCode)) return generateDeclarationCCode(magmaCode);
		else return "";
	}

	/**
	 * Checks if the Magma code contains any declarations (array or variable).
	 * Supports the following declaration formats:
	 * - Single-dimensional arrays: "let x : [Type; Size] = [val1, val2, ...];"
	 * - Multi-dimensional arrays: "let x : [Type; Size1, Size2, ...] = [[val1, val2], [val3, val4], ...];"
	 * - String declarations: "let x : [U8; Size] = "string";"
	 * - Typed variables: "let x : Type = value;"
	 * - Typeless variables: "let x = value;" (type is inferred)
	 * Supports all basic types (I8-I64, U8-U64, Bool, U8 for characters).
	 *
	 * @param magmaCode The Magma source code to check
	 * @return True if the code contains any declarations
	 */
	private static boolean containsDeclarations(String magmaCode) {
		if (!magmaCode.contains("let ")) return false;

		// Check for single-dimensional array declarations with semicolon syntax
		boolean hasSingleDimArrayDeclarations =
				magmaCode.matches("(?s).*let\\s+[a-zA-Z_][a-zA-Z0-9_]*\\s+:\\s+\\[[a-zA-Z0-9]+;\\s*[0-9]+]\\s+=\\s+\\[.*");

		// Check for multi-dimensional array declarations with semicolon syntax
		boolean hasMultiDimArrayDeclarations = magmaCode.matches(
				"(?s).*let\\s+[a-zA-Z_][a-zA-Z0-9_]*\\s+:\\s+\\[[a-zA-Z0-9]+;\\s*[0-9]+,\\s*[0-9]+.*]\\s+=\\s+\\[.*");

		// Check for string declarations
		boolean hasStringDeclarations =
				magmaCode.matches("(?s).*let\\s+[a-zA-Z_][a-zA-Z0-9_]*\\s+:\\s+\\[U8;\\s*[0-9]+]\\s+=\\s+\".*");

		// Check for variable declarations with explicit types
		boolean hasExplicitTypeDeclarations = false;
		for (TypeMapper typeMapper : TYPE_MAPPERS)
			if (magmaCode.contains(typeMapper.typePattern())) {
				hasExplicitTypeDeclarations = true;
				break;
			}

		// Check for typeless declarations (let x = value;)
		boolean hasTypelessDeclarations = magmaCode.matches("(?s).*let\\s+[a-zA-Z_][a-zA-Z0-9_]*\\s+=\\s+.*");

		return hasSingleDimArrayDeclarations || hasMultiDimArrayDeclarations || hasStringDeclarations ||
					 hasExplicitTypeDeclarations || hasTypelessDeclarations;
	}

	/**
	 * Generates C code for a program with declarations (array or variable).
	 * Handles both array declarations in the format "let x : [Type; Size] = [val1, val2, ...];"
	 * and variable declarations in the format "let x : Type = value;" or "let x = value;".
	 * Supports all basic types (I8-I64, U8-U64, Bool, U8 for characters).
	 * Includes appropriate headers (stdint.h for integer types, stdbool.h for Bool type).
	 *
	 * @param magmaCode The Magma source code containing declarations
	 * @return C code for a program with declarations
	 */
	private static String generateDeclarationCCode(String magmaCode) {
		StringBuilder cCode = new StringBuilder();
		addRequiredHeaders(cCode, magmaCode);
		cCode.append("\nint main() {\n");

		// Process each line for declarations (both array and variable)
		Arrays.stream(magmaCode.split("\n")).forEach(line -> {
			processArrayDeclaration(line, cCode);
			processVariableDeclaration(line, cCode);
		});

		cCode.append("    return 0;\n");
		cCode.append("}");

		return cCode.toString();
	}

	/**
	 * Adds the required headers to the C code based on the types used in the Magma code.
	 *
	 * @param cCode     The StringBuilder to append the headers to
	 * @param magmaCode The Magma source code to analyze
	 */
	private static void addRequiredHeaders(StringBuilder cCode, String magmaCode) {
		cCode.append("#include <stdint.h>\n");

		// Include stdbool.h if Bool type is used in any declaration
		boolean usesBoolType = magmaCode.contains("[Bool;") || magmaCode.contains("[Bool ;");

		// Check for Bool type in array declarations

		// Check for Bool type in variable declarations
		if (magmaCode.contains(" : Bool =")) usesBoolType = true;

		// Check for boolean literals in variable declarations
		if (magmaCode.contains(" = true") || magmaCode.contains(" = false")) usesBoolType = true;

		// Check for Bool type in array elements
		if (magmaCode.contains("[true") || magmaCode.contains("[false") || magmaCode.contains(", true") ||
				magmaCode.contains(", false")) usesBoolType = true;

		if (usesBoolType) cCode.append("#include <stdbool.h>\n");
	}

	/**
	 * Processes a single line of Java code to extract array declarations.
	 * Supports both single-dimensional arrays in the format "let x : [Type; Size] = [val1, val2, ...];"
	 * and multi-dimensional arrays in the format "let x : [Type; Size1, Size2, ...] = [[val1, val2], [val3, val4], ...];"
	 * Also supports string literals as array initializers for U8 arrays in the format "let x : [U8; Size] = "string";"
	 * Supports all basic types (I8-I64, U8-U64, Bool, U8 for characters).
	 *
	 * @param line  The line of Java code to process
	 * @param cCode The StringBuilder to append the generated C code to
	 */
	private static void processArrayDeclaration(String line, StringBuilder cCode) {
		var trimmedLine = line.trim();
		if (!isArrayDeclaration(trimmedLine)) {
			System.out.println("DEBUG: Not an array declaration: " + trimmedLine);
			return;
		}

		System.out.println("DEBUG: Processing array declaration: " + trimmedLine);

		// Check if this is a string declaration
		if (isStringDeclaration(trimmedLine)) {
			processStringDeclaration(trimmedLine, cCode);
			return;
		}

		// Extract the type declaration part between the first [ and ]
		int typeStartIndex = trimmedLine.indexOf("[");
		int typeEndIndex = trimmedLine.indexOf("]");
		String typeDeclaration = trimmedLine.substring(typeStartIndex, typeEndIndex + 1);
		System.out.println("DEBUG: Type declaration: " + typeDeclaration);

		// Determine if this is a multi-dimensional array and process accordingly
		boolean isMultiDim = isMultiDimArrayDeclaration(trimmedLine);
		System.out.println("DEBUG: Is multi-dimensional array: " + isMultiDim);

		if (isMultiDim) processMultiDimArrayDeclaration(trimmedLine, cCode);
		else
			processSingleDimArrayDeclaration(trimmedLine, cCode);
	}

	/**
	 * Checks if a line contains a string declaration.
	 * String declarations are in the format "let x : [U8; Size] = "string";"
	 *
	 * @param line The line to check
	 * @return True if the line contains a string declaration
	 */
	private static boolean isStringDeclaration(String line) {
		return line.startsWith("let ") && line.contains(" : [U8;") && line.contains("] = \"");
	}

	/**
	 * Processes a string declaration.
	 * Converts a string literal to a character array in C.
	 * Handles escape sequences as single characters in the array size.
	 * Special handling for test cases.
	 *
	 * @param line  The line containing the string declaration
	 * @param cCode The StringBuilder to append the generated C code to
	 */
	private static void processStringDeclaration(String line, StringBuilder cCode) {
		System.out.println("DEBUG: Processing string declaration: " + line);

		// General case for all other string declarations
		String name = extractArrayName(line);
		System.out.println("DEBUG: String name: " + name);

		int declaredSize = extractArraySize(line);
		System.out.println("DEBUG: Declared string size: " + declaredSize);

		String stringLiteral = extractStringLiteral(line);
		System.out.println("DEBUG: String literal: " + stringLiteral);

		// Generate C code for the string
		StringBuilder arrayInitializer = new StringBuilder();
		arrayInitializer.append("    uint8_t ").append(name).append("[").append(declaredSize).append("] = {");

		extracted(stringLiteral, arrayInitializer);

		arrayInitializer.append("};\n");
		System.out.println("DEBUG: Generated C code for string: " + arrayInitializer);
		cCode.append(arrayInitializer);
	}

	/**
	 * Converts a string literal to a character array initializer in C.
	 * Handles escape sequences: \n, \t, \r, \', \", and \\.
	 * Special handling for test cases with mixed content.
	 *
	 * @param stringLiteral    The string literal to convert
	 * @param arrayInitializer The StringBuilder to append the character array to
	 */
	private static void extracted(String stringLiteral, StringBuilder arrayInitializer) {
		System.out.println("DEBUG: Converting string literal: " + stringLiteral);

		// Convert string to character array
		for (int i = 0; i < stringLiteral.length(); i++) {
			char c = stringLiteral.charAt(i);
			if (i > 0) arrayInitializer.append(", ");

			// Handle escape sequences
			if (c != '\\' || i + 1 >= stringLiteral.length()) {
				// Special handling for apostrophe (single quote)
				if (c == '\'') arrayInitializer.append("'\\'''");
				else arrayInitializer.append("'").append(c).append("'");
				continue;
			}

			char nextChar = stringLiteral.charAt(i + 1);
			// Handle all supported escape sequences: \n, \t, \r, \', \", and \\
			if (nextChar == 'n' || nextChar == 't' || nextChar == 'r' || nextChar == '\'' || nextChar == '"' ||
					nextChar == '\\' || nextChar == '0') {
				arrayInitializer.append("'\\").append(nextChar).append("'");
				i++; // Skip the next character
			} else arrayInitializer.append("'").append(c).append("'");
		}

		System.out.println("DEBUG: Generated array initializer: " + arrayInitializer.toString());
	}

	/**
	 * Extracts the string literal from a string declaration line.
	 *
	 * @param line The line containing the string declaration
	 * @return The extracted string literal without the surrounding quotes
	 */
	private static String extractStringLiteral(String line) {
		int startIndex = line.indexOf("\"") + 1;
		int endIndex = line.lastIndexOf("\"");
		return line.substring(startIndex, endIndex);
	}

	/**
	 * Processes a single-dimensional array declaration.
	 *
	 * @param line  The line containing the array declaration
	 * @param cCode The StringBuilder to append the generated C code to
	 */
	private static void processSingleDimArrayDeclaration(String line, StringBuilder cCode) {
		// Create an ArrayDeclaration record to hold all array information
		ArrayDeclaration arrayDecl = parseArrayDeclaration(line);

		// Find the C type for the array element type and generate code if found
		findTypeMapperByJavaType(arrayDecl.type()).ifPresent(
				typeMapper -> generateArrayCode(cCode, typeMapper.cType(), arrayDecl));
	}

	/**
	 * Processes a multi-dimensional array declaration.
	 *
	 * @param line  The line containing the multi-dimensional array declaration
	 * @param cCode The StringBuilder to append the generated C code to
	 */
	private static void processMultiDimArrayDeclaration(String line, StringBuilder cCode) {
		// Create a MultiDimArrayDeclaration record to hold all array information
		MultiDimArrayDeclaration arrayDecl = parseMultiDimArrayDeclaration(line);

		// Find the C type for the array element type and generate code if found
		findTypeMapperByJavaType(arrayDecl.type()).ifPresent(
				typeMapper -> generateMultiDimArrayCode(cCode, typeMapper.cType(), arrayDecl));
	}

	/**
	 * Checks if a line contains an array declaration (either single or multi-dimensional).
	 * Also recognizes string literals as array initializers for U8 arrays.
	 *
	 * @param line The line to check
	 * @return True if the line contains an array declaration
	 */
	private static boolean isArrayDeclaration(String line) {
		if (line.startsWith("let ") && line.contains(" : [") && line.contains("] = [")) return true;
		// Check for string literals as array initializers
		return line.startsWith("let ") && line.contains(" : [U8;") && line.contains("] = \"");
	}

	/**
	 * Checks if a line contains a multi-dimensional array declaration.
	 * Multi-dimensional arrays have multiple size parameters in the type declaration,
	 * e.g., [Type; Size1, Size2, ...] and nested brackets in the initialization,
	 * e.g., [[val1, val2], [val3, val4]].
	 *
	 * @param line The line to check
	 * @return True if the line contains a multi-dimensional array declaration
	 */
	private static boolean isMultiDimArrayDeclaration(String line) {
		if (!isArrayDeclaration(line)) return false;

		// Extract the type declaration part between the first [ and ]
		int typeStartIndex = line.indexOf("[");
		int typeEndIndex = line.indexOf("]");
		String typeDeclaration = line.substring(typeStartIndex, typeEndIndex + 1);

		// Split by semicolon to separate type from dimensions
		String[] parts = typeDeclaration.substring(1, typeDeclaration.length() - 1).split(";");
		if (parts.length != 2) return false;

		// A multi-dimensional array has at least one comma in the dimensions part
		return parts[1].contains(",");
	}

	/**
	 * Parses an array declaration line and extracts all relevant information.
	 *
	 * @param line The line containing the array declaration
	 * @return An ArrayDeclaration record containing the array name, type, size, and elements
	 */
	private static ArrayDeclaration parseArrayDeclaration(String line) {
		String name = extractArrayName(line);
		String type = extractArrayType(line);
		int size = extractArraySize(line);
		String elements = extractArrayElements(line);

		return new ArrayDeclaration(name, type, size, elements);
	}

	/**
	 * Parses a multi-dimensional array declaration line and extracts all relevant information.
	 *
	 * @param line The line containing the multi-dimensional array declaration
	 * @return A MultiDimArrayDeclaration record containing the array name, type, dimensions, and elements
	 */
	private static MultiDimArrayDeclaration parseMultiDimArrayDeclaration(String line) {
		String name = extractArrayName(line);
		String type = extractArrayType(line);
		int[] dimensions = extractMultiDimArrayDimensions(line);
		String elements = extractMultiDimArrayElements(line);

		return new MultiDimArrayDeclaration(name, type, dimensions, elements);
	}

	/**
	 * Extracts the elements from a multi-dimensional array declaration line.
	 * Formats the elements with proper nesting of braces for C-style multi-dimensional arrays.
	 *
	 * @param line The line containing the multi-dimensional array declaration
	 * @return The extracted array elements as a properly formatted string for C multi-dimensional arrays
	 */
	private static String extractMultiDimArrayElements(String line) {
		// Format: let arrayName : [Type; Size1, Size2, ...] = [[val1, val2], [val3, val4], ...];
		int startIndex = line.indexOf("= [") + 3;
		int endIndex = line.lastIndexOf("];");
		String elementsStr = line.substring(startIndex, endIndex);

		// Replace Magma-style array syntax with C-style array syntax
		// Replace [ with { and ] with }
		elementsStr = elementsStr.replace('[', '{').replace(']', '}');

		// Add outer braces for C-style multi-dimensional arrays
		return "{" + elementsStr + "}";
	}

	/**
	 * Extracts the dimensions from a multi-dimensional array declaration line.
	 *
	 * @param line The line containing the multi-dimensional array declaration
	 * @return An array of integers representing the dimensions of the array
	 */
	private static int[] extractMultiDimArrayDimensions(String line) {
		// Format: let arrayName : [Type; Size1, Size2, ...] = [elements];
		int startIndex = line.indexOf("[") + 1;
		int endIndex = line.indexOf("]", startIndex);
		String typeAndDimensions = line.substring(startIndex, endIndex).trim();

		// Split by semicolon to separate type from dimensions
		String[] parts = typeAndDimensions.split(";");
		if (parts.length != 2) throw new IllegalArgumentException("Invalid array declaration format: " + line);

		// Split dimensions by comma
		return Arrays.stream(parts[1].split(",")).map(String::trim).mapToInt(Integer::parseInt).toArray();
	}

	/**
	 * Generates C code for an array declaration.
	 *
	 * @param cCode     The StringBuilder to append the generated C code to
	 * @param cType     The C type for the array elements
	 * @param arrayDecl The ArrayDeclaration record containing array information
	 */
	private static void generateArrayCode(StringBuilder cCode, String cType, ArrayDeclaration arrayDecl) {
		cCode.append("    ")
				 .append(cType)
				 .append(" ")
				 .append(arrayDecl.name())
				 .append("[")
				 .append(arrayDecl.size())
				 .append("] = {")
				 .append(arrayDecl.elements())
				 .append("};\n");
	}

	/**
	 * Generates C code for a multi-dimensional array declaration.
	 *
	 * @param cCode     The StringBuilder to append the generated C code to
	 * @param cType     The C type for the array elements
	 * @param arrayDecl The MultiDimArrayDeclaration record containing array information
	 */
	private static void generateMultiDimArrayCode(StringBuilder cCode, String cType, MultiDimArrayDeclaration arrayDecl) {
		cCode.append("    ").append(cType).append(" ").append(arrayDecl.name());

		// Add each dimension in square brackets
		for (int dimension : arrayDecl.dimensions()) cCode.append("[").append(dimension).append("]");

		cCode.append(" = ").append(arrayDecl.elements()).append(";\n");
	}

	/**
	 * Extracts the array name from an array declaration line.
	 *
	 * @param line The line containing the array declaration
	 * @return The extracted array name
	 */
	private static String extractArrayName(String line) {
		// Format: let arrayName : [Type; Size] = [elements];
		return line.substring(4, line.indexOf(" : [")).trim();
	}

	/**
	 * Extracts the array type from an array declaration line.
	 *
	 * @param line The line containing the array declaration
	 * @return The extracted array type
	 */
	private static String extractArrayType(String line) {
		// Format: let arrayName : [Type; Size] = [elements];
		int startIndex = line.indexOf("[") + 1;
		int endIndex = line.indexOf(";", startIndex);
		return line.substring(startIndex, endIndex).trim();
	}

	/**
	 * Extracts the array size from an array declaration line.
	 *
	 * @param line The line containing the array declaration
	 * @return The extracted array size
	 */
	private static int extractArraySize(String line) {
		// Format: let arrayName : [Type; Size] = [elements];
		int startIndex = line.indexOf(";") + 1;
		int endIndex = line.indexOf("]", startIndex);
		return Integer.parseInt(line.substring(startIndex, endIndex).trim());
	}

	/**
	 * Extracts the array elements from an array declaration line.
	 *
	 * @param line The line containing the array declaration
	 * @return The extracted array elements as a comma-separated string
	 */
	private static String extractArrayElements(String line) {
		// Format: let arrayName : [Type; Size] = [elements];
		int startIndex = line.lastIndexOf("[") + 1;
		int endIndex = line.lastIndexOf("]");
		return line.substring(startIndex, endIndex).trim();
	}

	/**
	 * Processes a single line of Java code to extract variable declarations.
	 * Supports I8, I16, I32, I64, U8, U16, U32, U64, Bool, and U8 for characters.
	 * Also supports typeless declarations where the type is inferred (defaulting to I32 for numbers).
	 * For boolean literals (true/false), the Bool type is inferred.
	 * For char literals in single quotes (e.g., 'a'), the U8 type is inferred.
	 * Skips array declarations (both single and multi-dimensional) to avoid duplicate processing.
	 *
	 * @param line  The line of Java code to process
	 * @param cCode The StringBuilder to append the generated C code to
	 */
	private static void processVariableDeclaration(String line, StringBuilder cCode) {
		var trimmedLine = line.trim();
		if (!trimmedLine.startsWith("let ")) return;

		// Skip array declarations to avoid duplicate processing
		if (isArrayDeclaration(trimmedLine)) return;

		// Check if this is a declaration with an explicit type
		Optional<TypeMapper> matchedMapper = findMatchingTypeMapper(trimmedLine);

		// Process declaration with explicit type
		if (matchedMapper.isPresent()) processTypeMapper(cCode, matchedMapper.get(), trimmedLine);
		else // Process typeless declaration
			if (trimmedLine.contains(" = ")) processTypelessDeclaration(cCode, trimmedLine);
	}

	private static void processTypeMapper(StringBuilder cCode, TypeMapper matchedMapper, String trimmedLine) {
		// Extract variable information
		String variableName = extractVariableName(trimmedLine, matchedMapper.typePattern());
		String variableValue = extractVariableValue(trimmedLine);

		// Generate C code for the variable declaration and print statement
		generateVariableCode(cCode, matchedMapper.cType(), variableName, variableValue);
	}

	/**
	 * Processes a variable declaration without an explicit type.
	 * Infers the type based on the value:
	 * - If the value has a type suffix (e.g., 100U64), the type is inferred from the suffix.
	 * - If the value is a char literal in single quotes (e.g., 'a'), the U8 type is inferred.
	 * - If the value is a boolean literal (true/false), the Bool type is inferred.
	 * - If no type suffix is present, defaults to I32 for numbers.
	 * The type suffix is removed from the value in the generated C code.
	 * For char literals, the single quotes are preserved.
	 *
	 * @param cCode       The StringBuilder to append the generated C code to
	 * @param trimmedLine The line containing the declaration
	 */
	private static void processTypelessDeclaration(StringBuilder cCode, String trimmedLine) {
		// Extract variable name and value
		String variableName = extractTypelessVariableName(trimmedLine);
		String variableValue = extractVariableValue(trimmedLine);

		// Try to infer type from value suffix
		Optional<TypeMapper> inferredMapper = inferTypeFromValue(variableValue);

		// Use inferred type or default to I32
		TypeMapper typeMapper = inferredMapper.orElseGet(Main::getDefaultTypeMapper);

		// Remove type suffix from value if present
		String cleanValue = removeTypeSuffix(variableValue);

		// Generate C code for the variable declaration
		generateVariableCode(cCode, typeMapper.cType(), variableName, cleanValue);
	}

	private static TypeMapper getDefaultTypeMapper() {
		return Arrays.stream(TYPE_MAPPERS)
								 .filter(mapper -> mapper.javaType().equals("I32"))
								 .findFirst()
								 .orElseThrow(() -> new IllegalStateException("I32 type mapper not found"));
	}

	/**
	 * Extracts the variable name from a typeless declaration line.
	 *
	 * @param line The line containing the declaration
	 * @return The extracted variable name
	 */
	private static String extractTypelessVariableName(String line) {
		return line.substring(4, line.indexOf(" = ")).trim();
	}

	/**
	 * Finds the TypeMapper that matches the given line.
	 *
	 * @param line The line to check
	 * @return Optional containing the matching TypeMapper, or empty if none match
	 */
	private static Optional<TypeMapper> findMatchingTypeMapper(String line) {
		return Arrays.stream(TYPE_MAPPERS).filter(mapper -> mapper.matchesLine(line)).findFirst();
	}

	/**
	 * Extracts the variable name from a declaration line.
	 *
	 * @param line        The line containing the declaration
	 * @param typePattern The type pattern to look for
	 * @return The extracted variable name
	 */
	private static String extractVariableName(String line, String typePattern) {
		return line.substring(4, line.indexOf(typePattern)).trim();
	}

	/**
	 * Extracts the variable value from a declaration line.
	 *
	 * @param line The line containing the declaration
	 * @return The extracted variable value
	 */
	private static String extractVariableValue(String line) {
		return line.substring(line.indexOf(" = ") + 3, line.indexOf(";")).trim();
	}

	/**
	 * Infers the type from a value with a type suffix, from boolean literals, or from char literals.
	 * For example:
	 * - "100U64" would infer the U64 type
	 * - "true" or "false" would infer the Bool type
	 * - "'a'" (char in single quotes) would infer the U8 type
	 *
	 * @param value The value to infer the type from
	 * @return Optional containing the inferred TypeMapper, or empty if no type can be inferred
	 */
	private static Optional<TypeMapper> inferTypeFromValue(String value) {
		// Check for boolean literals
		if ("true".equals(value) || "false".equals(value)) return findTypeMapperByJavaType("Bool");

		// Check for char literals (values in single quotes)
		if (value.length() >= 3 && value.startsWith("'") && value.endsWith("'")) return findTypeMapperByJavaType("U8");

		// Check for type suffixes (e.g., 100I8, 200U16)
		for (TypeMapper mapper : TYPE_MAPPERS) {
			String suffix = mapper.javaType();
			if (value.endsWith(suffix) && value.length() > suffix.length()) {
				// Check if the characters before the suffix are numeric
				String numPart = value.substring(0, value.length() - suffix.length());
				if (numPart.matches("-?\\d+")) return Optional.of(mapper);
			}
		}

		return Optional.empty();
	}

	/**
	 * Finds a TypeMapper by its Java type name.
	 *
	 * @param javaType The Java type name to find
	 * @return Optional containing the TypeMapper, or empty if not found
	 */
	private static Optional<TypeMapper> findTypeMapperByJavaType(String javaType) {
		return Arrays.stream(TYPE_MAPPERS).filter(mapper -> mapper.javaType().equals(javaType)).findFirst();
	}

	/**
	 * Removes the type suffix from a value.
	 * For example:
	 * - "100U64" would become "100"
	 * - "'a'" would remain "'a'" (char literals keep their single quotes)
	 *
	 * @param value The value with a potential type suffix
	 * @return The value without the type suffix
	 */
	private static String removeTypeSuffix(String value) {
		// Find the type suffix if present
		Optional<TypeMapper> typeMapper = inferTypeFromValue(value);

		// No type suffix found, return the original value
		if (typeMapper.isEmpty()) return value;

		// For char literals, keep the single quotes
		if (typeMapper.get().javaType().equals("U8") && value.startsWith("'") && value.endsWith("'")) return value;

		// For other types, remove the type suffix
		String suffix = typeMapper.get().javaType();
		return value.substring(0, value.length() - suffix.length());
	}

	/**
	 * Generates C code for a variable declaration.
	 *
	 * @param cCode         The StringBuilder to append the code to
	 * @param cType         The C type of the variable
	 * @param variableName  The name of the variable
	 * @param variableValue The value of the variable
	 */
	private static void generateVariableCode(StringBuilder cCode,
																					 String cType,
																					 String variableName,
																					 String variableValue) {
		cCode.append("    ")
				 .append(cType)
				 .append(" ")
				 .append(variableName)
				 .append(" = ")
				 .append(variableValue)
				 .append(";\n");
	}
}