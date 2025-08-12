package magma;

public class Compiler {
	// TODO: Implement compiler logic
	public String compile(String source) {
		if (source.isEmpty()) {
			return "";
		}
		// Support 'let <name> = <value>;' pattern
		String trimmed = source.trim();
		if (trimmed.matches("let\\s+[a-zA-Z_][a-zA-Z0-9_]*\\s*=\\s*\\d+;")) {
			String[] parts = trimmed.replace("let","").replace(";","").trim().split("=");
			String name = parts[0].trim();
			String value = parts[1].trim();
			return "int32_t " + name + " = " + value + ";";
		}
		throw new CompileException("Input is not supported");
	}
}
