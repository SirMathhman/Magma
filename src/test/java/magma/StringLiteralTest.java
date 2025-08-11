package magma;

import org.junit.jupiter.api.Test;

class StringLiteralTest extends CompilerTestBase {
	@Test
	void stringsEmpty() {
		assertValid("let empty : [U8; 0] = \"\";", "uint8_t empty[0] = \"\";");
	}

	@Test
	void stringsWithSpaces() {
		assertValid("let message : [U8; 11] = \"Hello World\";", "uint8_t message[11] = \"Hello World\";");
	}

	@Test
	void stringsWithSpecialChars() {
		assertValid("let special : [U8; 3] = \"\\n\\t\";", "uint8_t special[3] = \"\\n\\t\";");
	}

	@Test
	void stringsMutable() {
		assertValid("let mut buffer : [U8; 10] = \"test\";", "uint8_t buffer[10] = \"test\";");
	}

	@Test
	void stringsSingleChar() {
		assertValid("let ch : [U8; 1] = \"a\";", "uint8_t ch[1] = \"a\";");
	}

	@Test
	void stringsLengthMismatch() {
		// Test what happens when string length doesn't match array size
		// This should either validate or pass through to C compiler to catch
		assertValid("let mismatch : [U8; 3] = \"hello\";", "uint8_t mismatch[3] = \"hello\";");
	}

	@Test
	void stringsWithDifferentTypes() {
		// Test string literals with different array types
		assertValid("let i8_str : [I8; 4] = \"test\";", "int8_t i8_str[4] = \"test\";");
	}

}