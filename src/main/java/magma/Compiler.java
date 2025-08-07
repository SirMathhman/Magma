package magma;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Compiler {
	public static String compile(String input) throws CompileException {
		if (input.isEmpty()) return "";
		
		// Transform "let x : I32 = 100;" to "int32_t x = 100;"
		Pattern letPatternWithType = Pattern.compile("let\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*:\\s*I32\\s*=\\s*([^;]+);");
		Matcher matcherWithType = letPatternWithType.matcher(input);
		
		if (matcherWithType.find()) {
			String variableName = matcherWithType.group(1);
			String value = matcherWithType.group(2);
			return "int32_t " + variableName + " = " + value + ";";
		}
		
		// Transform "let x = 100;" to "int32_t x = 100;" (backward compatibility)
		Pattern letPattern = Pattern.compile("let\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*([^;]+);");
		Matcher matcher = letPattern.matcher(input);
		
		if (matcher.find()) {
			String variableName = matcher.group(1);
			String value = matcher.group(2);
			return "int32_t " + variableName + " = " + value + ";";
		}
		
		throw new CompileException();
	}
}
