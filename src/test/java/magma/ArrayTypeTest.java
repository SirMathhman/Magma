package magma;

import org.junit.jupiter.api.Test;

import static magma.TestUtils.assertInvalid;
import static magma.TestUtils.assertValid;

class ArrayTypeTest {
	@Test
	void letWithArrayTypeAndStringLiteral() {
		// Test U8 array with string literal
		assertValid("let x : [U8; 5] = \"hello\";", "uint8_t x[5] = {104, 101, 108, 108, 111};");
		assertValid("let msg : [U8; 3] = \"abc\";", "uint8_t msg[3] = {97, 98, 99};");
		assertValid("let   spaces  :  [U8; 2]  =  \"xy\";", "uint8_t spaces[2] = {120, 121};");
	}

	@Test
	void invalidArrayTypeUsage() {
		// String literals are only allowed with U8 arrays
		assertInvalid("let x : [I8; 5] = \"hello\";");
		assertInvalid("let x : [U16; 5] = \"hello\";");
		assertInvalid("let x : [I32; 5] = \"hello\";");
		assertInvalid("let x : [U64; 5] = \"hello\";");
		assertInvalid("let x : [Bool; 5] = \"hello\";");

		// String length must match array size
		assertInvalid("let x : [U8; 4] = \"hello\";");
		assertInvalid("let x : [U8; 6] = \"hello\";");
		assertInvalid("let x : [U8; 0] = \"hello\";");
	}
}