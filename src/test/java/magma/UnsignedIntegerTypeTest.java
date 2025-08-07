package magma;

import org.junit.jupiter.api.Test;

import static magma.TestUtils.assertValid;

class UnsignedIntegerTypeTest {
	// Tests for unsigned integer type annotations
	@Test
	void letToUint8tWithTypeAnnotation() {
		assertValid("let x : U8 = 100;", "uint8_t x = 100;");
	}
	
	@Test
	void letToUint16tWithTypeAnnotation() {
		assertValid("let x : U16 = 100;", "uint16_t x = 100;");
	}
	
	@Test
	void letToUint32tWithTypeAnnotation() {
		assertValid("let x : U32 = 100;", "uint32_t x = 100;");
	}
	
	@Test
	void letToUint64tWithTypeAnnotation() {
		assertValid("let x : U64 = 100;", "uint64_t x = 100;");
	}
	
	// Tests for unsigned integer suffixes
	@Test
	void letToUint8tWithSuffix() {
		assertValid("let x = 10U8;", "uint8_t x = 10;");
	}
	
	@Test
	void letToUint16tWithSuffix() {
		assertValid("let x = 10U16;", "uint16_t x = 10;");
	}
	
	@Test
	void letToUint32tWithSuffix() {
		assertValid("let x = 10U32;", "uint32_t x = 10;");
	}
	
	@Test
	void letToUint64tWithSuffix() {
		assertValid("let x = 10U64;", "uint64_t x = 10;");
	}
	
	// Test for suffix precedence over type annotation
	@Test
	void letWithMixedTypeAnnotationAndSuffix() {
		assertValid("let x : U8 = 100I16;", "int16_t x = 100;");
	}
}