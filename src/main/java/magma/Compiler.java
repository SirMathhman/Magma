package magma;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Compiler {
	private static final Pattern LET_PATTERN = Pattern.compile("^let\\s+(\\w+)(?:\\s*:\\s*([UI]\\d+))?\\s*=\\s*(\\d+);$");

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

			String cType;
			if (declaredType != null) {
				cType = TYPE_MAPPING.get(declaredType);
				if (cType == null) throw new CompileException("Unsupported type: " + declaredType);
			} else cType = "int32_t";

			return cType + " " + variableName + " = " + value + ";";
		}

		throw new CompileException("Invalid input: " + input);
	}
}
