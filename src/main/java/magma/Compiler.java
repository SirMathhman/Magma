package magma;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Compiler {
	private static final Map<String, String> TYPE_MAPPING = new HashMap<>();

	static {
		// Signed integer types
		TYPE_MAPPING.put("I8", "int8_t");
		TYPE_MAPPING.put("I16", "int16_t");
		TYPE_MAPPING.put("I32", "int32_t");
		TYPE_MAPPING.put("I64", "int64_t");

		// Unsigned integer types
		TYPE_MAPPING.put("U8", "uint8_t");
		TYPE_MAPPING.put("U16", "uint16_t");
		TYPE_MAPPING.put("U32", "uint32_t");
		TYPE_MAPPING.put("U64", "uint64_t");

		// Boolean type
		TYPE_MAPPING.put("Bool", "bool");
	}

	/**
	 * Compiles the input string to C-style code.
	 */
	public static String compile(String input) throws CompileException {
		if (input.isEmpty()) return "";

		// Try to match array type annotation
		Pattern arrayTypePattern = Pattern.compile(
				"let\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*:\\s*\\[([IU][0-9]+|Bool)\\s*;\\s*(\\d+)\\]\\s*=\\s*([^;]+);");
		Matcher arrayTypeMatcher = arrayTypePattern.matcher(input);

		if (arrayTypeMatcher.find()) {
			String variableName = arrayTypeMatcher.group(1);
			String elementType = arrayTypeMatcher.group(2);
			int arraySize = Integer.parseInt(arrayTypeMatcher.group(3));
			String value = arrayTypeMatcher.group(4);

			return compileArrayType(variableName, elementType, arraySize, value);
		}

		// Try to match with explicit type annotation
		String result = tryCompileWithExplicitType(input);
		if (!result.isEmpty()) return result;

		// Try to match without explicit type annotation
		result = tryCompileWithoutExplicitType(input);
		if (!result.isEmpty()) return result;

		throw new CompileException();
	}

	/**
	 * Compiles an array type declaration: "let x : [U8; 5] = "hello";"
	 */
	private static String compileArrayType(String variableName, String elementType, int arraySize, String value)
			throws CompileException {
		String cType = TYPE_MAPPING.get(elementType);
		if (cType == null) throw new CompileException();

		// Check if the value is a string literal (like "hello")
		Pattern stringLiteralPattern = Pattern.compile("\"([^\"]*)\"");
		Matcher stringLiteralMatcher = stringLiteralPattern.matcher(value);

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

	/**
	 * Tries to compile a declaration with explicit type annotation: "let x : TYPE = value;"
	 */
	private static String tryCompileWithExplicitType(String input) throws CompileException {
		Pattern letPatternWithType =
				Pattern.compile("let\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*:\\s*([IU][0-9]+|Bool)\\s*=\\s*([^;]+);");
		Matcher matcherWithType = letPatternWithType.matcher(input);

		if (!matcherWithType.find()) return "";

		String variableName = matcherWithType.group(1);
		String typeAnnotation = matcherWithType.group(2);
		String value = matcherWithType.group(3);

		String cType = TYPE_MAPPING.get(typeAnnotation);
		if (cType == null) throw new CompileException();

		// Special handling for Bool type
		if (typeAnnotation.equals("Bool") && (!value.equals("true") && !value.equals("false")))
			throw new CompileException();

		// Process the value based on its format
		value = processValueWithExplicitType(value, typeAnnotation);

		return cType + " " + variableName + " = " + value + ";";
	}

	/**
	 * Processes the value part of a variable declaration with explicit type annotation.
	 */
	private static String processValueWithExplicitType(String value, String typeAnnotation) throws CompileException {
		// Check if the value is a character literal (like 'a')
		Pattern charLiteralPattern = Pattern.compile("'(.)'");
		Matcher charLiteralMatcher = charLiteralPattern.matcher(value);

		if (charLiteralMatcher.matches()) {
			// Character literals are only allowed with U8 type
			if (!typeAnnotation.equals("U8")) throw new CompileException();

			// Get the character and convert it to its ASCII/Unicode value
			char character = charLiteralMatcher.group(1).charAt(0);
			return String.valueOf((int) character);
		}

		// Check if the value has a type suffix (like 100U64)
		Pattern typeSuffixPattern = Pattern.compile("(\\d+)([IU][0-9]+)");
		Matcher typeSuffixMatcher = typeSuffixPattern.matcher(value);

		if (typeSuffixMatcher.matches()) {
			String baseValue = typeSuffixMatcher.group(1);
			String typeSuffix = typeSuffixMatcher.group(2);

			// Check if the type suffix is compatible with the explicit type annotation
			if (!typeAnnotation.equals(typeSuffix)) throw new CompileException();

			// Use the base value without the type suffix
			return baseValue;
		}

		// Return the original value if no special processing is needed
		return value;
	}

	/**
	 * Tries to compile a declaration without explicit type annotation: "let x = value;"
	 * Also handles type suffixes like "let x = 100U64;" and character literals like 'a'
	 */
	private static String tryCompileWithoutExplicitType(String input) throws CompileException {
		Pattern letPattern = Pattern.compile("let\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*([^;]+);");
		Matcher matcher = letPattern.matcher(input);

		if (!matcher.find()) return "";

		String variableName = matcher.group(1);
		String value = matcher.group(2);

		// Check if the value is a character literal (like 'a')
		Pattern charLiteralPattern = Pattern.compile("'(.)'");
		Matcher charLiteralMatcher = charLiteralPattern.matcher(value);

		if (charLiteralMatcher.matches()) {
			// Character literals are automatically assigned U8 type
			char character = charLiteralMatcher.group(1).charAt(0);
			return "uint8_t " + variableName + " = " + (int) character + ";";
		}

		// Check if the value has a type suffix (like 100U64)
		Pattern typeSuffixPattern = Pattern.compile("(\\d+)([IU][0-9]+)");
		Matcher typeSuffixMatcher = typeSuffixPattern.matcher(value);

		if (typeSuffixMatcher.matches()) {
			String baseValue = typeSuffixMatcher.group(1);
			String typeSuffix = typeSuffixMatcher.group(2);

			String cType = TYPE_MAPPING.get(typeSuffix);
			if (cType == null) throw new CompileException();

			return cType + " " + variableName + " = " + baseValue + ";";
		}

		// Check for boolean literals
		if (value.equals("true") || value.equals("false")) return "bool " + variableName + " = " + value + ";";

		// Default to int32_t if no type suffix and not a boolean literal or character literal
		return "int32_t " + variableName + " = " + value + ";";
	}
}