package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompilerTest {
	@Test
	void valid() {
		assertValid("", "");
	}
	
	@Test
	void letToInt32() {
		assertValid("let x = 100;", "int32_t x = 100;");
	}

	@Test
	void letToInt32WithSpaces() {
		assertValid("let   y  =  42;", "int32_t y = 42;");
	}
	
	@Test
	void letToInt32WithTypeAnnotation() {
		assertValid("let x : I32 = 100;", "int32_t x = 100;");
	}
	
	@Test
	void letToInt32WithTypeAnnotationAndSpaces() {
		assertValid("let   z  :  I32  =  200;", "int32_t z = 200;");
	}

	private void assertValid(String input, String output) {
		try {
			assertEquals(output, Compiler.compile(input));
		} catch (CompileException e) {
			fail(e);
		}
	}

	@Test
	void invalid() {
		assertInvalid("?");
	}

	private CompileException assertInvalid(String input) {
		return assertThrows(CompileException.class, () -> Compiler.compile(input));
	}
}