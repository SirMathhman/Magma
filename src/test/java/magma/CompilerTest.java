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
		assertThrows(CompileException.class, () -> Compiler.compile(""));
	}
}