package magma;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

	private static class VariableInfo {
		boolean mutable;
		String type;
		String dimensions; // For arrays like "[3]" or "[2][3]"

		VariableInfo(boolean mutable, String type, String dimensions) {
			this.mutable = mutable;
			this.type = type;
			this.dimensions = dimensions;
		}
	}

	private static final Map<String, VariableInfo> variables = new HashMap<>();

	public static String run(String input) throws CompileException {
		variables.clear();

		if (input.trim().isEmpty()) return "";

		List<String> statements = splitStatements(input);
		StringBuilder result = new StringBuilder();

		for (int i = 0; i < statements.size(); i++) {
			String statement = statements.get(i).trim();
			if (statement.isEmpty()) continue;

			if (i > 0) result.append(" ");

			result.append(compileStatement(statement));
			result.append(";");
		}

		return result.toString();
	}

	private static List<String> splitStatements(String input) {
		List<String> statements = new ArrayList<>();
		int level = 0;
		int start = 0;

		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (c == '[') level++;
			else if (c == ']') level--;
			else if (c == ';' && level == 0) {
				statements.add(input.substring(start, i));
				start = i + 1;
			}
		}
		if (start < input.length()) statements.add(input.substring(start));

		return statements;
	}

	private static String compileStatement(String statement) throws CompileException {
		// Handle array indexing get (let y = x[1]) - check this FIRST
		Pattern indexGetPattern = Pattern.compile(
				"let\\s+(mut\\s+)?([a-zA-Z_][a-zA-Z0-9_]*)\\s*(:\\s*([a-zA-Z0-9_]+))?\\s*=\\s*([a-zA-Z_][a-zA-Z0-9_]*)\\[(.+?)]");
		Matcher indexGetMatcher = indexGetPattern.matcher(statement);

		if (indexGetMatcher.matches()) {
			boolean isMutable = indexGetMatcher.group(1) != null;
			String varName = indexGetMatcher.group(2);
			String typeDecl = indexGetMatcher.group(4);
			String sourceVar = indexGetMatcher.group(5);
			String index = indexGetMatcher.group(6);

			VariableInfo sourceInfo = variables.get(sourceVar);
			if (sourceInfo == null) throw new CompileException("Undefined variable", sourceVar);

			if (sourceInfo.dimensions.isEmpty()) throw new CompileException("Variable is not an array", sourceVar);

			// Type check
			if (typeDecl != null && isTypeIncompatible(typeDecl, sourceInfo.type))
				throw new CompileException("Type mismatch", "Cannot assign " + sourceInfo.type + " to " + typeDecl);

			// If no explicit type, use source array element type
			String resultType = typeDecl != null ? typeDecl : sourceInfo.type;
			variables.put(varName, new VariableInfo(isMutable, resultType, ""));
			String cppType = convertType(resultType);
			return cppType + " " + varName + " = " + sourceVar + "[" + index + "]";
		}

		// Handle property access (like x.length) - check this SECOND
		Pattern propertyPattern = Pattern.compile(
				"let\\s+(mut\\s+)?([a-zA-Z_][a-zA-Z0-9_]*)\\s*(:\\s*([a-zA-Z0-9_]+))?\\s*=\\s*([a-zA-Z_][a-zA-Z0-9_]*)\\.([a-zA-Z_][a-zA-Z0-9_]*)");
		Matcher propertyMatcher = propertyPattern.matcher(statement);

		if (propertyMatcher.matches()) {
			boolean isMutable = propertyMatcher.group(1) != null;
			String varName = propertyMatcher.group(2);
			String typeDecl = propertyMatcher.group(4);
			String sourceVar = propertyMatcher.group(5);
			String property = propertyMatcher.group(6);

			if ("length".equals(property)) {
				VariableInfo sourceInfo = variables.get(sourceVar);
				if (sourceInfo == null || sourceInfo.dimensions.isEmpty())
					throw new CompileException("Variable is not an array", sourceVar);

				// Type check
				if (typeDecl != null && !"USize".equals(typeDecl))
					throw new CompileException("Length property must be USize type", typeDecl);

				// Extract first dimension
				String firstDim = sourceInfo.dimensions.substring(1, sourceInfo.dimensions.indexOf(']'));

				variables.put(varName, new VariableInfo(isMutable, "USize", ""));
				return "usize_t " + varName + " = " + firstDim;
			}
		}

		// Handle let statements with arrays - check this THIRD
		Pattern letPattern = Pattern.compile("let\\s+(mut\\s+)?([a-zA-Z_][a-zA-Z0-9_]*)\\s*(:\\s*(.+?))?\\s*=\\s*(.+)");
		Matcher letMatcher = letPattern.matcher(statement);

		if (letMatcher.matches()) {
			boolean isMutable = letMatcher.group(1) != null;
			String varName = letMatcher.group(2);
			String typeDecl = letMatcher.group(4);
			String value = letMatcher.group(5);

			// Process value and extract type information
			ValueInfo valueInfo = processValue(value);

			String inferredType = valueInfo.inferredType;
			String dimensions = "";

			// Parse array type declaration
			// Type inference for arrays
			if (typeDecl != null) if (typeDecl.startsWith("[")) {
				// Array type like [I32; 3] or [I32; 2, 3]
				Pattern arrayTypePattern = Pattern.compile("\\[([a-zA-Z0-9_]+);\\s*([0-9, ]+)]");
				Matcher arrayTypeMatcher = arrayTypePattern.matcher(typeDecl);
				if (arrayTypeMatcher.matches()) {
					String elementType = arrayTypeMatcher.group(1);
					String dimStr = arrayTypeMatcher.group(2);
					String[] dims = dimStr.split(",\\s*");

					// Build dimensions string
					StringBuilder dimBuilder = new StringBuilder();
					for (String dim : dims) dimBuilder.append("[").append(dim.trim()).append("]");
					dimensions = dimBuilder.toString();

					// Validate type compatibility (but allow empty arrays)
					if (inferredType != null && !inferredType.equals("Array") && isTypeIncompatible(elementType, inferredType))
						throw new CompileException("Type mismatch", "Expected " + elementType + ", got " + inferredType);

					variables.put(varName, new VariableInfo(isMutable, elementType, dimensions));
					String cppType = convertType(elementType);
					return cppType + " " + varName + dimensions + " = " + valueInfo.processedValue;
				}
			} else {
				// Simple type
				if (inferredType != null && isTypeIncompatible(typeDecl, inferredType))
					throw new CompileException("Type mismatch", "Expected " + typeDecl + ", got " + inferredType);
				variables.put(varName, new VariableInfo(isMutable, typeDecl, ""));
				String cppType = convertType(typeDecl);
				return cppType + " " + varName + " = " + valueInfo.processedValue;
			}
			else if (valueInfo.inferredType != null && valueInfo.inferredType.equals("I32") &&
							 valueInfo.processedValue.startsWith("{")) {
				// Infer array dimensions from value
				int elementCount = countArrayElements(valueInfo.processedValue);
				dimensions = "[" + elementCount + "]";
				variables.put(varName, new VariableInfo(isMutable, inferredType, dimensions));
				String cppType = convertType(inferredType);
				return cppType + " " + varName + dimensions + " = " + valueInfo.processedValue;
			} else {
				// Regular type inference
				variables.put(varName, new VariableInfo(isMutable, inferredType, dimensions));
				String cppType = convertType(inferredType);
				return cppType + " " + varName + dimensions + " = " + valueInfo.processedValue;
			}
		}

		// Handle array indexing assignment
		Pattern arrayAssignPattern = Pattern.compile("([a-zA-Z_][a-zA-Z0-9_]*)\\[(.+?)]\\s*=\\s*(.+)");
		Matcher arrayAssignMatcher = arrayAssignPattern.matcher(statement);

		if (arrayAssignMatcher.matches()) {
			String varName = arrayAssignMatcher.group(1);
			String index = arrayAssignMatcher.group(2);
			String value = arrayAssignMatcher.group(3);

			VariableInfo varInfo = variables.get(varName);
			if (varInfo == null) throw new CompileException("Undefined variable", varName);

			if (!varInfo.mutable) throw new CompileException("Cannot assign to immutable variable", varName);

			// Type check value
			ValueInfo valueInfo = processValue(value);
			if (valueInfo.inferredType != null && isTypeIncompatible(varInfo.type, valueInfo.inferredType))
				throw new CompileException("Type mismatch", "Cannot assign " + valueInfo.inferredType + " to " + varInfo.type);

			return varName + "[" + index + "] = " + processValue(value).processedValue;
		}

		// Handle regular assignment statements  
		Pattern assignmentPattern = Pattern.compile("([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*(.+)");
		Matcher assignmentMatcher = assignmentPattern.matcher(statement);

		if (assignmentMatcher.matches()) {
			String varName = assignmentMatcher.group(1);
			String value = assignmentMatcher.group(2);

			VariableInfo varInfo = variables.get(varName);
			if (varInfo == null) throw new CompileException("Undefined variable", varName);

			if (!varInfo.mutable) throw new CompileException("Cannot assign to immutable variable", varName);

			return varName + " = " + processValue(value).processedValue;
		}

		throw new CompileException("Invalid input", statement);
	}

	private static boolean isTypeIncompatible(String expected, String actual) {
		return !expected.equals(actual);
	}

	private static ValueInfo processValue(String value) {
		// Handle string literals for U8 arrays
		if (value.matches("\"[^\"]*\"")) {
			String str = value.substring(1, value.length() - 1);
			StringBuilder chars = new StringBuilder("{");
			for (int i = 0; i < str.length(); i++) {
				if (i > 0) chars.append(", ");
				chars.append("'").append(str.charAt(i)).append("'");
			}
			chars.append("}");
			return new ValueInfo(chars.toString(), "U8");
		}

		// Handle array literals
		if (value.startsWith("[") && value.endsWith("]")) {
			String content = value.substring(1, value.length() - 1).trim();
			if (content.isEmpty()) return new ValueInfo("{}", "Array");

			// Check for nested arrays
			if (content.contains("[")) {
				// 2D array
				StringBuilder result = new StringBuilder("{");
				String[] parts = splitArrayElements(content);
				String elementType = null;

				for (int i = 0; i < parts.length; i++) {
					if (i > 0) result.append(", ");
					ValueInfo subArray = processValue(parts[i].trim());
					result.append(subArray.processedValue);
					if (elementType == null && subArray.inferredType != null) elementType = subArray.inferredType;
				}
				result.append("}");
				return new ValueInfo(result.toString(), elementType);
			} else {
				// 1D array
				StringBuilder result = new StringBuilder("{");
				String[] elements = content.split(",\\s*");
				String elementType = null;

				for (int i = 0; i < elements.length; i++) {
					if (i > 0) result.append(", ");
					ValueInfo elem = processValue(elements[i].trim());
					result.append(elem.processedValue);
					if (elementType == null && elem.inferredType != null) elementType = elem.inferredType;
				}
				result.append("}");
				return new ValueInfo(result.toString(), elementType);
			}
		}

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

	private static String[] splitArrayElements(String content) {
		List<String> elements = new ArrayList<>();
		int level = 0;
		int start = 0;

		for (int i = 0; i < content.length(); i++) {
			char c = content.charAt(i);
			if (c == '[') level++;
			else if (c == ']') level--;
			else if (c == ',' && level == 0) {
				elements.add(content.substring(start, i));
				start = i + 1;
			}
		}
		elements.add(content.substring(start));

		return elements.toArray(new String[0]);
	}

	private static int countArrayElements(String arrayValue) {
		if (!arrayValue.startsWith("{") || !arrayValue.endsWith("}")) return 0;
		String content = arrayValue.substring(1, arrayValue.length() - 1).trim();
		if (content.isEmpty()) return 0;
		return content.split(",\\s*").length;
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
			case "USize":
				return "usize_t";
			default:
				return type;
		}
	}
}