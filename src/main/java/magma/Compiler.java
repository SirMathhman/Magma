package magma;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Compiler {
	private static final Pattern LET_INT_PATTERN = Pattern.compile(
			"^\\s*let\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*:\\s*([IU])(8|16|32|64)\\s*=\\s*0\\s*;\\s*$");
	// New pattern: let <name> = <number><suffix>; where suffix is [IU](8|16|32|64)
	private static final Pattern LET_TYPED_LITERAL_PATTERN = Pattern.compile(
			"^\\s*let\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*([0-9]+)\\s*([IU])(8|16|32|64)\\s*;\\s*$");
	// New pattern: let <name> = <int>; defaults to I32
	private static final Pattern LET_DEFAULT_I32_PATTERN = Pattern.compile(
			"^\\s*let\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*([0-9]+)\\s*;\\s*$");

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

		Matcher m3 = LET_TYPED_LITERAL_PATTERN.matcher(input);
		if (m3.matches()) {
			String name = m3.group(1);
			String value = m3.group(2);
			String sign = m3.group(3);
			String bits = m3.group(4);
			String cType = ("U".equals(sign) ? "uint" : "int") + bits + "_t";
			return "#include <stdint.h>\n" + cType + " " + name + " = " + value + ";";
		}

		Matcher m2 = LET_DEFAULT_I32_PATTERN.matcher(input);
		if (m2.matches()) {
			String name = m2.group(1);
			String value = m2.group(2);
			return "#include <stdint.h>\nint32_t " + name + " = " + value + ";";
		}

		throw new CompileException();
	}
}
