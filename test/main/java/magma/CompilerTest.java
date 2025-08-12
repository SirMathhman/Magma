package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompilerTest {
	@Test
	void testCompileReturnsInput() {
		Compiler compiler = new Compiler();
		String input = "test input";
		String output = compiler.compile(input);
		assertEquals(input, output, "compile should return the input string");
	}
}

