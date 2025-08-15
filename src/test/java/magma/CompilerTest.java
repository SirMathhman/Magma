package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompilerTest {
	public static final String CLASS_NAME = "Test";

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
	void classWithMethod() {
		assertValidWithinClass("void method(){}",
													 "void method_" + CLASS_NAME + "(void* _ref_) {struct " + CLASS_NAME + " this = *(struct " +
													 CLASS_NAME + "*) _ref_;}");
	}

	private void assertValidWithinClass(String input, String output) {
		assertValid("class " + CLASS_NAME + " {" + input + "}", "struct " + CLASS_NAME + " {}; " + output);
	}

	@Test
	void classWithPublicKeywordStripped() {
		assertValid("public class Empty {}", "struct Empty {};");
	}
}