package magma;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Compiler {
	public static String compile(String input) throws CompileException {
		if (input.isEmpty()) return "";
		
		// Pattern to match "let x = 100;" format
		Pattern letPattern = Pattern.compile("let\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*(\\d+)\\s*;");
		Matcher matcher = letPattern.matcher(input);
		
		if (matcher.matches()) {
			String variableName = matcher.group(1);
			String value = matcher.group(2);
			return "int32_t " + variableName + " = " + value + ";";
		}
		
		throw new CompileException();
	}
}
