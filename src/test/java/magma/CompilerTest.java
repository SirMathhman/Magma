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
		assertValid("let x : I32 = 100;", "int32_t x = 100;");
	}

	@Test
	void letWithDifferentName() {
		assertValid("let y : I32 = 100;", "int32_t y = 100;");
	}

	@Test
	void letWithTypeInference() {
		assertValid("let x = 100;", "int32_t x = 100;");
	}

	@Test
	void letWithDifferentValue() {
		assertValid("let x = 200;", "int32_t x = 200;");
	}

	@Test
	void letWithMutModifier() {
		assertValid("let mut x = 100; x = 200;", "int32_t x = 100; x = 200;");
	}

	@Test
	void letWithoutMutModifier() {
		assertInvalid("let x = 100; x = 200;");
	}

	private void assertValid(String input, String output) {
		try {
			assertEquals(output, Compiler.run(input));
		} catch (CompileException e) {
			fail(e);
		}
	}

	@Test
	void invalid() {
		assertInvalid("?");
	}

	private void assertInvalid(String input) {
		assertThrows(CompileException.class, () -> Compiler.run(input));
	}
}