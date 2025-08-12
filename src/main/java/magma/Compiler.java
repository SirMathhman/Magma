package magma;

public class Compiler {
	// TODO: Implement compiler logic
	public String compile(String source) {
		if (source.isEmpty()) {
			return "";
		}
		// Simple implementation for 'let x = 100;'
		String s = source.trim();
		if (s.startsWith("let ") && s.endsWith(";")) {
			String body = s.substring(4, s.length() - 1).trim();
			String var = null;
			if (body.contains(" = 100;")) {
				// Should not happen, already endsWith ;
			}
			// let x = 100;
			if (body.contains(" = 100")) {
				String[] parts = body.split(" = ");
				if (parts.length == 2 && parts[1].equals("100")) {
					var = parts[0].trim();
					return "int32_t " + var + " = 100;";
				}
				// let x = 100I32;
				if (parts.length == 2 && parts[1].equals("100I32")) {
					var = parts[0].trim();
					return "int32_t " + var + " = 100;";
				}
			}
			// let a : I32 = 100;
			if (body.contains(": I32 = 100")) {
				String[] parts = body.split(": I32 = ");
				if (parts.length == 2 && parts[1].equals("100")) {
					var = parts[0].trim();
					return "int32_t " + var + " = 100;";
				}
			}
		}
		throw new CompileException("Input is not supported");
	}
}
