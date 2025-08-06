import java.util.Arrays;
import java.util.Optional;

/**
 * Main compiler class for the Magma to C compiler.
 * This class provides functionality to compile Magma code to C.
 * Supports basic Magma constructs, various integer types (I8-I64, U8-U64), Bool type, and Char type.
 * Also supports typeless variable declarations where the type is inferred:
 * - If the value has a type suffix (e.g., 100U64), the type is inferred from the suffix.
 * - If the value is a char literal in single quotes (e.g., 'a'), the Char type (U8) is inferred.
 * - If the value is a boolean literal (true/false), the Bool type is inferred.
 * - If no type suffix is present, defaults to I32 for numbers.
 * Supports array declarations with syntax: let myArray : [Type, Size] = [val1, val2, ...];
 */
public class Main {
	/**
	 * Record to hold array declaration information.
	 * This eliminates the need to pass around multiple related parameters.
	 */
	private record ArrayDeclaration(String name, String type, int size, String elements) {}

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
	 * Also supports array declarations with syntax: let myArray : [Type, Size] = [val1, val2, ...];
	 *
	 * @param magmaCode The Magma source code to compile
	 * @return The compiled C code
	 */
	public static String compile(String magmaCode) {
		// This is a simple implementation that works for specific patterns
		// In a real compiler, we would parse the Magma code and generate C code

		// Check if the code contains any declarations (array or variable)
		if (containsDeclarations(magmaCode)) {
			return generateDeclarationCCode(magmaCode);
		} else {
			// Default case for unsupported code
			return "";
		}
	}

	/**
	 * Checks if the Magma code contains any declarations (array or variable).
	 * Supports array declarations in the format "let x : [Type, Size] = [val1, val2, ...];"
	 * Supports variable declarations in the format "let x : Type = value;" or "let x = value;"
	 * Supports all basic types (I8-I64, U8-U64, Bool, Char).
	 *
	 * @param magmaCode The Magma source code to check
	 * @return True if the code contains any declarations
	 */
	private static boolean containsDeclarations(String magmaCode) {
		if (!magmaCode.contains("let ")) {
			return false;
		}

		// Check for array declarations
		boolean hasArrayDeclarations =
				magmaCode.matches("(?s).*let\\s+[a-zA-Z_][a-zA-Z0-9_]*\\s+:\\s+\\[[a-zA-Z0-9]+,\\s*[0-9]+]\\s+=\\s+\\[.*");

		// Check for variable declarations with explicit types
		boolean hasExplicitTypeDeclarations = false;
		for (TypeMapper typeMapper : TYPE_MAPPERS) {
			if (magmaCode.contains(typeMapper.typePattern())) {
				hasExplicitTypeDeclarations = true;
				break;
			}
		}

		// Check for typeless declarations (let x = value;)
		boolean hasTypelessDeclarations = magmaCode.matches("(?s).*let\\s+[a-zA-Z_][a-zA-Z0-9_]*\\s+=\\s+.*");

		return hasArrayDeclarations || hasExplicitTypeDeclarations || hasTypelessDeclarations;
	}

	/**
	 * Generates C code for a program with declarations (array or variable).
	 * Handles both array declarations in the format "let x : [Type, Size] = [val1, val2, ...];"
	 * and variable declarations in the format "let x : Type = value;" or "let x = value;".
	 * Supports all basic types (I8-I64, U8-U64, Bool, Char).
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
		if (magmaCode.contains("[Bool,") || magmaCode.contains("[Bool ,") || magmaCode.contains(" : Bool =") ||
				magmaCode.contains(" = true") || magmaCode.contains(" = false")) {
			cCode.append("#include <stdbool.h>\n");
		}
	}

	/**
	 * Processes a single line of Java code to extract array declarations.
	 * Supports array declarations in the format "let x : [Type, Size] = [val1, val2, ...];"
	 * Supports all basic types (I8-I64, U8-U64, Bool, Char).
	 *
	 * @param line  The line of Java code to process
	 * @param cCode The StringBuilder to append the generated C code to
	 */
	private static void processArrayDeclaration(String line, StringBuilder cCode) {
		var trimmedLine = line.trim();
		if (!isArrayDeclaration(trimmedLine)) {
			return;
		}

		// Create an ArrayDeclaration record to hold all array information
		ArrayDeclaration arrayDecl = parseArrayDeclaration(trimmedLine);

		// Find the C type for the array element type
		Optional<TypeMapper> typeMapper = findTypeMapperByJavaType(arrayDecl.type());
		if (typeMapper.isEmpty()) {
			return;
		}

		// Generate C code for the array declaration
		generateArrayCode(cCode, typeMapper.get().cType(), arrayDecl);
	}

	/**
	 * Checks if a line contains an array declaration.
	 *
	 * @param line The line to check
	 * @return True if the line contains an array declaration
	 */
	private static boolean isArrayDeclaration(String line) {
		return line.startsWith("let ") && line.contains(" : [") && line.contains("] = [");
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
	 * Extracts the array name from an array declaration line.
	 *
	 * @param line The line containing the array declaration
	 * @return The extracted array name
	 */
	private static String extractArrayName(String line) {
		// Format: let arrayName : [Type, Size] = [elements];
		return line.substring(4, line.indexOf(" : [")).trim();
	}

	/**
	 * Extracts the array type from an array declaration line.
	 *
	 * @param line The line containing the array declaration
	 * @return The extracted array type
	 */
	private static String extractArrayType(String line) {
		// Format: let arrayName : [Type, Size] = [elements];
		int startIndex = line.indexOf("[") + 1;
		int endIndex = line.indexOf(",", startIndex);
		return line.substring(startIndex, endIndex).trim();
	}

	/**
	 * Extracts the array size from an array declaration line.
	 *
	 * @param line The line containing the array declaration
	 * @return The extracted array size
	 */
	private static int extractArraySize(String line) {
		// Format: let arrayName : [Type, Size] = [elements];
		int startIndex = line.indexOf(",") + 1;
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
		// Format: let arrayName : [Type, Size] = [elements];
		int startIndex = line.lastIndexOf("[") + 1;
		int endIndex = line.lastIndexOf("]");
		return line.substring(startIndex, endIndex).trim();
	}

	/**
	 * Processes a single line of Java code to extract variable declarations.
	 * Supports I8, I16, I32, I64, U8, U16, U32, U64, Bool, and Char types.
	 * Also supports typeless declarations where the type is inferred (defaulting to I32 for numbers).
	 * For boolean literals (true/false), the Bool type is inferred.
	 * For char literals in single quotes (e.g., 'a'), the Char type (U8) is inferred.
	 * Skips array declarations to avoid duplicate processing.
	 *
	 * @param line  The line of Java code to process
	 * @param cCode The StringBuilder to append the generated C code to
	 */
	private static void processVariableDeclaration(String line, StringBuilder cCode) {
		var trimmedLine = line.trim();
		if (!trimmedLine.startsWith("let ")) {
			return;
		}

		// Skip array declarations to avoid duplicate processing
		if (isArrayDeclaration(trimmedLine)) {
			return;
		}

		// Check if this is a declaration with an explicit type
		Optional<TypeMapper> matchedMapper = findMatchingTypeMapper(trimmedLine);

		if (matchedMapper.isPresent()) {
			// Process declaration with explicit type
			processTypeMapper(cCode, matchedMapper.get(), trimmedLine);
		} else if (trimmedLine.contains(" = ")) {
			// Process typeless declaration
			processTypelessDeclaration(cCode, trimmedLine);
		}
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
	 * - If the value is a char literal in single quotes (e.g., 'a'), the Char type (U8) is inferred.
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
		TypeMapper typeMapper = inferredMapper.orElseGet(() -> Arrays.stream(TYPE_MAPPERS)
																																 .filter(mapper -> mapper.javaType().equals("I32"))
																																 .findFirst()
																																 .orElseThrow(() -> new IllegalStateException(
																																		 "I32 type mapper not found")));

		// Remove type suffix from value if present
		String cleanValue = removeTypeSuffix(variableValue);

		// Generate C code for the variable declaration
		generateVariableCode(cCode, typeMapper.cType(), variableName, cleanValue);
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
	 * - "'a'" (char in single quotes) would infer the Char type
	 *
	 * @param value The value to infer the type from
	 * @return Optional containing the inferred TypeMapper, or empty if no type can be inferred
	 */
	private static Optional<TypeMapper> inferTypeFromValue(String value) {
		// Check for boolean literals
		if ("true".equals(value) || "false".equals(value)) {
			return findTypeMapperByJavaType("Bool");
		}

		// Check for char literals (values in single quotes)
		if (value.length() >= 3 && value.startsWith("'") && value.endsWith("'")) {
			return findTypeMapperByJavaType("Char");
		}

		// Check each type suffix
		return Arrays.stream(TYPE_MAPPERS).filter(mapper -> value.endsWith(mapper.javaType())).findFirst();
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

		if (typeMapper.isPresent()) {
			// For char literals, keep the single quotes
			if (typeMapper.get().javaType().equals("Char")) {
				return value;
			}

			// For other types, remove the type suffix
			String suffix = typeMapper.get().javaType();
			return value.substring(0, value.length() - suffix.length());
		}

		// No type suffix found, return the original value
		return value;
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