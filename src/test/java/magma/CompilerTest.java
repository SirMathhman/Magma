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
	void letDeclaration() {
		try {
			assertEquals("int32_t x = 100;", Compiler.compile("let x = 100;"));
		} catch (CompileException e) {
			fail(e);
		}
	}
	
	@Test
	void letDeclarationWithDifferentVariable() {
		try {
			assertEquals("int32_t counter = 42;", Compiler.compile("let counter = 42;"));
		} catch (CompileException e) {
			fail(e);
		}
	}
	
	@Test
	void invalidLetDeclaration() {
		assertThrows(CompileException.class, () -> Compiler.compile("let x = ;"));
		assertThrows(CompileException.class, () -> Compiler.compile("let = 100;"));
		assertThrows(CompileException.class, () -> Compiler.compile("let x 100;"));
	}
}