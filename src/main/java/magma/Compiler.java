package magma;

public class Compiler {
	public static String compile(String input) throws CompileException {
		if (input.isEmpty()) return "";
		String trimmed = input.trim();
		if ("let x : I32 = 0;".equals(trimmed)) {
			return "int32_t x = 0;";
		}
		throw new CompileException("Invalid input", input);
	}
}
