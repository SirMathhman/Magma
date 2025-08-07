package magma;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles compilation of array type declarations in Magma.
 */
public class ArrayTypeCompiler {
	private static final Pattern ARRAY_TYPE_PATTERN = Pattern.compile(
			"let\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*:\\s*\\[([IU][0-9]+|Bool)\\s*;\\s*(\\d+)\\]\\s*=\\s*([^;]+);");
	private static final Pattern STRING_LITERAL_PATTERN = Pattern.compile("\"([^\"]*)\"");

	/**
	 * Tries to compile an array type declaration.
	 *
	 * @param input The input string to compile
	 * @return An Optional containing the compiled C code, or empty if the input doesn't match an array type declaration
	 * @throws CompileException If the compilation fails
	 */
	public static Optional<String> tryCompile(String input) throws CompileException {
		Matcher arrayTypeMatcher = ARRAY_TYPE_PATTERN.matcher(input);

		if (!arrayTypeMatcher.find()) return Optional.empty();

		String variableName = arrayTypeMatcher.group(1);
		String elementType = arrayTypeMatcher.group(2);
		int arraySize = Integer.parseInt(arrayTypeMatcher.group(3));
		String value = arrayTypeMatcher.group(4);

		return Optional.of(compileArrayType(variableName, elementType, arraySize, value));
	}

	/**
	 * Compiles an array type declaration: "let x : [U8; 5] = "hello";"
	 */
	private static String compileArrayType(String variableName, String elementType, int arraySize, String value)
			throws CompileException {
		String cType = TypeMapper.getCType(elementType);
		if (cType == null) throw new CompileException();

		// Check if the value is a string literal (like "hello")
		Matcher stringLiteralMatcher = STRING_LITERAL_PATTERN.matcher(value);

		if (stringLiteralMatcher.matches()) {
			// String literals are only allowed with U8 arrays
			if (!elementType.equals("U8")) throw new CompileException();

			String stringContent = stringLiteralMatcher.group(1);

			// Validate that the string length matches the array size
			if (stringContent.length() != arraySize) throw new CompileException();

			// Convert the string to an array initializer
			StringBuilder arrayInitializer = new StringBuilder("{");
			for (int i = 0; i < stringContent.length(); i++) {
				if (i > 0) arrayInitializer.append(", ");
				arrayInitializer.append((int) stringContent.charAt(i));
			}
			arrayInitializer.append("}");

			return cType + " " + variableName + "[" + arraySize + "] = " + arrayInitializer + ";";
		}

		// For now, we only support string literals for array initializers
		throw new CompileException();
	}
}