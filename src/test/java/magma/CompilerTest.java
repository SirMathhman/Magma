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
		assertValid("let x : I32 = 0;", "#include <stdint.h>\nint32_t x = 0;");
	}

	@Test
	void letI8() {
		assertValid("let a : I8 = 0;", "#include <stdint.h>\nint8_t a = 0;");
	}

	@Test
	void letI16() {
		assertValid("let b : I16 = 0;", "#include <stdint.h>\nint16_t b = 0;");
	}

	@Test
	void letI64() {
		assertValid("let c : I64 = 0;", "#include <stdint.h>\nint64_t c = 0;");
	}

	@Test
	void letU8() {
		assertValid("let d : U8 = 0;", "#include <stdint.h>\nuint8_t d = 0;");
	}

	@Test
	void letU16() {
		assertValid("let e : U16 = 0;", "#include <stdint.h>\nuint16_t e = 0;");
	}

	@Test
	void letU32() {
		assertValid("let f : U32 = 0;", "#include <stdint.h>\nuint32_t f = 0;");
	}

	@Test
	void letU64() {
		assertValid("let g : U64 = 0;", "#include <stdint.h>\nuint64_t g = 0;");
	}

	private void assertValid(String input, String output) {
		try {
			assertEquals(output, Compiler.compile(input));
		} catch (CompileException e) {
			fail(e);
		}
	}
}