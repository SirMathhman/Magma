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
	void letTypeSuffix() {
		assertValid("let x = 200U64;", "uint64_t x = 200;");
	}

	@Test
	void letTypeSuffixSigned() {
		assertValid("let y = 127I8;", "int8_t y = 127;");
	}

	@Test
	void conflictingTypes() {
		assertInvalid("let x : U64 = 100I32;");
	}

	private void assertInvalid(String input) {
		assertThrows(CompileException.class, () -> Compiler.compile(input));
	}

	@Test
	void matchingTypes() {
		assertValid("let x : U32 = 100U32;", "uint32_t x = 100;");
	}

	@Test
	void invalid() {
		assertInvalid("?");
	}


}