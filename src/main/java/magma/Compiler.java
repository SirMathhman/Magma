package magma;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Compiler {
	private static final Pattern LET_PATTERN = Pattern.compile("let\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*(?:\\s*:\\s*(I8|I16|I32|I64|U8|U16|U32|U64))?\\s*=\\s*([0-9]+)(?:(I8|I16|I32|I64|U8|U16|U32|U64))?;");

	public static String compile(String input) throws CompileException {
		if (input.isEmpty()) return "";

		Matcher matcher = LET_PATTERN.matcher(input);
		if (matcher.matches()) {
			return processLetStatement(matcher);
		}

		throw new CompileException();
	}
	
	private static String processLetStatement(Matcher matcher) {
		String varName = matcher.group(1);
		String typeAnnotation = matcher.group(2);
		String value = matcher.group(3);
		String suffix = matcher.group(4);
		
		String type = determineType(typeAnnotation, suffix);
		return type + " " + varName + " = " + value + ";";
	}
	
	private static String determineType(String typeAnnotation, String suffix) {
		if (suffix != null) {
			return mapTypeToC(suffix);
		} else if (typeAnnotation != null) {
			return mapTypeToC(typeAnnotation);
		} else {
			return "int32_t"; // Default type
		}
	}
	
	private static String mapTypeToC(String type) {
		return mapSignedType(type, mapUnsignedType(type));
	}
	
	private static String mapSignedType(String type, String defaultType) {
		switch (type) {
			case "I8": return "int8_t";
			case "I16": return "int16_t";
			case "I32": return "int32_t";
			case "I64": return "int64_t";
			default: return defaultType;
		}
	}
	
	private static String mapUnsignedType(String type) {
		switch (type) {
			case "U8": return "uint8_t";
			case "U16": return "uint16_t";
			case "U32": return "uint32_t";
			case "U64": return "uint64_t";
			default: return "int32_t";
		}
	}
}