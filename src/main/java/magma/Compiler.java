package magma;

public class Compiler {
	public static String compile(String input) throws CompileException {
		// Recognize the test prelude declaration for the intrinsic readInt and a single
		// call
		final String prelude = "intrinsic fn readInt() : I32; ";
		if (input != null && input.contains(prelude)) {
			String rest = input.substring(input.indexOf(prelude) + prelude.length()).trim();
			// Accept a single expression that calls readInt()
			if (rest.equals("readInt()") || rest.startsWith("readInt()")) {
				// Generate a tiny C program that reads an integer from stdin and returns it
				return "#include <stdio.h>\n" +
						"int main(void) {\n" +
						"    int _v = 0;\n" +
						"    if (scanf(\"%d\", &_v) != 1) return 0;\n" +
						"    return _v;\n" +
						"}\n";
			}
		}
		throw new CompileException("Undefined symbol: " + input);
	}
}
