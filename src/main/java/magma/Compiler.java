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

	public static String compile(String input) throws CompileException {
		if (input.isEmpty()) return "";

		// Transform "let x : TYPE = 100;" to corresponding C type
		Pattern letPatternWithType =
				Pattern.compile("let\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*:\\s*([IU][0-9]+)\\s*=\\s*([^;]+);");
		Matcher matcherWithType = letPatternWithType.matcher(input);

		if (matcherWithType.find()) {
			String variableName = matcherWithType.group(1);
			String typeAnnotation = matcherWithType.group(2);
			String value = matcherWithType.group(3);

			String cType = TYPE_MAPPING.get(typeAnnotation);
			if (cType == null) throw new CompileException();

			return cType + " " + variableName + " = " + value + ";";
		}

		// Transform "let x = 100;" to "int32_t x = 100;" (backward compatibility)
		Pattern letPattern = Pattern.compile("let\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*([^;]+);");
		Matcher matcher = letPattern.matcher(input);

		if (matcher.find()) {
			String variableName = matcher.group(1);
			String value = matcher.group(2);
			return "int32_t " + variableName + " = " + value + ";";
		}

		throw new CompileException();
	}
}
