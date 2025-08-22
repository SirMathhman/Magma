package magma;

public class Compiler {
	public static String compile(String input) throws CompileException {
		throw new CompileException("Undefined symbol: " + input);
	}
}
