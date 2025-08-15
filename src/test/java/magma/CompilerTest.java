package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompilerTest {
	@Test
	void valid() {
		try {
			final var actual = Compiler.compile("");
			assertEquals("", actual);
		} catch (CompileException e) {
			fail(e);
		}
	}

	@Test
	void invalid() {
		assertThrows(CompileException.class, () -> Compiler.compile("?"));
	}
}