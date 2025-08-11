package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompilerTest {
	@Test
	void valid() {
		assertValid("", "");
	}

	@Test
	void let() {
		assertValid("let x = 10;", "int32_t x = 10;");
	}

	@Test
	void letName() {
		assertValid("let y = 10;", "int32_t y = 10;");
	}

	@Test
	void letValue() {
		assertValid("let y = 20;", "int32_t y = 20;");
	}

	@Test
	void letType() {
		assertValid("let y : I32 = 20;", "int32_t y = 20;");
	}

	private void assertValid(String input, String output) {
		try {
			assertEquals(output, Compiler.compile(input));
		} catch (CompileException e) {
			fail(e);
		}
	}

	@Test
	void invalid() {
		assertThrows(CompileException.class, () -> Compiler.compile("?"));
	}
}