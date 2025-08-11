package magma;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Compiler {
	private static final Pattern BLOCK_PATTERN = Pattern.compile("^\\{(.*)}$", Pattern.DOTALL);
	private static final Pattern IF_PATTERN =
			Pattern.compile("^if\\s*\\(([^)]+)\\)\\s*\\{([^}]*)}(?:\\s*else\\s*\\{([^}]*)})?", Pattern.DOTALL);
	private static final Pattern WHILE_PATTERN =
			Pattern.compile("^while\\s*\\(([^)]+)\\)\\s*\\{([^}]*)}$", Pattern.DOTALL);
	private static final Pattern STRUCT_PATTERN = Pattern.compile("^struct\\s+(\\w+)\\s*\\{([^}]*)}$", Pattern.DOTALL);
	private static final Pattern FUNCTION_PATTERN =
			Pattern.compile("^fn\\s+(\\w+)\\s*\\(([^)]*)\\)(?:\\s*:\\s*(\\w+))?\\s*=>\\s*\\{([^}]*)}$", Pattern.DOTALL);

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

		// Void type
		TYPE_MAPPING.put("Void", "void");
	}

	public static String compile(String input) throws CompileException {
		if (input.isEmpty()) return input;

		// Clear mutable variables for each compilation
		mutableVars.clear();

		return compileCode(input.trim());
	}

	private static String compileCode(String input) throws CompileException {
		String trimmed = input.trim();

		// Try control flow first
		Matcher ifMatcher = IF_PATTERN.matcher(trimmed);
		if (ifMatcher.matches()) return compileIfStatement(ifMatcher);

		Matcher whileMatcher = WHILE_PATTERN.matcher(trimmed);
		if (whileMatcher.matches()) return compileWhileStatement(whileMatcher);

		// Try struct
		Matcher structMatcher = STRUCT_PATTERN.matcher(trimmed);
		if (structMatcher.matches()) return compileStructStatement(structMatcher);

		// Try function
		Matcher functionMatcher = FUNCTION_PATTERN.matcher(trimmed);
		if (functionMatcher.matches()) return compileFunctionStatement(functionMatcher);

		// Try block
		Matcher blockMatcher = BLOCK_PATTERN.matcher(trimmed);
		if (blockMatcher.matches()) return compileBlockStatement(blockMatcher);

		// Fall back to statement-based compilation
		return compileStatements(trimmed);
	}

	private static String compileStatements(String input) throws CompileException {
		String trimmed = input.trim();

		// Handle multiple statements by splitting on semicolons intelligently
		if (trimmed.contains(";")) return parseMultipleStatements(trimmed);

		// Single statement
		String stmt = trimmed;
		if (stmt.endsWith(";")) stmt = stmt.substring(0, stmt.length() - 1).trim();
		String compiled = compileStatement(stmt);
		return compiled.isEmpty() ? compiled : compiled + ";";
	}

	private static String parseMultipleStatements(String input) throws CompileException {
		StatementCompiler statementCompiler = new StatementCompiler(TYPE_MAPPING, mutableVars);
		return statementCompiler.parseMultiple(input);
	}

	private static String compileStatement(String stmt) throws CompileException {
		StatementCompiler statementCompiler = new StatementCompiler(TYPE_MAPPING, mutableVars);
		return statementCompiler.compileStatement(stmt);
	}

	private static String compileBlockStatement(Matcher matcher) throws CompileException {
		String content = matcher.group(1).trim();
		if (content.isEmpty()) return "{}";
		return "{" + compileCode(content) + "}";
	}

	private static String compileIfStatement(Matcher matcher) throws CompileException {
		String condition = matcher.group(1);
		String thenBlock = matcher.group(2);
		String elseBlock = matcher.group(3);

		String result = "if(" + condition + "){" + compileCode(thenBlock) + "}";
		if (elseBlock != null) result += " else {" + compileCode(elseBlock) + "}";
		return result;
	}

	private static String compileWhileStatement(Matcher matcher) throws CompileException {
		String condition = matcher.group(1);
		String body = matcher.group(2);
		return "while(" + condition + "){" + compileCode(body) + "}";
	}

	private static String compileStructStatement(Matcher matcher) throws CompileException {
		String structName = matcher.group(1);
		String fields = matcher.group(2).trim();

		if (fields.isEmpty()) return "struct " + structName + " {};";

		// Parse fields - format "x : I32" becomes "int32_t x;"
		StringBuilder result = new StringBuilder("struct " + structName + " {");
		String[] fieldArray = fields.split(",");

		for (String field : fieldArray) {
			String trimmedField = field.trim();
			if (!trimmedField.isEmpty()) {
				String[] parts = trimmedField.split("\\s*:\\s*");
				if (parts.length == 2) {
					String fieldName = parts[0].trim();
					String fieldType = parts[1].trim();
					String cType = TYPE_MAPPING.get(fieldType);
					if (cType == null) throw new CompileException("Unsupported type: " + fieldType);
					result.append(cType).append(" ").append(fieldName).append(";");
				}
			}
		}
		result.append("};");
		return result.toString();
	}

	private static String compileFunctionStatement(Matcher matcher) throws CompileException {
		String functionName = matcher.group(1);
		String params = matcher.group(2);
		String returnType = matcher.group(3);
		String body = matcher.group(4);

		// Default return type is void
		String cReturnType;
		if (returnType != null) {
			cReturnType = TYPE_MAPPING.get(returnType);
			if (cReturnType == null) throw new CompileException("Unsupported type: " + returnType);
		} else cReturnType = "void";

		// Parse parameters - format "value : I32" becomes "int32_t value"
		StringBuilder paramList = new StringBuilder();
		if (params != null && !params.trim().isEmpty()) {
			String[] paramArray = params.split(",");
			for (int i = 0; i < paramArray.length; i++) {
				String param = paramArray[i].trim();
				if (!param.isEmpty()) {
					String[] parts = param.split("\\s*:\\s*");
					if (parts.length == 2) {
						String paramName = parts[0].trim();
						String paramType = parts[1].trim();
						String cType = TYPE_MAPPING.get(paramType);
						if (cType == null) throw new CompileException("Unsupported type: " + paramType);
						if (i > 0) paramList.append(", ");
						paramList.append(cType).append(" ").append(paramName);
					}
				}
			}
		}

		return cReturnType + " " + functionName + "(" + paramList + "){" + compileCode(body) + "}";
	}

}