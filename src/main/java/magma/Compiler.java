package magma;

public class Compiler {
	// TODO: Implement compiler logic
	public String compile(String source) {
	    if (source.isEmpty()) {
	        return "";
	    }
	    throw new CompileException("Input is not empty");
	}
}
