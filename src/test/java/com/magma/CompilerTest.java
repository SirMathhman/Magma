package com.magma;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for StringProcessor using JUnit 5
 */
public class CompilerTest {
	@Test
	public void empty() {
		assertValid("", "");
	}

	@ParameterizedTest
	@ValueSource(strings = {"x", "y", "z"})
	void letName(String name) {
		assertValid("let " + name + " = 0", "int32_t " + name + " = 0;");
	}

	@ParameterizedTest
	@ValueSource(strings = {"0", "42", "-1", "100"})
	void letValues(String value) {
		assertValid("let x = " + value, "int32_t x = " + value + ";");
	}

	@Test
	void letTypeI8() {
		assertValid("let x : I8 = 0;", "int8_t x = 0;");
	}

	@Test
	void letTypeI16() {
		assertValid("let x : I16 = 0;", "int16_t x = 0;");
	}

	@Test
	void letTypeI32() {
		assertValid("let x : I32 = 0;", "int32_t x = 0;");
	}

	@Test
	void letTypeI64() {
		assertValid("let x : I64 = 0;", "int64_t x = 0;");
	}

	@Test
	void letTypeU8() {
		assertValid("let x : U8 = 0;", "uint8_t x = 0;");
	}

	@Test
	void letTypeU16() {
		assertValid("let x : U16 = 0;", "uint16_t x = 0;");
	}

	@Test
	void letTypeU32() {
		assertValid("let x : U32 = 0;", "uint32_t x = 0;");
	}

	@Test
	void letTypeU64() {
		assertValid("let x : U64 = 0;", "uint64_t x = 0;");
	}
	
	@Test
	void extraWhitespaceAroundLet() {
		assertValid("  let x = 0  ", "int32_t x = 0;");
	}
	
	@Test
	void extraWhitespaceAroundEquals() {
		assertValid("let x  =  0", "int32_t x = 0;");
	}
	
	@Test
	void extraWhitespaceAroundTypeAnnotation() {
		assertValid("let x  :  I32  = 0", "int32_t x = 0;");
	}
	
	@Test
	void multipleWhitespaces() {
		assertValid("let    x    =    0", "int32_t x = 0;");
	}

	private void assertValid(String input, String output) {
		String actual = Compiler.process(input);
		assertEquals(output, actual);
	}
}