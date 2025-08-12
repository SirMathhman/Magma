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

	@Test
	void intWithTypeSuffix() {
		assertValid("let x : I32 = 100I32;", "int32_t x = 100;");
	}

	@Test
	void intWithDifferentType() {
		// Update this to test U8 to U64, and I8 through I64.
		assertValid("let x = 100I64;", "int64_t x = 100;");
	}

	@Test
	void testTrue() {
		assertValid("let x : Bool = true;", "bool x = true;");
	}

	@Test
	void testFalse() {
		assertValid("let x : Bool = false;", "bool x = false;");
	}

	@Test
	void boolWithImplicitType() {
		assertValid("let x = true;", "bool x = true;");
	}

	@Test
	void float32() {
		assertValid("let x : F32 = 3.14F32;", "float x = 3.14f;");
	}

	@Test
	void float64() {
		assertValid("let x : F64 = 3.14F64;", "double x = 3.14;");
	}

	@Test
	void floatDefaultsToF32() {
		assertValid("let x = 3.14;", "float x = 3.14f;");
	}

	@Test
	void float32WithExplicitSuffix() {
		assertValid("let x = 3.14F32;", "float x = 3.14f;");
	}

	@Test
	void float64WithExplicitSuffix() {
		assertValid("let x = 3.14F64;", "double x = 3.14;");
	}

	@Test
	void testCharAsU8() {
		assertValid("let x : U8 = 'a';", "uint8_t x = 'a';");
	}
}