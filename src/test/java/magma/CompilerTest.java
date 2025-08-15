package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompilerTest {
	@Test
	void valid() {
		assertValid("", "");
	}

	private void assertValid(String input, String output) {
		try {
			final var actual = Compiler.compile(input);
			assertEquals(output, actual);
		} catch (CompileException e) {
			fail(e);
		}
	}

	@Test
	void invalid() {
		assertThrows(CompileException.class, () -> Compiler.compile("?"));
	}

	@Test
	void testPackage() {
		assertValid("package test;", "");
	}

	@Test
	void testClass() {
		assertValid("class Empty {}", "struct Empty {};");
	}

	@Test
	void testMethod() {
		assertValid("void method() {}", "void method() {}");
	}

	@Test
	void testIf() {
		assertValid("if (true) {}", "if (true) {}");
	}

	@Test
	void testReturn() {
		assertValid("return;", "return;");
	}
}