package magma;

import java.util.Optional;

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
		Optional<String> result = ArrayTypeCompiler.tryCompile(input);
		if (result.isPresent()) return result.get();

		// Try to compile with explicit type annotation
		result = ExplicitTypeCompiler.tryCompile(input);
		if (result.isPresent()) return result.get();

		// Try to compile without explicit type annotation
		result = ImplicitTypeCompiler.tryCompile(input);
		if (result.isPresent()) return result.get();

		throw new CompileException();
	}
}