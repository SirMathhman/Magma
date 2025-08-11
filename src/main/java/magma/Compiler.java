package magma;

public class Compiler {
	public static String compile(String input) throws CompileException {
		if (input.isEmpty()) return input;
		throw new CompileException("Invalid input: " + input);
	}
}
