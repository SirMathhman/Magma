package magma;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Compiler {
	private static class ValueInfo {
		String processedValue;
		String inferredType;

		ValueInfo(String processedValue, String inferredType) {
			this.processedValue = processedValue;
			this.inferredType = inferredType;
		}
	}
	private static final Map<String, Boolean> mutableVariables = new HashMap<>();

	public static String run(String input) throws CompileException {
		mutableVariables.clear();

		if (input.trim().isEmpty()) return "";

		String[] statements = input.split(";");
		StringBuilder result = new StringBuilder();

		for (int i = 0; i < statements.length; i++) {
			String statement = statements[i].trim();
			if (statement.isEmpty()) continue;

			if (i > 0) result.append(" ");

			result.append(compileStatement(statement));
			result.append(";");
		}

		return result.toString();
	}

	private static String compileStatement(String statement) throws CompileException {
		// Handle let statements
		Pattern letPattern =
				Pattern.compile("let\\s+(mut\\s+)?([a-zA-Z_][a-zA-Z0-9_]*)\\s*(:\\s*([a-zA-Z0-9_]+))?\\s*=\\s*(.+)");
		Matcher letMatcher = letPattern.matcher(statement);

		if (letMatcher.matches()) {
			boolean isMutable = letMatcher.group(1) != null;
			String varName = letMatcher.group(2);
			String type = letMatcher.group(4);
			String value = letMatcher.group(5);

			mutableVariables.put(varName, isMutable);

			// Process value and extract type information if needed
			ValueInfo valueInfo = processValue(value);

			// If no explicit type, infer from value
			if (type == null) type = valueInfo.inferredType;

			// Convert type to C++ equivalent
			String cppType = convertType(type);

			return cppType + " " + varName + " = " + valueInfo.processedValue;
		}

		// Handle assignment statements
		Pattern assignmentPattern = Pattern.compile("([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*(.+)");
		Matcher assignmentMatcher = assignmentPattern.matcher(statement);

		if (assignmentMatcher.matches()) {
			String varName = assignmentMatcher.group(1);
			String value = assignmentMatcher.group(2);

			// Check if variable is mutable
			if (!mutableVariables.containsKey(varName) || !mutableVariables.get(varName))
				throw new CompileException("Cannot assign to immutable variable", varName);

			return varName + " = " + value;
		}

		throw new CompileException("Invalid input", statement);
	}

	private static ValueInfo processValue(String value) {
		// Handle integer literals with type suffixes
		if (value.matches("\\d+I(8|16|32|64)")) {
			String number = value.replaceAll("I(8|16|32|64)", "");
			String suffix = value.substring(number.length());
			return new ValueInfo(number, suffix);
		}

		// Handle unsigned integer literals with type suffixes
		if (value.matches("\\d+U(8|16|32|64)")) {
			String number = value.replaceAll("U(8|16|32|64)", "");
			String suffix = value.substring(number.length());
			return new ValueInfo(number, suffix);
		}

		// Handle float literals with type suffixes
		if (value.matches("\\d+\\.\\d+F(32|64)")) {
			String number = value.replaceAll("F(32|64)", "");
			String suffix = value.substring(number.length());
			if (suffix.equals("F32")) return new ValueInfo(number + "f", "F32");
			else return new ValueInfo(number, "F64");
		}

		// Handle plain integers (infer I32)
		if (value.matches("\\d+")) return new ValueInfo(value, "I32");

		// Handle plain floats (infer F32)
		if (value.matches("\\d+\\.\\d+")) return new ValueInfo(value + "f", "F32");

		// Handle boolean literals
		if (value.equals("true") || value.equals("false")) return new ValueInfo(value, "Bool");

		// Handle character literals
		if (value.matches("'.'")) return new ValueInfo(value, "U8");

		// Default: return as-is with unknown type
		return new ValueInfo(value, null);
	}

	private static String convertType(String type) {
		if (type == null) return "auto";

		switch (type) {
			case "I8":
				return "int8_t";
			case "I16":
				return "int16_t";
			case "I32":
				return "int32_t";
			case "I64":
				return "int64_t";
			case "U8":
				return "uint8_t";
			case "U16":
				return "uint16_t";
			case "U32":
				return "uint32_t";
			case "U64":
				return "uint64_t";
			case "F32":
				return "float";
			case "F64":
				return "double";
			case "Bool":
				return "bool";
			default:
				return type;
		}
	}
}
