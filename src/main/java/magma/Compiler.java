package magma;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Compiler {
	private static final Pattern LET_PATTERN = Pattern.compile("^let\\s+(\\w+)(?:\\s*:\\s*\\w+)?\\s*=\\s*(\\d+);$");

	public static String compile(String input) throws CompileException {
		if (input.isEmpty()) return input;

		Matcher matcher = LET_PATTERN.matcher(input.trim());
		if (matcher.matches()) {
			String variableName = matcher.group(1);
			String value = matcher.group(2);
			return "int32_t " + variableName + " = " + value + ";";
		}

		throw new CompileException("Invalid input: " + input);
	}
}
