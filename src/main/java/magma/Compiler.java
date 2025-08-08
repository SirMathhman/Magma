package magma;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Compiler {
	private static final Pattern LET_I32_PATTERN = Pattern.compile(
			"^\\s*let\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*:\\s*I32\\s*=\\s*0\\s*;\\s*$");

	public static String compile(String input) throws CompileException {
		if (input == null) throw new CompileException();
		if (input.isEmpty()) return "";

		Matcher m = LET_I32_PATTERN.matcher(input);
		if (m.matches()) {
			String name = m.group(1);
			return "#include <stdint.h>\nint32_t " + name + " = 0;";
		}

		throw new CompileException();
	}
}
