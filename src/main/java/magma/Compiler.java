package magma;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Compiler {
	private static final Pattern LET_PATTERN = Pattern.compile("let\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*(?:\\s*:\\s*I32)?\\s*=\\s*([0-9]+)(U64)?;");

	public static String compile(String input) throws CompileException {
		if (input.isEmpty()) return "";

		Matcher matcher = LET_PATTERN.matcher(input);
		if (matcher.matches()) {
			String type = matcher.group(3) != null ? "uint64_t" : "int32_t";
			return type + " " + matcher.group(1) + " = " + matcher.group(2) + ";";
		}

		throw new CompileException();
	}
}
