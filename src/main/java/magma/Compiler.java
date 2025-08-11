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

		return compileCode(input.trim());
	}

	private static String compileCode(String input) throws CompileException {
		String trimmed = input.trim();

		// Try control flow first
		Matcher ifMatcher = IF_PATTERN.matcher(trimmed);
		if (ifMatcher.matches()) return compileIfStatement(ifMatcher);

		Matcher whileMatcher = WHILE_PATTERN.matcher(trimmed);
		if (whileMatcher.matches()) return compileWhileStatement(whileMatcher);

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
}