package magma;

import org.junit.jupiter.api.Test;

import static magma.TestUtils.assertInvalid;
import static magma.TestUtils.assertValid;

class CompilerTest {
	@Test
	void valid() {
		assertValid("", "");
	}

	@Test
	void invalid() {
		assertInvalid("?");
	}

	@Test
	void letToInt32t() {
		assertValid("let x = 100;", "int32_t x = 100;");
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
	void incompatibleTypeAnnotationAndSuffix() {
		assertInvalid("let x : I32 = 100U64;");
	}

	@Test
	void compatibleTypeAnnotationAndSuffix() {
		assertValid("let x : I64 = 100I64;", "int64_t x = 100;");
	}
}