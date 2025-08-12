package magma;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;
import java.util.Map;

public class Compiler {
	private static Map<String, Boolean> mutableVariables = new HashMap<>();

	public static String run(String input) throws CompileException {
		mutableVariables.clear();
		
		if (input.trim().isEmpty()) {
			return "";
		}
		
		String[] statements = input.split(";");
		StringBuilder result = new StringBuilder();
		
		for (int i = 0; i < statements.length; i++) {
			String statement = statements[i].trim();
			if (statement.isEmpty()) {
				continue;
			}
			
			if (i > 0) {
				result.append(" ");
			}
			
			result.append(compileStatement(statement));
			result.append(";");
		}
		
		return result.toString();
	}
	
	private static String compileStatement(String statement) throws CompileException {
		// Handle let statements
		Pattern letPattern = Pattern.compile("let\\s+(mut\\s+)?([a-zA-Z_][a-zA-Z0-9_]*)\\s*(:\\s*([a-zA-Z0-9_]+))?\\s*=\\s*(.+)");
		Matcher letMatcher = letPattern.matcher(statement);
		
		if (letMatcher.matches()) {
			boolean isMutable = letMatcher.group(1) != null;
			String varName = letMatcher.group(2);
			String type = letMatcher.group(4);
			String value = letMatcher.group(5);
			
			mutableVariables.put(varName, isMutable);
			
			// If no explicit type, infer from value
			if (type == null) {
				// For now, assume integer literals are I32
				if (value.matches("\\d+")) {
					type = "I32";
				}
			}
			
			// Convert type to C++ equivalent
			String cppType = convertType(type);
			
			return cppType + " " + varName + " = " + value;
		}
		
		// Handle assignment statements
		Pattern assignmentPattern = Pattern.compile("([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*(.+)");
		Matcher assignmentMatcher = assignmentPattern.matcher(statement);
		
		if (assignmentMatcher.matches()) {
			String varName = assignmentMatcher.group(1);
			String value = assignmentMatcher.group(2);
			
			// Check if variable is mutable
			if (!mutableVariables.containsKey(varName) || !mutableVariables.get(varName)) {
				throw new CompileException("Cannot assign to immutable variable", varName);
			}
			
			return varName + " = " + value;
		}
		
		throw new CompileException("Invalid input", statement);
	}
	
	private static String convertType(String type) {
		switch (type) {
			case "I32":
				return "int32_t";
			default:
				return type;
		}
	}
}
