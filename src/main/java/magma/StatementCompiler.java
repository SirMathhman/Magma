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
			Pattern.compile("^let\\s+(mut\\s+)?(\\w+)(?:\\s*:\\s*(\\*?\\w+))?\\s*=\\s*('.'|&?\\*?[\\w\\d\\[\\]()]+);?$");
	private static final Pattern ASSIGN_PATTERN = Pattern.compile("^(\\w+)\\s*=\\s*([\\w\\d]+);?$");
	private static final Pattern ARRAY_INDEX_ASSIGN_PATTERN = Pattern.compile("^(\\w+)\\[(\\d+)]\\s*=\\s*([\\w\\d]+);?$");
	private static final Pattern FUNCTION_CALL_PATTERN = Pattern.compile("^(\\w+)\\s*\\(\\s*\\);?$");
	private static final Pattern CONSTRUCTOR_CALL_PATTERN =
			Pattern.compile("^let\\s+(\\w+)\\s*:\\s*(\\w+)\\s*=\\s*(\\w+)\\s*\\{\\s*\\};?$");
	private static final Pattern GENERIC_CONSTRUCTOR_CALL_PATTERN =
			Pattern.compile("^let\\s+(\\w+)\\s*:\\s*(\\w+)<([^>]+)>\\s*=\\s*(\\w+)<([^>]+)>\\s*\\{\\s*([^}]*)\\s*\\};?$");
	private static final Pattern RETURN_PATTERN = Pattern.compile("^return\\s+(.+);?$");
	private static final Pattern GENERIC_FUNCTION_CALL_PATTERN = Pattern.compile("^let\\s+(\\w+)\\s*=\\s*(\\w+)\\s*\\(([^)]*)\\);?$");
	private final StatementCompilerUtils.StatementContext context;

	public StatementCompiler(Map<String, String> typeMapping, Set<String> mutableVars) {
		this.context = new StatementCompilerUtils.StatementContext(mutableVars, typeMapping);
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
		if (stmt.trim().isEmpty()) return "";

		String result = tryCompileVariableStatements(stmt);
		if (result != null) return result;

		result = tryCompileAssignmentStatements(stmt);
		if (result != null) return result;

		result = tryCompileCallStatements(stmt);
		if (result != null) return result;

		throw new CompileException("Invalid input: " + stmt);
	}

	private String tryCompileVariableStatements(String stmt) throws CompileException {
		// Check for generic function calls first, before general LET_PATTERN
		Matcher genericFunctionCallMatcher = GENERIC_FUNCTION_CALL_PATTERN.matcher(stmt);
		if (genericFunctionCallMatcher.matches()) {
			String result = compileGenericFunctionCallStatement(genericFunctionCallMatcher);
			if (result != null) {
				return result;
			}
			// If null, continue with other patterns
		}
		
		Matcher letWithSuffixMatcher = LET_WITH_SUFFIX_PATTERN.matcher(stmt);
		if (letWithSuffixMatcher.matches()) return compileLetStatement(letWithSuffixMatcher, true);

		Matcher array2DMatcher = LET_2D_ARRAY_PATTERN.matcher(stmt);
		if (array2DMatcher.matches()) return StatementCompilerUtils.compileSpecialArrayStatement(array2DMatcher,
																																														 new StatementCompilerUtils.SpecialArrayContext(
																																																 context, true));

		Matcher stringMatcher = LET_STRING_PATTERN.matcher(stmt);
		if (stringMatcher.matches()) return StatementCompilerUtils.compileSpecialArrayStatement(stringMatcher,
																																														new StatementCompilerUtils.SpecialArrayContext(
																																																context, false));

		Matcher arrayLiteralMatcher = LET_ARRAY_LITERAL_PATTERN.matcher(stmt);
		if (arrayLiteralMatcher.matches())
			return StatementCompilerUtils.compileArrayLiteralStatement(arrayLiteralMatcher, context);

		Matcher arrayMatcher = LET_ARRAY_PATTERN.matcher(stmt);
		if (arrayMatcher.matches()) return StatementCompilerUtils.compileArrayStatement(arrayMatcher, context);

		Matcher letMatcher = LET_PATTERN.matcher(stmt);
		if (letMatcher.matches()) return compileLetStatement(letMatcher, false);

		return null;
	}

	private String tryCompileAssignmentStatements(String stmt) throws CompileException {
		Matcher arrayIndexAssignMatcher = ARRAY_INDEX_ASSIGN_PATTERN.matcher(stmt);
		if (arrayIndexAssignMatcher.matches()) return StatementCompilerUtils.compileAssignStatement(arrayIndexAssignMatcher,
																																																new StatementCompilerUtils.AssignContext(
																																																		context, true));

		Matcher assignMatcher = ASSIGN_PATTERN.matcher(stmt);
		if (assignMatcher.matches()) return StatementCompilerUtils.compileAssignStatement(assignMatcher,
																																											new StatementCompilerUtils.AssignContext(
																																													context, false));

		return null;
	}

	private String tryCompileCallStatements(String stmt) throws CompileException {
		Matcher constructorMatcher = CONSTRUCTOR_CALL_PATTERN.matcher(stmt);
		if (constructorMatcher.matches())
			return StatementCompilerUtils.compileConstructorStatement(constructorMatcher, context);

		Matcher genericConstructorMatcher = GENERIC_CONSTRUCTOR_CALL_PATTERN.matcher(stmt);
		if (genericConstructorMatcher.matches())
			return StatementCompilerUtils.compileGenericConstructorStatement(genericConstructorMatcher, context);

		Matcher functionCallMatcher = FUNCTION_CALL_PATTERN.matcher(stmt);
		if (functionCallMatcher.matches()) return StatementCompilerUtils.compileFunctionCallStatement(functionCallMatcher);

		Matcher returnMatcher = RETURN_PATTERN.matcher(stmt);
		if (returnMatcher.matches()) return compileReturnStatement(returnMatcher);

		return null;
	}

	private String compileLetStatement(Matcher matcher, boolean hasTypeSuffix) throws CompileException {
		String mutKeyword = matcher.group(1);
		String variableName = matcher.group(2);
		String declaredType = matcher.group(3);
		String value = matcher.group(4);
		String typeSuffix = hasTypeSuffix ? matcher.group(5) : null;

		if (mutKeyword != null) context.mutableVars.add(variableName);

		// Type inference for reference operations and array access
		if (declaredType == null && value.startsWith("&")) declaredType = "*I32"; // Default to *I32 for references
		else if (declaredType == null && value.matches("\\w+\\[\\d+]"))
			declaredType = "U8"; // Infer element type from array access

		String cType = StatementCompilerUtils.resolveType(new StatementCompilerUtils.TypeResolutionParams(
				new StatementCompilerUtils.TypeInput(new StatementCompilerUtils.TypeData(declaredType, typeSuffix), context)));
		return cType + " " + variableName + " = " + value;
	}

	private String compileReturnStatement(Matcher matcher) {
		String returnValue = matcher.group(1);
		return "return " + returnValue;
	}

	private String compileGenericFunctionCallStatement(Matcher matcher) throws CompileException {
		String variableName = matcher.group(1);
		String functionName = matcher.group(2);
		String args = matcher.group(3);
		
		// Check if this is a call to a generic function and infer the type
		String inferredType = inferTypeFromArgs(args);
		if (inferredType != null) {
			// Try to monomorphize the generic function
			try {
				String monomorphizedFunction = GenericRegistry.monomorphizeFunction(functionName, inferredType);
				// Return the monomorphized function definition + the variable assignment
				return monomorphizedFunction + " int32_t " + variableName + " = " + functionName + "(" + args + ")";
			} catch (CompileException e) {
				// Not a generic function, return null to let other patterns handle this
				return null;
			}
		}
		
		// Not a generic function call, return null to let other patterns handle this
		return null;
	}
	
	private String inferTypeFromArgs(String args) {
		// Simple type inference - if argument is a number, assume I32
		if (args.trim().matches("\\d+")) {
			return "I32";
		}
		return null;
	}

}