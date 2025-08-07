package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompilerTest {
	@Test
	void valid() {
		assertValid("", "");
	}

	@Test
	void invalid() {
		assertInvalid("?");
	}

	@Test
	void letDeclaration() {
		assertValid("int32_t x = 100;", "let x = 100;");
	}

	@Test
	void letDeclarationWithDifferentVariable() {
		assertValid("int32_t counter = 42;", "let counter = 42;");
	}

	private void assertValid(String expected, String input) {
		try {
			assertEquals(expected, Compiler.compile(input));
		} catch (CompileException e) {
			fail(e);
		}
	}

	@Test
	void invalidLetDeclaration() {
		assertInvalid("let x = ;");
		assertInvalid("let = 100;");
		assertInvalid("let x 100;");
	}

	private void assertInvalid(String input) {
		assertThrows(CompileException.class, () -> Compiler.compile(input));
	}

	@Test
	void letDeclarationWithI32Type() {
		assertValid("int32_t x = 100;", "let x : I32 = 100;");
	}

	@Test
	void letDeclarationWithU8Type() {
		assertValid("uint8_t x = 100;", "let x : U8 = 100;");
	}

	@Test
	void letDeclarationWithU16Type() {
		assertValid("uint16_t x = 100;", "let x : U16 = 100;");
	}

	@Test
	void letDeclarationWithU32Type() {
		assertValid("uint32_t x = 100;", "let x : U32 = 100;");
	}

	@Test
	void letDeclarationWithU64Type() {
		assertValid("uint64_t x = 100;", "let x : U64 = 100;");
	}

	@Test
	void letDeclarationWithI8Type() {
		assertValid("int8_t x = 100;", "let x : I8 = 100;");
	}

	@Test
	void letDeclarationWithI16Type() {
		assertValid("int16_t x = 100;", "let x : I16 = 100;");
	}

	@Test
	void letDeclarationWithI64Type() {
		assertValid("int64_t x = 100;", "let x : I64 = 100;");
	}
}