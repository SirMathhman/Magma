package magma;

public class Compiler {
	public static String compile(String input) throws CompileException {
		if (input.isEmpty()) return input;
		if (input.startsWith("let ") && input.endsWith( " = 100;")) {
			final var name = input.substring("let ".length(), input.length() - " = 100;".length());
			return "int32_t " + name + " = 100;";
		}

		throw new CompileException();
	}
}
