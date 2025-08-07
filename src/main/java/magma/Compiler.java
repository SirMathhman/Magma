package magma;

/**
 * Main compiler class that coordinates between different specialized compilers.
 */
public class Compiler {
	/**
	 * Compiles the input string to C-style code.
	 */
	public static String compile(String input) throws CompileException {
		if (input.isEmpty()) return "";

		// Try to compile as an array type declaration
		String result = ArrayTypeCompiler.tryCompile(input);
		if (!result.isEmpty()) return result;

		// Try to compile with explicit type annotation
		result = ExplicitTypeCompiler.tryCompile(input);
		if (!result.isEmpty()) return result;

		// Try to compile without explicit type annotation
		result = ImplicitTypeCompiler.tryCompile(input);
		if (!result.isEmpty()) return result;

		throw new CompileException();
	}
}