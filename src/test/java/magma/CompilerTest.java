package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompilerTest {
	@Test
	void valid() {
		try {
			assertEquals("", Compiler.compile(""));
		} catch (CompileException e) {
			fail(e);
		}
	}

	@Test
	void invalid() {
		assertThrows(CompileException.class, () -> Compiler.compile("?"));
	}
	
	@Test
	void letToInt32t() {
		try {
			assertEquals("int32_t x = 100;", Compiler.compile("let x = 100;"));
		} catch (CompileException e) {
			fail(e);
		}
	}
	
	@Test
	void letToInt32tWithSpaces() {
		try {
			assertEquals("int32_t myVar = 42;", Compiler.compile("let   myVar  =  42;"));
		} catch (CompileException e) {
			fail(e);
		}
	}
}