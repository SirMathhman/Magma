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
	void letType() {
		assertValid("let x : I32 = 0;", "int32_t x = 0;");
	}

	private void assertValid(String input, String output) {
		String actual = Compiler.process(input);
		assertEquals(output, actual);
	}
}