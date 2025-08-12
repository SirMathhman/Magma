package magma;

public class Compiler {
	// TODO: Implement compiler logic
	public String compile(String source) {
		if (source.isEmpty()) {
			return "";
		}
		String s = source.trim();
		if (s.startsWith("let ") && s.endsWith(";")) {
			String body = s.substring(4, s.length() - 1).trim();
			String result = handleLet(body);
			if (result != null) {
				return result;
			}
		}
		throw new CompileException("Input is not supported");
	}

	private String handleLet(String body) {
		// Match explicit type annotation: let a : TYPE = VALUE;
	String explicitTypeRegex = "([a-zA-Z_][a-zA-Z0-9_]*)\\s*:\\s*([UI][0-9]+)\\s*=\\s*([0-9]+);?";
	java.util.regex.Pattern explicitTypePattern = java.util.regex.Pattern.compile(explicitTypeRegex);
	java.util.regex.Matcher explicitTypeMatcher = explicitTypePattern.matcher(body);
		if (explicitTypeMatcher.matches()) {
			String var = explicitTypeMatcher.group(1);
			String type = explicitTypeMatcher.group(2);
			String value = explicitTypeMatcher.group(3);
			String cType = magmaTypeToC(type);
			if (cType != null) {
				return cType + " " + var + " = " + value + ";";
			}
		}

		// Match annotated value: let x = VALUE_TYPE;
	String annotatedValueRegex = "([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*([0-9]+)([UI][0-9]+);?";
	java.util.regex.Pattern annotatedValuePattern = java.util.regex.Pattern.compile(annotatedValueRegex);
	java.util.regex.Matcher annotatedValueMatcher = annotatedValuePattern.matcher(body);
		if (annotatedValueMatcher.matches()) {
			String var = annotatedValueMatcher.group(1);
			String value = annotatedValueMatcher.group(2);
			String type = annotatedValueMatcher.group(3);
			String cType = magmaTypeToC(type);
			if (cType != null) {
				return cType + " " + var + " = " + value + ";";
			}
		}

		// Match default int32: let x = VALUE;
	String defaultIntRegex = "([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*([0-9]+);?";
	java.util.regex.Pattern defaultIntPattern = java.util.regex.Pattern.compile(defaultIntRegex);
	java.util.regex.Matcher defaultIntMatcher = defaultIntPattern.matcher(body);
		if (defaultIntMatcher.matches()) {
			String var = defaultIntMatcher.group(1);
			String value = defaultIntMatcher.group(2);
			return "int32_t " + var + " = " + value + ";";
		}
		return null;
	}

	private String magmaTypeToC(String type) {
		switch (type) {
			case "U8": return "uint8_t";
			case "U16": return "uint16_t";
			case "U32": return "uint32_t";
			case "U64": return "uint64_t";
			case "I8": return "int8_t";
			case "I16": return "int16_t";
			case "I32": return "int32_t";
			case "I64": return "int64_t";
			default: return null;
		}
	}
}
