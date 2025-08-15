package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompilerTest {
	@Test
	void valid() {
		final var actual = Compiler.compile("");
		assertEquals("", actual);
	}

	@Test
	void invalid() {
		assertEquals(CompileException.class, Compiler.compile("?"));
	}
}