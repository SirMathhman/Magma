package magma;

public class Compiler {
 public static String compile(String input) throws CompileException {
 	if (input.isEmpty()) return "";
	
 	// Transform "let x = 100;" to "int32_t = 100;"
 	if (input.matches("let\\s+\\w+\\s*=\\s*\\d+\\s*;")) {
 		return input.replaceFirst("let\\s+\\w+", "int32_t");
 	}
	
 	throw new CompileException();
 }
}
