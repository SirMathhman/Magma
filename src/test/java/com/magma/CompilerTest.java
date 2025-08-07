package com.magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Test class for StringProcessor using JUnit 5
 */
public class CompilerTest {
	private final Compiler processor = new Compiler();

	@Test
	public void testEmptyString() {
		// Test with an empty string
		String input = "";
		String result = processor.process(input);
		assertEquals(input, result, "Empty string should be returned unchanged");
	}
}