package magma;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Compiler {
	private static final Pattern LET_PATTERN =
			Pattern.compile("^let\\s+(\\w+)(?:\\s*:\\s*([UI]\\d+))?\\s*=\\s*(\\d+)([UI]\\d+)?;$");

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
		if (input.isEmpty()) return input;

		Matcher matcher = LET_PATTERN.matcher(input.trim());
		if (matcher.matches()) {
			String variableName = matcher.group(1);
			String declaredType = matcher.group(2);
			String value = matcher.group(3);
			String typeSuffix = matcher.group(4);

			String cType = resolveType(declaredType, typeSuffix);
			return cType + " " + variableName + " = " + value + ";";
		}

		throw new CompileException("Invalid input: " + input);
	}

	private static String resolveType(String declaredType, String typeSuffix) throws CompileException {
		if (typeSuffix != null && declaredType != null) {
			validateTypeConsistency(declaredType, typeSuffix);
			return mapType(typeSuffix);
		}
		
		if (typeSuffix != null) {
			return mapType(typeSuffix);
		}
		
		if (declaredType != null) {
			return mapType(declaredType);
		}
		
		return "int32_t";
	}

	private static void validateTypeConsistency(String declaredType, String typeSuffix) throws CompileException {
		if (!typeSuffix.equals(declaredType)) {
			throw new CompileException("Type conflict: declared type " + declaredType + " does not match suffix type " + typeSuffix);
		}
	}

	private static String mapType(String type) throws CompileException {
		String cType = TYPE_MAPPING.get(type);
		if (cType == null) {
			throw new CompileException("Unsupported type: " + type);
		}
		return cType;
	}
}
