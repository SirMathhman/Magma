package magma;

import org.junit.jupiter.api.Test;

import static magma.TestUtils.assertInvalid;
import static magma.TestUtils.assertValid;

class TypeSystemTest {
	@Test
	void letWithSignedIntegerTypes() {
		// Test I8
		assertValid("let a : I8 = 10;", "int8_t a = 10;");

		// Test I16
		assertValid("let b : I16 = 1000;", "int16_t b = 1000;");

		// Test I64
		assertValid("let c : I64 = 9223372036854775807;", "int64_t c = 9223372036854775807;");
	}

	@Test
	void letWithUnsignedIntegerAndBooleanTypes() {
		// Test U8
		assertValid("let d : U8 = 255;", "uint8_t d = 255;");

		// Test U16
		assertValid("let e : U16 = 65535;", "uint16_t e = 65535;");

		// Test U32
		assertValid("let f : U32 = 4294967295;", "uint32_t f = 4294967295;");

		// Test U64
		assertValid("let g : U64 = 18446744073709551615;", "uint64_t g = 18446744073709551615;");

		// Test Bool with explicit type annotation
		assertValid("let h : Bool = true;", "bool h = true;");
		assertValid("let i : Bool = false;", "bool i = false;");
		assertValid("let   j  :  Bool  =  true;", "bool j = true;");

		// Test Bool with implicit type annotation
		assertValid("let k = true;", "bool k = true;");
		assertValid("let l = false;", "bool l = false;");

		// Test invalid boolean values
		assertInvalid("let m : Bool = 42;");
		assertInvalid("let n : Bool = \"true\";");
	}

	@Test
	void letWithIntegerTypeSuffixes() {
		// Test U64 suffix
		assertValid("let x = 100U64;", "uint64_t x = 100;");

		// Test other unsigned integer suffixes
		assertValid("let a = 255U8;", "uint8_t a = 255;");
		assertValid("let b = 65535U16;", "uint16_t b = 65535;");
		assertValid("let c = 4294967295U32;", "uint32_t c = 4294967295;");

		// Test signed integer suffixes
		assertValid("let d = 127I8;", "int8_t d = 127;");
		assertValid("let e = 32767I16;", "int16_t e = 32767;");
		assertValid("let f = 2147483647I32;", "int32_t f = 2147483647;");
		assertValid("let g = 9223372036854775807I64;", "int64_t g = 9223372036854775807;");
	}

	@Test
	void letWithCharacterLiterals() {
		// Test character literals with explicit U8 type annotation
		assertValid("let a : U8 = 'a';", "uint8_t a = 97;");
		assertValid("let b : U8 = 'Z';", "uint8_t b = 90;");
		assertValid("let c : U8 = '0';", "uint8_t c = 48;");
		assertValid("let d : U8 = ' ';", "uint8_t d = 32;");
		assertValid("let   e  :  U8  =  'x';", "uint8_t e = 120;");

		// Test character literals without explicit type annotation (should default to U8)
		assertValid("let f = 'a';", "uint8_t f = 97;");
		assertValid("let g = 'Z';", "uint8_t g = 90;");
		assertValid("let h = '0';", "uint8_t h = 48;");
		assertValid("let i = ' ';", "uint8_t i = 32;");
		assertValid("let   j  =  'x';", "uint8_t j = 120;");
	}

	@Test
	void invalidCharacterLiteralUsage() {
		// Character literals are only allowed with U8 type
		assertInvalid("let a : I8 = 'a';");
		assertInvalid("let b : U16 = 'b';");
		assertInvalid("let c : I32 = 'c';");
		assertInvalid("let d : U64 = 'd';");
		assertInvalid("let e : Bool = 'e';");
	}

	@Test
	void invalidTypeMismatch() {
		// Test the specific case mentioned in the issue description
		assertInvalid("let x : U16 = 200I32;");

		// Test other incompatible type combinations
		assertInvalid("let y : I8 = 10U8;");
		assertInvalid("let z : U32 = 1000I16;");
		assertInvalid("let w : I64 = 9223372036854775807U64;");
	}
}