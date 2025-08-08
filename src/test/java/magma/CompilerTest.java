package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompilerTest {
	@Test
	void valid() {
		assertValid("", "");
	}

	@Test
	void compileI32Let() {
		assertValid("let x : I32 = 0;", "int32_t x = 0;");
	}

	@Test
	void compileAllIntegerLets() {
		// Unsigned
		assertValid("let x : U8 = 0;", "uint8_t x = 0;");
		assertValid("let x : U16 = 0;", "uint16_t x = 0;");
		assertValid("let x : U32 = 0;", "uint32_t x = 0;");
		assertValid("let x : U64 = 0;", "uint64_t x = 0;");
		// Signed
		assertValid("let x : I8 = 0;", "int8_t x = 0;");
		assertValid("let x : I16 = 0;", "int16_t x = 0;");
		assertValid("let x : I64 = 0;", "int64_t x = 0;");
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