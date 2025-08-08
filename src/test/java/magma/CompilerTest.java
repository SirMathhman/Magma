package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompilerTest {
	@Test
	void invalid() {
		assertThrows(CompileException.class, () -> Compiler.compile("?"));
	}

	@Test
	void valid() {
		assertValid("", "");
	}

	@Test
	void letI32() {
		assertValid("let x :  I32 = 0;", "#include <stdint.h>\nint32_t x = 0;");
	}

	private void assertValid(String input, String output) {
		try {
			assertEquals(output, Compiler.compile(input));
		} catch (CompileException e) {
			fail(e);
		}
	}
}