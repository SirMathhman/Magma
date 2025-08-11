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
		var name = input1.substring(0, i);
		var withEnd = input1.substring(i + " = ".length());

		if (!withEnd.endsWith(";")) return Optional.empty();
		final var slice = withEnd.substring(0, withEnd.length() - ";".length());

		return Optional.of("int32_t " + name + " = " + slice + ";");
	}
}
