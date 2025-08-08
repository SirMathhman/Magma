package magma;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Compiler {
	private static final Pattern LET_INT_PATTERN = Pattern.compile(
			"^\\s*let\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*:\\s*([IU])(8|16|32|64)\\s*=\\s*0\\s*;\\s*$");

	public static String compile(String input) throws CompileException {
		if (input == null) throw new CompileException();
		if (input.isEmpty()) return "";

		Matcher m = LET_INT_PATTERN.matcher(input);
		if (m.matches()) {
			String name = m.group(1);
			String sign = m.group(2);
			String bits = m.group(3);
			String cType;
			if ("U".equals(sign)) {
				cType = "uint";
			} else {
				cType = "int";
			}
			cType = cType + bits + "_t";
			return "#include <stdint.h>\n" + cType + " " + name + " = 0;";
		}

		throw new CompileException();
	}
}
