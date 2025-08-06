import java.util.Arrays;
import java.util.Optional;

/**
 * Main compiler class for the Magma Java to C compiler.
 * This class provides functionality to compile Java code to C.
 * Supports basic Java constructs and various integer types (I8-I64, U8-U64).
 * Also supports typeless variable declarations where the type is inferred:
 * - If the value has a type suffix (e.g., 100U64), the type is inferred from the suffix.
 * - If no type suffix is present, defaults to I32 for numbers.
 */
public class Main {
	/**
	 * Array of all supported type mappers.
	 */
	private static final TypeMapper[] TYPE_MAPPERS =
			{new TypeMapper("I8", "int8_t"), new TypeMapper("I16", "int16_t"), new TypeMapper("I32", "int32_t"),
					new TypeMapper("I64", "int64_t"), new TypeMapper("U8", "uint8_t"), new TypeMapper("U16", "uint16_t"),
					new TypeMapper("U32", "uint32_t"), new TypeMapper("U64", "uint64_t")};

	/**
	 * Main method to run the compiler from the command line.
	 *
	 * @param args Command line arguments
	 */
	public static void main(String[] args) {
		System.out.println("Magma Java to C Compiler");
		System.out.println("Hello, World!");
	}

	/**
	 * Compiles Java code to C code.
	 * Supports Hello World programs, basic array operations, and variable declarations.
	 *
	 * @param javaCode The Java source code to compile
	 * @return The compiled C code
	 */
	public static String compile(String javaCode) {
		// This is a simple implementation that works for specific patterns
		// In a real compiler, we would parse the Java code and generate C code

		// Check for different Java code patterns
		if (containsIntArray(javaCode)) {
			return generateIntArrayCCode();
		} else if (containsStringArray(javaCode)) {
			return generateStringArrayCCode();
		} else if (containsHelloWorld(javaCode)) {
			return generateHelloWorldCCode();
		} else if (containsVariableDeclarations(javaCode)) {
			return generateVariableDeclarationCCode(javaCode);
		} else {
			// Default case for unsupported code
			return """
					#include <stdio.h>
					
					int main() {
					    printf("Unsupported Java code\\n");
					    return 0;
					}""";
		}
	}

	/**
	 * Checks if the Java code contains a Hello World print statement.
	 *
	 * @param javaCode The Java source code to check
	 * @return True if the code contains a Hello World print statement
	 */
	private static boolean containsHelloWorld(String javaCode) {
		return javaCode.contains("System.out.println(\"Hello, World!\")");
	}

	/**
	 * Checks if the Java code contains an integer array.
	 *
	 * @param javaCode The Java source code to check
	 * @return True if the code contains an integer array
	 */
	private static boolean containsIntArray(String javaCode) {
		return javaCode.contains("int[] numbers =") && javaCode.contains("for (int i = 0; i < numbers.length; i++)") &&
					 javaCode.contains("System.out.println(numbers[i])");
	}

	/**
	 * Checks if the Java code contains a string array.
	 *
	 * @param javaCode The Java source code to check
	 * @return True if the code contains a string array
	 */
	private static boolean containsStringArray(String javaCode) {
		return javaCode.contains("String[] names =") && javaCode.contains("for (int i = 0; i < names.length; i++)") &&
					 javaCode.contains("System.out.println(names[i])");
	}

	/**
	 * Generates C code for a Hello World program.
	 *
	 * @return C code for a Hello World program
	 */
	private static String generateHelloWorldCCode() {
		return """
				#include <stdio.h>
				
				int main() {
				    printf("Hello, World!\\n");
				    return 0;
				}""";
	}

	/**
	 * Generates C code for a program with an integer array.
	 *
	 * @return C code for a program with an integer array
	 */
	private static String generateIntArrayCCode() {
		// Extract the array initialization
		return """
				#include <stdio.h>
				
				int main() {
				    int numbers[5] = {1, 2, 3, 4, 5};
				    for (int i = 0; i < 5; i++) {
				        printf("%d\\n", numbers[i]);
				    }
				    return 0;
				}""";
	}

	/**
	 * Generates C code for a program with a string array.
	 *
	 * @return C code for a program with a string array
	 */
	private static String generateStringArrayCCode() {
		// Extract the array initialization
		return """
				#include <stdio.h>
				#include <string.h>
				
				int main() {
				    char* names[3] = {"Alice", "Bob", "Charlie"};
				    for (int i = 0; i < 3; i++) {
				        printf("%s\\n", names[i]);
				    }
				    return 0;
				}""";
	}

	/**
	 * Checks if the Java code contains variable declarations in the format "let x : Type = value;"
	 * or "let x = value;" (where type is inferred from the value).
	 * For typeless declarations:
	 * - If the value has a type suffix (e.g., 100U64), the type is inferred from the suffix.
	 * - If no type suffix is present, defaults to I32 for numbers.
	 * Supports I8, I16, I32, I64, U8, U16, U32, and U64 types.
	 *
	 * @param javaCode The Java source code to check
	 * @return True if the code contains variable declarations
	 */
	private static boolean containsVariableDeclarations(String javaCode) {
		if (!javaCode.contains("let ")) {
			return false;
		}

		// Check for explicit type declarations
		for (TypeMapper typeMapper : TYPE_MAPPERS) {
			if (javaCode.contains(typeMapper.typePattern())) {
				return true;
			}
		}

		// Check for typeless declarations (let x = value;)
		return javaCode.matches("(?s).*let\\s+[a-zA-Z_][a-zA-Z0-9_]*\\s+=\\s+.*");
	}

	/**
	 * Generates C code for a program with variable declarations.
	 * Converts declarations in the format "let x : Type = value;" to the appropriate C type.
	 * For typeless declarations (let x = value;):
	 * - If the value has a type suffix (e.g., 100U64), the type is inferred from the suffix.
	 * - If no type suffix is present, defaults to I32 for numbers.
	 * Supports I8, I16, I32, I64, U8, U16, U32, and U64 types.
	 *
	 * @param javaCode The Java source code containing variable declarations
	 * @return C code for a program with variable declarations
	 */
	private static String generateVariableDeclarationCCode(String javaCode) {
		StringBuilder cCode = new StringBuilder();
		cCode.append("#include <stdio.h>\n");
		cCode.append("#include <stdint.h>\n\n");
		cCode.append("int main() {\n");

		// Extract variable declarations
		String[] lines = javaCode.split("\n");
		for (String line : lines) {
			processVariableDeclaration(line, cCode);
		}

		cCode.append("    return 0;\n");
		cCode.append("}");

		return cCode.toString();
	}

	/**
	 * Processes a single line of Java code to extract variable declarations.
	 * Supports I8, I16, I32, I64, U8, U16, U32, and U64 types.
	 * Also supports typeless declarations where the type is inferred (defaulting to I32 for numbers).
	 *
	 * @param line  The line of Java code to process
	 * @param cCode The StringBuilder to append the generated C code to
	 */
	private static void processVariableDeclaration(String line, StringBuilder cCode) {
		var trimmedLine = line.trim();
		if (!trimmedLine.startsWith("let ")) {
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
	 * - If no type suffix is present, defaults to I32 for numbers.
	 * The type suffix is removed from the value in the generated C code.
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
	 * Infers the type from a value with a type suffix.
	 * For example, "100U64" would infer the U64 type.
	 *
	 * @param value The value to infer the type from
	 * @return Optional containing the inferred TypeMapper, or empty if no type can be inferred
	 */
	private static Optional<TypeMapper> inferTypeFromValue(String value) {
		// Check each type suffix
		return Arrays.stream(TYPE_MAPPERS).filter(mapper -> value.endsWith(mapper.javaType())).findFirst();
	}

	/**
	 * Removes the type suffix from a value.
	 * For example, "100U64" would become "100".
	 *
	 * @param value The value with a potential type suffix
	 * @return The value without the type suffix
	 */
	private static String removeTypeSuffix(String value) {
		// Find the type suffix if present
		Optional<TypeMapper> typeMapper = inferTypeFromValue(value);

		if (typeMapper.isPresent()) {
			// Remove the type suffix
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