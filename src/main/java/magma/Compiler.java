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
	private static final Pattern LET_PATTERN =
			Pattern.compile("^let\\s+(mut\\s+)?(\\w+)(?:\\s*:\\s*(\\w+))?\\s*=\\s*([\\w\\d]+);?$");
	private static final Pattern ASSIGN_PATTERN = Pattern.compile("^(\\w+)\\s*=\\s*([\\w\\d]+);?$");

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
	}

	public static String compile(String input) throws CompileException {
		if (input.isEmpty()) return input;

		// Clear mutable variables for each compilation
		mutableVars.clear();

		// Split by semicolons and process each statement
		String[] statements = input.split(";");
		StringBuilder result = new StringBuilder();

		for (int i = 0; i < statements.length; i++) {
			String stmt = statements[i].trim();
			if (!stmt.isEmpty()) {
				if (result.length() > 0) result.append(" ");
				result.append(compileStatement(stmt)).append(";");
			}
		}

		return result.toString();
	}

	private static String compileStatement(String stmt) throws CompileException {
		// Try let statement with type suffix first
		Matcher letWithSuffixMatcher = LET_WITH_SUFFIX_PATTERN.matcher(stmt);
		if (letWithSuffixMatcher.matches()) return compileLetStatementWithSuffix(letWithSuffixMatcher);

		// Try regular let statement
		Matcher letMatcher = LET_PATTERN.matcher(stmt);
		if (letMatcher.matches()) return compileLetStatement(letMatcher);

		// Try assignment statement
		Matcher assignMatcher = ASSIGN_PATTERN.matcher(stmt);
		if (assignMatcher.matches()) return compileAssignStatement(assignMatcher);

		throw new CompileException("Invalid input: " + stmt);
	}

	private static String compileLetStatementWithSuffix(Matcher matcher) throws CompileException {
		String mutKeyword = matcher.group(1);
		String variableName = matcher.group(2);
		String declaredType = matcher.group(3);
		String value = matcher.group(4);
		String typeSuffix = matcher.group(5);

		boolean isMutable = mutKeyword != null;
		if (isMutable) mutableVars.add(variableName);

		String cType = resolveType(declaredType, typeSuffix);
		return cType + " " + variableName + " = " + value;
	}

	private static String compileLetStatement(Matcher matcher) throws CompileException {
		String mutKeyword = matcher.group(1);
		String variableName = matcher.group(2);
		String declaredType = matcher.group(3);
		String value = matcher.group(4);

		boolean isMutable = mutKeyword != null;
		if (isMutable) mutableVars.add(variableName);

		String cType = resolveType(declaredType, null);
		return cType + " " + variableName + " = " + value;
	}

	private static String compileAssignStatement(Matcher matcher) throws CompileException {
		String variableName = matcher.group(1);
		String value = matcher.group(2);

		if (!mutableVars.contains(variableName))
			throw new CompileException("Cannot assign to immutable variable: " + variableName);

		return variableName + " = " + value;
	}

	private static String resolveType(String declaredType, String typeSuffix) throws CompileException {
		if (typeSuffix != null && declaredType != null) {
			validateTypeConsistency(declaredType, typeSuffix);
			return mapType(typeSuffix);
		}

		if (typeSuffix != null) return mapType(typeSuffix);

		if (declaredType != null) return mapType(declaredType);

		return "int32_t";
	}

	private static void validateTypeConsistency(String declaredType, String typeSuffix) throws CompileException {
		if (!typeSuffix.equals(declaredType)) throw new CompileException(
				"Type conflict: declared type " + declaredType + " does not match suffix type " + typeSuffix);
	}

	private static String mapType(String type) throws CompileException {
		String cType = TYPE_MAPPING.get(type);
		if (cType == null) throw new CompileException("Unsupported type: " + type);
		return cType;
	}
}
