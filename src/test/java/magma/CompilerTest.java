package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompilerTest {
	@Test
	void valid() {
		assertValid("", "");
	}

	@Test
	void letToInt32() {
		assertValid("let x = 100;", "int32_t x = 100;");
	}

	@Test
	void letToInt32WithSpaces() {
		assertValid("let   y  =  42;", "int32_t y = 42;");
	}

	@Test
	void letToInt32WithTypeAnnotation() {
		assertValid("let x : I32 = 100;", "int32_t x = 100;");
	}

	@Test
	void letToInt32WithTypeAnnotationAndSpaces() {
		assertValid("let   z  :  I32  =  200;", "int32_t z = 200;");
	}

	@Test
	void letWithSignedIntegerTypes() {
		// Test I8
		assertValid("let a : I8 = 10;", "int8_t a = 10;");

		// Test I16
		assertValid("let b : I16 = 1000;", "int16_t b = 1000;");

		// Test I64
		assertValid("let c : I64 = 9223372036854775807;", "int64_t c = 9223372036854775807;");
	}

	@Test
	void letWithUnsignedIntegerTypes() {
		// Test U8
		assertValid("let d : U8 = 255;", "uint8_t d = 255;");

		// Test U16
		assertValid("let e : U16 = 65535;", "uint16_t e = 65535;");

		// Test U32
		assertValid("let f : U32 = 4294967295;", "uint32_t f = 4294967295;");

		// Test U64
		assertValid("let g : U64 = 18446744073709551615;", "uint64_t g = 18446744073709551615;");
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
		assertInvalid("?");
	}

	private CompileException assertInvalid(String input) {
		return assertThrows(CompileException.class, () -> Compiler.compile(input));
	}
}