package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompilerTest {
	@Test
	void valid() {
		assertValid("", "");
	}

	@Test
	void invalid() {
		assertThrows(CompileException.class, () -> Compiler.compile("?"));
	}

	@Test
	void letToInt32t() {
		assertValid("let x = 100;", "int32_t x = 100;");
	}

	private void assertValid(String input, String output) {
		try {
			assertEquals(output, Compiler.compile(input));
		} catch (CompileException e) {
			fail(e);
		}
	}

	@Test
	void letToInt32tWithSpaces() {
		assertValid("let   myVar  =  42;", "int32_t myVar = 42;");
	}

	@Test
	void letToInt32tWithTypeAnnotation() {
		assertValid("let x : I32 = 100;", "int32_t x = 100;");
	}

	@Test
	void letToUint64tWithSuffix() {
		assertValid("let x = 10U64;", "uint64_t x = 10;");
	}

	@Test
	void letToUint64tWithSuffixAndSpaces() {
		assertValid("let   myVar  =  42U64;", "uint64_t myVar = 42;");
	}

	@Test
	void letToUint64tWithTypeAnnotationAndSuffix() {
		assertValid("let x : I32 = 100U64;", "uint64_t x = 100;");
	}
}