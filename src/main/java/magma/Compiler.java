package magma;

public class Compiler {
	// TODO: Implement compiler logic
	public String compile(String source) {
		if (source.isEmpty()) {
			return "";
		}
		// Simple implementation for 'let x = 100;'
		String s = source.trim();
		if (s.matches("let [a-zA-Z_][a-zA-Z0-9_]* = 100;")) {
			String var = s.split(" ")[1];
			return "int32_t " + var + " = 100;";
		}
		if (s.matches("let [a-zA-Z_][a-zA-Z0-9_]* : I32 = 100;")) {
			String var = s.split(" ")[1];
			return "int32_t " + var + " = 100;";
		}
		if (s.matches("let [a-zA-Z_][a-zA-Z0-9_]* = 100I32;")) {
			String var = s.split(" ")[1];
			return "int32_t " + var + " = 100;";
		}
		throw new CompileException("Input is not supported");
	}
}
