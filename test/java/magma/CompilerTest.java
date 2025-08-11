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
		assertValid("let x = 100;", "int32_t x = 100;");
	}

	@Test
	void letName() {
		assertValid("let y = 100;", "int32_t y = 100;");
	}

	@Test
	void letValue() {
		assertValid("let y = 200;", "int32_t y = 200;");
	}

	@Test
	void letType() {
		assertValid("let y : I32 = 200;", "int32_t y = 200;");
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