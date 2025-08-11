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
		final var typeSeparator = substring.indexOf(" : ");

		final String cType;
		String name;
		if (typeSeparator >= 0) {
			name = substring.substring(0, typeSeparator);
			final var type = substring.substring(typeSeparator + " : ".length());
			cType = (type.startsWith("U") ? "u" : "") + "int32_t";
		} else {
			name = substring;
			cType = "int32_t";
		}

		var withEnd = input1.substring(i + " = ".length());
		if (!withEnd.endsWith(";")) return Optional.empty();
		final var slice = withEnd.substring(0, withEnd.length() - ";".length());

		return Optional.of(cType + " " + name + " = " + slice + ";");
	}
}
