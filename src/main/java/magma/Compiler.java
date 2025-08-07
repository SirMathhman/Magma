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
	}

	/**
	 * Compiles the input string to C-style code.
	 */
	public static String compile(String input) throws CompileException {
		if (input.isEmpty()) return "";

		// Try to match with explicit type annotation
		String result = tryCompileWithExplicitType(input);
		if (!result.isEmpty()) return result;

		// Try to match without explicit type annotation
		result = tryCompileWithoutExplicitType(input);
		if (!result.isEmpty()) return result;

		throw new CompileException();
	}

	/**
	 * Tries to compile a declaration with explicit type annotation: "let x : TYPE = value;"
	 */
	private static String tryCompileWithExplicitType(String input) throws CompileException {
		Pattern letPatternWithType =
				Pattern.compile("let\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*:\\s*([IU][0-9]+)\\s*=\\s*([^;]+);");
		Matcher matcherWithType = letPatternWithType.matcher(input);

		if (!matcherWithType.find()) return "";

		String variableName = matcherWithType.group(1);
		String typeAnnotation = matcherWithType.group(2);
		String value = matcherWithType.group(3);

		String cType = TYPE_MAPPING.get(typeAnnotation);
		if (cType == null) throw new CompileException();

		return cType + " " + variableName + " = " + value + ";";
	}

	/**
	 * Tries to compile a declaration without explicit type annotation: "let x = value;"
	 * Also handles type suffixes like "let x = 100U64;"
	 */
	private static String tryCompileWithoutExplicitType(String input) throws CompileException {
		Pattern letPattern = Pattern.compile("let\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*([^;]+);");
		Matcher matcher = letPattern.matcher(input);

		if (!matcher.find()) return "";

		String variableName = matcher.group(1);
		String value = matcher.group(2);

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

		// Default to int32_t if no type suffix
		return "int32_t " + variableName + " = " + value + ";";
	}
}
