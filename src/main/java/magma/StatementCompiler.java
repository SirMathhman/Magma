package magma;

import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class StatementCompiler {
	private static final Pattern LET_WITH_SUFFIX_PATTERN =
			Pattern.compile("^let\\s+(mut\\s+)?(\\w+)(?:\\s*:\\s*(\\w+))?\\s*=\\s*(\\d+)([UI]\\d+);?$");
	private static final Pattern LET_ARRAY_PATTERN =
			Pattern.compile("^let\\s+(mut\\s+)?(\\w+)\\s*:\\s*\\[(\\w+);\\s*(\\d+)]\\s*=\\s*\\[([\\d\\s,]+)];?$");
	private static final Pattern LET_2D_ARRAY_PATTERN =
			Pattern.compile("^let\\s+(mut\\s+)?(\\w+)\\s*:\\s*\\[(\\w+);\\s*(\\d+),\\s*(\\d+)]\\s*=\\s*(\\[\\[.*?]]);?$");
	private static final Pattern LET_STRING_PATTERN =
			Pattern.compile("^let\\s+(mut\\s+)?(\\w+)\\s*:\\s*\\[(\\w+);\\s*(\\d+)]\\s*=\\s*\"([^\"]*)\";?$");
	private static final Pattern LET_ARRAY_LITERAL_PATTERN =
			Pattern.compile("^let\\s+(mut\\s+)?(\\w+)\\s*=\\s*\\[([\\d\\s,]+)];?$");
	private static final Pattern LET_PATTERN =
			Pattern.compile("^let\\s+(mut\\s+)?(\\w+)(?:\\s*:\\s*(\\*?\\w+))?\\s*=\\s*('.'|&?\\*?[\\w\\d\\[\\]]+);?$");
	private static final Pattern ASSIGN_PATTERN = Pattern.compile("^(\\w+)\\s*=\\s*([\\w\\d]+);?$");
	private static final Pattern ARRAY_INDEX_ASSIGN_PATTERN = Pattern.compile("^(\\w+)\\[(\\d+)]\\s*=\\s*([\\w\\d]+);?$");

	private final Map<String, String> typeMapping;
	private final Set<String> mutableVars;

	public StatementCompiler(Map<String, String> typeMapping, Set<String> mutableVars) {
		this.typeMapping = typeMapping;
		this.mutableVars = mutableVars;
	}

	public String parseMultiple(String input) throws CompileException {
		// Simple bracket-aware parsing for arrays
		StringBuilder result = new StringBuilder();
		String[] parts = smartSplit(input);

		for (String part : parts) {
			String stmt = part.trim();
			if (!stmt.isEmpty()) {
				if (result.length() > 0) result.append(" ");
				if (stmt.startsWith("{") && stmt.endsWith("}")) result.append(stmt);
				else result.append(compileStatement(stmt)).append(";");
			}
		}

		return result.toString();
	}

	private String[] smartSplit(String input) {
		StringBuilder current = new StringBuilder();
		java.util.List<String> parts = new java.util.ArrayList<>();
		int bracketDepth = 0;
		int braceDepth = 0;

		for (char c : input.toCharArray()) {
			if (c == '[') bracketDepth++;
			else if (c == ']') bracketDepth--;
			else if (c == '{') braceDepth++;
			else if (c == '}') braceDepth--;
			else if (c == ';' && bracketDepth == 0 && braceDepth == 0) {
				parts.add(current.toString());
				current.setLength(0);
				continue;
			}
			current.append(c);
		}

		if (current.length() > 0) parts.add(current.toString());

		return parts.toArray(new String[0]);
	}


	public String compileStatement(String stmt) throws CompileException {
		// Handle empty statements
		if (stmt.trim().isEmpty()) return "";

		// Try let statement with type suffix first
		Matcher letWithSuffixMatcher = LET_WITH_SUFFIX_PATTERN.matcher(stmt);
		if (letWithSuffixMatcher.matches()) return compileLetStatement(letWithSuffixMatcher, true);

		// Try 2D array declaration
		Matcher array2DMatcher = LET_2D_ARRAY_PATTERN.matcher(stmt);
		if (array2DMatcher.matches()) return compileSpecialArrayStatement(array2DMatcher, true);

		// Try string literal declaration
		Matcher stringMatcher = LET_STRING_PATTERN.matcher(stmt);
		if (stringMatcher.matches()) return compileSpecialArrayStatement(stringMatcher, false);

		// Try array literal (no explicit type)
		Matcher arrayLiteralMatcher = LET_ARRAY_LITERAL_PATTERN.matcher(stmt);
		if (arrayLiteralMatcher.matches()) return compileArrayLiteralStatement(arrayLiteralMatcher);

		// Try array declaration
		Matcher arrayMatcher = LET_ARRAY_PATTERN.matcher(stmt);
		if (arrayMatcher.matches()) return compileArrayStatement(arrayMatcher);

		// Try array index assignment
		Matcher arrayIndexAssignMatcher = ARRAY_INDEX_ASSIGN_PATTERN.matcher(stmt);
		if (arrayIndexAssignMatcher.matches()) return compileAssignStatement(arrayIndexAssignMatcher, true);

		// Try regular assignment statement
		Matcher assignMatcher = ASSIGN_PATTERN.matcher(stmt);
		if (assignMatcher.matches()) return compileAssignStatement(assignMatcher, false);

		// Try regular let statement
		Matcher letMatcher = LET_PATTERN.matcher(stmt);
		if (letMatcher.matches()) return compileLetStatement(letMatcher, false);

		throw new CompileException("Invalid input: " + stmt);
	}

	private String compileLetStatement(Matcher matcher, boolean hasTypeSuffix) throws CompileException {
		String mutKeyword = matcher.group(1);
		String variableName = matcher.group(2);
		String declaredType = matcher.group(3);
		String value = matcher.group(4);
		String typeSuffix = hasTypeSuffix ? matcher.group(5) : null;

		if (mutKeyword != null) mutableVars.add(variableName);

		// Type inference for reference operations and array access
		if (declaredType == null && value.startsWith("&")) declaredType = "*I32"; // Default to *I32 for references
		else if (declaredType == null && value.matches("\\w+\\[\\d+]"))
			declaredType = "U8"; // Infer element type from array access

		String cType = resolveType(declaredType, typeSuffix);
		return cType + " " + variableName + " = " + value;
	}

	private String compileArrayStatement(Matcher matcher) throws CompileException {
		String mutKeyword = matcher.group(1);
		String variableName = matcher.group(2);
		String elementType = matcher.group(3);
		String size = matcher.group(4);
		String elements = matcher.group(5);

		if (mutKeyword != null) mutableVars.add(variableName);

		String cType = mapType(elementType);
		String cleanElements = elements.replaceAll("\\s+", " ").trim();

		return cType + " " + variableName + "[" + size + "] = { " + cleanElements + " }";
	}

	private String compileSpecialArrayStatement(Matcher matcher, boolean is2DArray) throws CompileException {
		String mutKeyword = matcher.group(1);
		String variableName = matcher.group(2);
		String elementType = matcher.group(3);

		if (mutKeyword != null) mutableVars.add(variableName);
		String cType = mapType(elementType);

		if (is2DArray) {
			String rows = matcher.group(4);
			String cols = matcher.group(5);
			String elements = matcher.group(6);
			String processedElements = elements.replace("[", "{ ").replace("]", " }").replaceAll("\\s+", " ").trim();
			return cType + " " + variableName + "[" + rows + "][" + cols + "] = " + processedElements;
		} else {
			String size = matcher.group(4);
			String value = matcher.group(5);
			return cType + " " + variableName + "[" + size + "] = \"" + value + "\"";
		}
	}

	private String compileArrayLiteralStatement(Matcher matcher) {
		String mutKeyword = matcher.group(1);
		String variableName = matcher.group(2);
		String elements = matcher.group(3);

		if (mutKeyword != null) mutableVars.add(variableName);

		String[] elementArray = elements.split(",");
		int size = elementArray.length;
		String cleanElements = elements.replaceAll("\\s+", " ").trim();

		return "uint8_t " + variableName + "[" + size + "] = { " + cleanElements + " }";
	}

	private String compileAssignStatement(Matcher matcher, boolean isArrayIndex) throws CompileException {
		if (isArrayIndex) {
			String variableName = matcher.group(1);
			String index = matcher.group(2);
			String value = matcher.group(3);
			return variableName + "[" + index + "] = " + value;
		} else {
			String variableName = matcher.group(1);
			String value = matcher.group(2);

			if (!mutableVars.contains(variableName))
				throw new CompileException("Cannot assign to immutable variable: " + variableName);

			return variableName + " = " + value;
		}
	}

	private String resolveType(String declaredType, String typeSuffix) throws CompileException {
		if (typeSuffix != null && declaredType != null) {
			if (!typeSuffix.equals(declaredType)) throw new CompileException(
					"Type conflict: declared type " + declaredType + " does not match suffix type " + typeSuffix);
			return mapType(typeSuffix);
		}

		if (typeSuffix != null) return mapType(typeSuffix);
		if (declaredType != null) return mapType(declaredType);
		return "int32_t";
	}

	private String mapType(String type) throws CompileException {
		if (type == null) return "int32_t";

		// Handle pointer types like *I32
		if (type.startsWith("*")) {
			String baseType = type.substring(1);
			String cType = typeMapping.get(baseType);
			if (cType == null) throw new CompileException("Unsupported type: " + baseType);
			return cType + "*";
		}

		String cType = typeMapping.get(type);
		if (cType == null) throw new CompileException("Unsupported type: " + type);
		return cType;
	}
}