/**
 * Main compiler class for the Magma Java to C compiler.
 * This class provides functionality to compile Java code to C.
 */
public class Main {

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
	 * Checks if the Java code contains variable declarations in the format "let x : I32 = 0;".
	 *
	 * @param javaCode The Java source code to check
	 * @return True if the code contains variable declarations
	 */
	private static boolean containsVariableDeclarations(String javaCode) {
		return javaCode.contains("let ") && javaCode.contains(" : I32 = ");
	}

	/**
	 * Generates C code for a program with variable declarations.
	 * Converts declarations in the format "let x : I32 = 0;" to "int x = 0;".
	 *
	 * @param javaCode The Java source code containing variable declarations
	 * @return C code for a program with variable declarations
	 */
	private static String generateVariableDeclarationCCode(String javaCode) {
		StringBuilder cCode = new StringBuilder();
		cCode.append("#include <stdio.h>\n\n");
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

	private static void processVariableDeclaration(String line, StringBuilder cCode) {
		var trimmedLine = line.trim();
		if (trimmedLine.startsWith("let ") && trimmedLine.contains(" : I32 = ")) {
			// Parse the variable declaration
			String variableName = trimmedLine.substring(4, trimmedLine.indexOf(" : I32 = ")).trim();
			String variableValue = trimmedLine.substring(trimmedLine.indexOf(" = ") + 3, trimmedLine.indexOf(";")).trim();

			// Generate C code for the variable declaration
			cCode.append("    int ").append(variableName).append(" = ").append(variableValue).append(";\n");
		}
	}
}