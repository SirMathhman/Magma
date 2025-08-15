package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompilerTest {
	@Test
	void valid() {
		assertValid("", "");
	}

	private void assertValid(String input, String output) {
		final var result = Compiler.compile(input);
		if (result.isErr()) {
			fail("Expected successful compilation but got error: " + result.unwrapErr());
		}
		assertEquals(output, result.unwrap());
	}

	@Test
	void invalid() {
		final var result = Compiler.compile("?");
		assertTrue(result.isErr());
		assertTrue(result.unwrapErr() instanceof CompileException);
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
	void method() {
		assertValidWithinClass("void method(){}",
													 "void method_Test(void* _ref_) {struct Test this = *(struct Test*) _ref_;}");
	}

	@Test
	void methodWithOneParameter() {
		assertValidWithinClass("void method(int a){}",
													 "void method_Test(void* _ref_, int a) {struct Test this = *(struct Test*) _ref_;}");
	}

	@Test
	void methodWithStringParameter() {
		assertValidWithinClass("void method(String a){}",
													 "void method_Test(void* _ref_, const char* a) {struct Test this = *(struct Test*) _ref_;}");
	}

	@Test
	void methodReturnsString() {
		assertValidWithinClass("String method() { return \"test\"; }",
													 "const char* method_Test(void* _ref_) {struct Test this = *(struct Test*) _ref_; return \"test\";}");
	}

	private void assertValidWithinClass(String input, String output) {
		assertValid("class Test {" + input + "}", "struct Test {}; " + output);
	}

	@Test
	void classWithPublicKeywordStripped() {
		assertValid("public class Empty {}", "struct Empty {};");
	}
}