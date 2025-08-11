package magma;

import java.util.Optional;

public class Compiler {
	public static String compile(String input) throws CompileException {
		if (input.isEmpty()) return input;

		return compileLet(input).orElseThrow(CompileException::new);
	}

	private static Optional<String> compileLet(String input) {
		if (!input.startsWith("let ")) return Optional.empty();
		final var input1 = input.substring("let ".length());

		final var i = input1.indexOf(" = ");
		if (i < 0) return Optional.empty();
		final var substring = input1.substring(0, i);
		final var withEnd = input1.substring(i + " = ".length());

		final var typeSeparator = substring.indexOf(" : ");
		if (typeSeparator < 0) return assembleLet("int32_t", substring, withEnd);
		final var type = substring.substring(typeSeparator + " : ".length());
		final var bits = type.substring(1);

		final String cType;
		if (type.startsWith("U")) cType = "u" + "int" + bits + "_t";
		else cType = "int" + bits + "_t";
		return assembleLet(cType, substring.substring(0, typeSeparator), withEnd);
	}

	private static Optional<String> assembleLet(String cType, String name, String withEnd) {
		if (!withEnd.endsWith(";")) return Optional.empty();
		final var slice = withEnd.substring(0, withEnd.length() - ";".length());
		return Optional.of(cType + " " + name + " = " + slice + ";");
	}
}
