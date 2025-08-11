package magma;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Compiler {
	private static final Pattern LET_WITH_SUFFIX_PATTERN =
			Pattern.compile("^let\\s+(mut\\s+)?(\\w+)(?:\\s*:\\s*(\\w+))?\\s*=\\s*(\\d+)([UI]\\d+);?$");
	private static final Pattern LET_ARRAY_PATTERN =
			Pattern.compile("^let\\s+(mut\\s+)?(\\w+)\\s*:\\s*\\[(\\w+);\\s*(\\d+)\\]\\s*=\\s*\\[([\\d\\s,]+)\\];?$");
	private static final Pattern LET_2D_ARRAY_PATTERN = Pattern.compile(
			"^let\\s+(mut\\s+)?(\\w+)\\s*:\\s*\\[(\\w+);\\s*(\\d+),\\s*(\\d+)\\]\\s*=\\s*(\\[\\[.*?\\]\\]);?$");
	private static final Pattern LET_STRING_PATTERN =
			Pattern.compile("^let\\s+(mut\\s+)?(\\w+)\\s*:\\s*\\[(\\w+);\\s*(\\d+)\\]\\s*=\\s*\"([^\"]*)\";?$");
	private static final Pattern LET_ARRAY_LITERAL_PATTERN =
			Pattern.compile("^let\\s+(mut\\s+)?(\\w+)\\s*=\\s*\\[([\\d\\s,]+)\\];?$");
	private static final Pattern LET_PATTERN =
			Pattern.compile("^let\\s+(mut\\s+)?(\\w+)(?:\\s*:\\s*(\\*?\\w+))?\\s*=\\s*('.'|&?\\*?[\\w\\d\\[\\]]+);?$");
	private static final Pattern ASSIGN_PATTERN = Pattern.compile("^(\\w+)\\s*=\\s*([\\w\\d]+);?$");
	private static final Pattern ARRAY_INDEX_ASSIGN_PATTERN = Pattern.compile("^(\\w+)\\[(\\d+)\\]\\s*=\\s*([\\w\\d]+);?$");

	private static final Map<String, String> TYPE_MAPPING = new HashMap<>();
	private static final Set<String> mutableVars = new HashSet<>();

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

		// Floating-point types
		TYPE_MAPPING.put("F32", "float");
		TYPE_MAPPING.put("F64", "double");
	}

	public static String compile(String input) throws CompileException {
		if (input.isEmpty()) return input;

		// Clear mutable variables for each compilation
		mutableVars.clear();

		return compileMultipleStatements(input.trim());
	}

	private static String compileMultipleStatements(String input) throws CompileException {
		String trimmed = input.trim();

		// Simple approach: split by semicolon and handle assignment after let patterns
		if (trimmed.matches("let\\s+mut\\s+\\w+\\s*=.*?;\\s*\\w+\\s*=.*")) {
			// Special case: mutable variable declaration followed by assignment
			int semiIndex = trimmed.indexOf(';');
			String firstStmt = trimmed.substring(0, semiIndex).trim();
			String secondStmt = trimmed.substring(semiIndex + 1).trim();
			if (secondStmt.endsWith(";")) secondStmt = secondStmt.substring(0, secondStmt.length() - 1).trim();

			String result1 = compileStatement(firstStmt);
			String result2 = compileStatement(secondStmt);
			return result1 + "; " + result2 + ";";
		}

		// Handle other multi-statement cases or single statements
		if (trimmed.matches(".*;\\s*let\\s+.*")) {
			// Multiple let statements
			String[] statements = trimmed.split(";\\s*(?=let\\s)");
			StringBuilder result = new StringBuilder();

			for (String stmt : statements) {
				String cleanStmt = stmt.trim();
				if (cleanStmt.endsWith(";")) cleanStmt = cleanStmt.substring(0, cleanStmt.length() - 1).trim();

				if (!cleanStmt.isEmpty()) {
					if (result.length() > 0) result.append(" ");
					result.append(compileStatement(cleanStmt)).append(";");
				}
			}
			return result.toString();
		}

		// Single statement
		String stmt = trimmed;
		if (stmt.endsWith(";")) stmt = stmt.substring(0, stmt.length() - 1).trim();
		return compileStatement(stmt) + ";";
	}

	private static String compileStatement(String stmt) throws CompileException {
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

	private static String compileLetStatement(Matcher matcher, boolean hasTypeSuffix) throws CompileException {
		String mutKeyword = matcher.group(1);
		String variableName = matcher.group(2);
		String declaredType = matcher.group(3);
		String value = matcher.group(4);
		String typeSuffix = hasTypeSuffix ? matcher.group(5) : null;

		if (mutKeyword != null) mutableVars.add(variableName);

		// Type inference for reference operations and array access
		if (declaredType == null && value.startsWith("&")) {
			// Infer pointer type from reference operation
			declaredType = "*I32"; // Default to *I32 for references
		} else if (declaredType == null && value.matches("\\w+\\[\\d+\\]")) {
			// Infer element type from array access - default to uint8_t for arrays
			declaredType = "U8";
		}

		String cType = resolveType(declaredType, typeSuffix);
		return cType + " " + variableName + " = " + value;
	}

	private static String compileArrayStatement(Matcher matcher) throws CompileException {
		String mutKeyword = matcher.group(1);
		String variableName = matcher.group(2);
		String elementType = matcher.group(3);
		String size = matcher.group(4);
		String elements = matcher.group(5);

		boolean isMutable = mutKeyword != null;
		if (isMutable) mutableVars.add(variableName);

		String cType = mapType(elementType);
		String cleanElements = elements.replaceAll("\\s+", " ").trim();

		return cType + " " + variableName + "[" + size + "] = { " + cleanElements + " }";
	}

	private static String compileSpecialArrayStatement(Matcher matcher, boolean is2DArray) throws CompileException {
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

	private static String compileArrayLiteralStatement(Matcher matcher) throws CompileException {
		String mutKeyword = matcher.group(1);
		String variableName = matcher.group(2);
		String elements = matcher.group(3);

		if (mutKeyword != null) mutableVars.add(variableName);

		// Count elements and infer type as uint8_t for integer literals
		String[] elementArray = elements.split(",");
		int size = elementArray.length;
		String cleanElements = elements.replaceAll("\\s+", " ").trim();
		
		return "uint8_t " + variableName + "[" + size + "] = { " + cleanElements + " }";
	}

	private static String compileAssignStatement(Matcher matcher, boolean isArrayIndex) throws CompileException {
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

	private static String resolveType(String declaredType, String typeSuffix) throws CompileException {
		if (typeSuffix != null && declaredType != null) {
			if (!typeSuffix.equals(declaredType)) throw new CompileException(
					"Type conflict: declared type " + declaredType + " does not match suffix type " + typeSuffix);
			return mapType(typeSuffix);
		}

		if (typeSuffix != null) return mapType(typeSuffix);
		if (declaredType != null) return mapType(declaredType);
		return "int32_t";
	}

	private static String mapType(String type) throws CompileException {
		if (type == null) return "int32_t";
		
		// Handle pointer types like *I32
		if (type.startsWith("*")) {
			String baseType = type.substring(1);
			String cType = TYPE_MAPPING.get(baseType);
			if (cType == null) throw new CompileException("Unsupported type: " + baseType);
			return cType + "*";
		}
		
		String cType = TYPE_MAPPING.get(type);
		if (cType == null) throw new CompileException("Unsupported type: " + type);
		return cType;
	}
}
