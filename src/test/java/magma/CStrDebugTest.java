package magma;

import org.junit.jupiter.api.Test;

class CStrDebugTest extends CompilerTestBase {
	@Test
	void debugBasicCStr() throws Exception {
		String input = "let x : *CStr = \"Hello World!\";";
		String result = Compiler.compile(input);
		System.out.println("Input: " + input);
		System.out.println("Output: " + result);
		// Let's see what we actually get
	}
}