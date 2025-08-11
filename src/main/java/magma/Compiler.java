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
	private static final Pattern GENERIC_STRUCT_PATTERN = Pattern.compile("^struct\\s+(\\w+)<([^>]+)>\\s*\\{([^}]*)}$", Pattern.DOTALL);
	private static final Pattern FUNCTION_PATTERN =
			Pattern.compile("^fn\\s+(\\w+)\\s*\\(([^)]*)\\)(?:\\s*:\\s*(\\w+))?\\s*=>\\s*\\{(.*)}$", Pattern.DOTALL);
	private static final Pattern GENERIC_FUNCTION_PATTERN =
			Pattern.compile("^fn\\s+(\\w+)<([^>]+)>\\s*\\(([^)]*)\\)(?:\\s*:\\s*(\\w+))?\\s*=>\\s*\\{(.*)}$", Pattern.DOTALL);
	private static final Pattern CLASS_PATTERN =
			Pattern.compile("^class\\s+fn\\s+(\\w+)\\s*\\(([^)]*)\\)\\s*=>\\s*\\{(.*)}$", Pattern.DOTALL);
	private static final Pattern GENERIC_CLASS_PATTERN =
			Pattern.compile("^class\\s+fn\\s+(\\w+)<([^>]+)>\\s*\\(([^)]*)\\)\\s*=>\\s*\\{(.*)}$", Pattern.DOTALL);
	private static final Pattern VARIADIC_FUNCTION_PATTERN =
			Pattern.compile("^fn\\s+(\\w+)<([^>]+)>\\s*\\(\\.\\.\\.(\\w+)\\s*:\\s*\\[([^;]+);\\s*([^\\]]+)\\]\\)(?:\\s*:\\s*(\\w+))?\\s*=>\\s*\\{(.*)}$", Pattern.DOTALL);

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

	static String compileCode(String input) throws CompileException {
		String trimmed = input.trim();

		// Try control flow statements first
		String result = PatternMatcher.tryCompileControlFlowStatements(trimmed, TYPE_MAPPING);
		if (result != null) return result;

		// Try struct statements  
		result = PatternMatcher.tryCompileStructStatements(trimmed, TYPE_MAPPING);
		if (result != null) return result;

		result = tryCompileClassAndFunctionStatements(trimmed);
		if (result != null) return result;

		Matcher blockMatcher = BLOCK_PATTERN.matcher(trimmed);
		if (blockMatcher.matches()) return CompilerUtils.compileBlockStatement(blockMatcher);

		// Handle multiple top-level constructs (functions, structs, variables)
		return compileMultipleConstructs(trimmed);
	}

	private static String tryCompileClassAndFunctionStatements(String trimmed) throws CompileException {
		Matcher genericClassMatcher = GENERIC_CLASS_PATTERN.matcher(trimmed);
		if (genericClassMatcher.matches()) return CompilerUtils.compileGenericClassStatement(genericClassMatcher, TYPE_MAPPING);

		Matcher classMatcher = CLASS_PATTERN.matcher(trimmed);
		if (classMatcher.matches()) return CompilerUtils.compileClassStatement(classMatcher, TYPE_MAPPING);

		Matcher functionMatcher = FUNCTION_PATTERN.matcher(trimmed);
		if (functionMatcher.matches()) return CompilerUtils.compileFunctionStatement(functionMatcher, TYPE_MAPPING);

		Matcher genericFunctionMatcher = GENERIC_FUNCTION_PATTERN.matcher(trimmed);
		if (genericFunctionMatcher.matches()) return CompilerUtils.compileGenericFunctionStatement(genericFunctionMatcher, TYPE_MAPPING);

		Matcher variadicFunctionMatcher = VARIADIC_FUNCTION_PATTERN.matcher(trimmed);
		if (variadicFunctionMatcher.matches()) return CompilerUtils.compileVariadicFunctionStatement(variadicFunctionMatcher, TYPE_MAPPING);

		return null;
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


	private static String compileMultipleConstructs(String input) throws CompileException {
		MultipleConstructsParser parser = new MultipleConstructsParser();
		return parser.parse(input);
	}
	
	static String compileConstruct(String construct) throws CompileException {
		String trimmed = construct.trim();
		
		// Try individual construct patterns first
		Matcher structMatcher = STRUCT_PATTERN.matcher(trimmed);
		if (structMatcher.matches()) return CompilerUtils.compileStructStatement(structMatcher, TYPE_MAPPING);
		
		Matcher genericStructMatcher = GENERIC_STRUCT_PATTERN.matcher(trimmed);
		if (genericStructMatcher.matches()) return CompilerUtils.compileGenericStructStatement(genericStructMatcher, TYPE_MAPPING);
		
		Matcher genericClassMatcher = GENERIC_CLASS_PATTERN.matcher(trimmed);
		if (genericClassMatcher.matches()) return CompilerUtils.compileGenericClassStatement(genericClassMatcher, TYPE_MAPPING);
		
		Matcher classMatcher = CLASS_PATTERN.matcher(trimmed);
		if (classMatcher.matches()) return CompilerUtils.compileClassStatement(classMatcher, TYPE_MAPPING);
		
		Matcher functionMatcher = FUNCTION_PATTERN.matcher(trimmed);
		if (functionMatcher.matches()) return CompilerUtils.compileFunctionStatement(functionMatcher, TYPE_MAPPING);
		
		Matcher genericFunctionMatcher = GENERIC_FUNCTION_PATTERN.matcher(trimmed);
		if (genericFunctionMatcher.matches()) return CompilerUtils.compileGenericFunctionStatement(genericFunctionMatcher, TYPE_MAPPING);
		
		Matcher variadicFunctionMatcher = VARIADIC_FUNCTION_PATTERN.matcher(trimmed);
		if (variadicFunctionMatcher.matches()) return CompilerUtils.compileVariadicFunctionStatement(variadicFunctionMatcher, TYPE_MAPPING);
		
		// Try as statement
		return compileStatements(trimmed);
	}

}