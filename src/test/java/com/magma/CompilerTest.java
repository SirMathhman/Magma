package com.magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for StringProcessor using JUnit 5
 */
public class CompilerTest {
	@Test
	public void testEmptyString() {
		assertValid("", "");
	}

	@Test
	void test() {
		assertValid("let x = 0", "int32_t x = 0;");
	}

	private void assertValid(String input, String output) {
		String actual = Compiler.process(input);
		assertEquals(output, actual);
	}
}