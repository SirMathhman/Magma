package magma;

import org.junit.jupiter.api.Test;

import static magma.TestUtils.assertValid;

class SignedIntegerTypeTest {
	// Tests for signed integer type annotations
	@Test
	void letToInt8tWithTypeAnnotation() {
		assertValid("let x : I8 = 100;", "int8_t x = 100;");
	}
	
	@Test
	void letToInt16tWithTypeAnnotation() {
		assertValid("let x : I16 = 100;", "int16_t x = 100;");
	}
	
	@Test
	void letToInt32tWithTypeAnnotation() {
		assertValid("let x : I32 = 100;", "int32_t x = 100;");
	}
	
	@Test
	void letToInt64tWithTypeAnnotation() {
		assertValid("let x : I64 = 100;", "int64_t x = 100;");
	}
	
	// Tests for signed integer suffixes
	@Test
	void letToInt8tWithSuffix() {
		assertValid("let x = 10I8;", "int8_t x = 10;");
	}
	
	@Test
	void letToInt16tWithSuffix() {
		assertValid("let x = 10I16;", "int16_t x = 10;");
	}
	
	@Test
	void letToInt32tWithSuffix() {
		assertValid("let x = 10I32;", "int32_t x = 10;");
	}
	
	@Test
	void letToInt64tWithSuffix() {
		assertValid("let x = 10I64;", "int64_t x = 10;");
	}
}