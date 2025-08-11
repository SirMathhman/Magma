package magma;

public class Compiler {
	public static String compile(String input) throws CompileException {
		if (input.isEmpty()) return input;
		if (input.startsWith("let ")) {
			final var i = input.indexOf(" = ");
			if (i >= 0) {
				var name = input.substring("let ".length(), i);
				var withEnd = input.substring(i + " = ".length());
				if (withEnd.endsWith(";")) {
					final var slice = withEnd.substring(0, withEnd.length() - ";".length());
					return "int32_t " + name + " = " + slice + ";";
				}
			}
		}

		throw new CompileException();
	}
}
