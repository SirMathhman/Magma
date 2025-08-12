package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class CompilerTest {
	@Test
	void emptyInput() {
		assertValid("", "");
	}

	private void assertValid(String input, String output) {
		Compiler compiler = new Compiler();
		String actual = compiler.compile(input);
		assertEquals(output, actual);
	}

	@Test
	void throwsException() {
		assertInvalid("not empty");
	}

	private void assertInvalid(String input) {
		try {
			new Compiler().compile(input);
			fail("Expected CompileException to be thrown");
		} catch (CompileException e) {
			// expected
		}
	}
}
