package magma;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class PatternMatcher {
	private static final Pattern IF_PATTERN =
			Pattern.compile("^if\\s*\\(([^)]+)\\)\\s*\\{([^}]*)}(?:\\s*else\\s*\\{([^}]*)})?", Pattern.DOTALL);
	private static final Pattern WHILE_PATTERN =
			Pattern.compile("^while\\s*\\(([^)]+)\\)\\s*\\{([^}]*)}$", Pattern.DOTALL);
	private static final Pattern STRUCT_PATTERN = Pattern.compile("^struct\\s+(\\w+)\\s*\\{([^}]*)}$", Pattern.DOTALL);
	private static final Pattern GENERIC_STRUCT_PATTERN = Pattern.compile("^struct\\s+(\\w+)<([^>]+)>\\s*\\{([^}]*)}$", Pattern.DOTALL);

	static String tryCompileControlFlowStatements(String trimmed, Map<String, String> typeMapping) throws CompileException {
		Matcher ifMatcher = IF_PATTERN.matcher(trimmed);
		if (ifMatcher.matches()) return CompilerUtils.compileIfStatement(ifMatcher);

		Matcher whileMatcher = WHILE_PATTERN.matcher(trimmed);
		if (whileMatcher.matches()) return CompilerUtils.compileWhileStatement(whileMatcher);

		return null;
	}

	static String tryCompileStructStatements(String trimmed, Map<String, String> typeMapping) throws CompileException {
		Matcher structMatcher = STRUCT_PATTERN.matcher(trimmed);
		if (structMatcher.matches()) return CompilerUtils.compileStructStatement(structMatcher, typeMapping);

		Matcher genericStructMatcher = GENERIC_STRUCT_PATTERN.matcher(trimmed);
		if (genericStructMatcher.matches()) return CompilerUtils.compileGenericStructStatement(genericStructMatcher, typeMapping);

		return null;
	}
}