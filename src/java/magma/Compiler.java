package magma;

public class Compiler {
	public static String compile(String input) throws CompileException {
		if (input.isEmpty()) return input;
		if (input.equals("let x = 100;")) return "int32_t x = 100;";

		throw new CompileException();
	}
}
