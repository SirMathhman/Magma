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
		assertValid("let x = 100;", "int32_t x = 100;");
	}

	@Test
	void letDeclarationWithDifferentVariable() {
		assertValid("let counter = 42;", "int32_t counter = 42;");
	}

	private void assertValid(String input, String expected) {
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
		assertValid("let x : I32 = 100;", "int32_t x = 100;");
	}

	@Test
	void letDeclarationWithU8Type() {
		assertValid("let x : U8 = 100;", "uint8_t x = 100;");
	}

	@Test
	void letDeclarationWithU16Type() {
		assertValid("let x : U16 = 100;", "uint16_t x = 100;");
	}

	@Test
	void letDeclarationWithU32Type() {
		assertValid("let x : U32 = 100;", "uint32_t x = 100;");
	}

	@Test
	void letDeclarationWithU64Type() {
		assertValid("let x : U64 = 100;", "uint64_t x = 100;");
	}

	@Test
	void letDeclarationWithI8Type() {
		assertValid("let x : I8 = 100;", "int8_t x = 100;");
	}

	@Test
	void letDeclarationWithI16Type() {
		assertValid("let x : I16 = 100;", "int16_t x = 100;");
	}

	@Test
	void letDeclarationWithI64Type() {
		assertValid("let x : I64 = 100;", "int64_t x = 100;");
	}
	
	@Test
	void letDeclarationWithU64Suffix() {
		assertValid("let x = 100U64;", "uint64_t x = 100;");
	}
	
	@Test
	void letDeclarationWithU32Suffix() {
		assertValid("let x = 100U32;", "uint32_t x = 100;");
	}
	
	@Test
	void letDeclarationWithU16Suffix() {
		assertValid("let x = 100U16;", "uint16_t x = 100;");
	}
	
	@Test
	void letDeclarationWithU8Suffix() {
		assertValid("let x = 100U8;", "uint8_t x = 100;");
	}
	
	@Test
	void letDeclarationWithI64Suffix() {
		assertValid("let x = 100I64;", "int64_t x = 100;");
	}
	
	@Test
	void letDeclarationWithI32Suffix() {
		assertValid("let x = 100I32;", "int32_t x = 100;");
	}
	
	@Test
	void letDeclarationWithI16Suffix() {
		assertValid("let x = 100I16;", "int16_t x = 100;");
	}
	
	@Test
	void letDeclarationWithI8Suffix() {
		assertValid("let x = 100I8;", "int8_t x = 100;");
	}
	
	@Test
	void typeMismatchBetweenAnnotationAndSuffix() {
		assertInvalid("let x : I32 = 0U64;");
		assertInvalid("let x : U8 = 100I16;");
		assertInvalid("let x : I64 = 42U32;");
	}
}