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
		try {
			assertEquals("", Compiler.compile(""));
		} catch (CompileException e) {
			fail(e);
		}
	}

	@Test
	void letI32() {
		String src = "let x :  I32 = 0;";
		String expected = "#include <stdint.h>\nint32_t x = 0;";
		try {
			assertEquals(expected, Compiler.compile(src));
		} catch (CompileException e) {
			fail(e);
		}
	}
}