package magma;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class MainTest {
	@Test
	void empty() {
		assertRun("", "");
	}

	@ParameterizedTest
	@ValueSource(strings = {"100", "200", "300"})
	void number(String value) {
		assertRun(value, value);
	}

	@ParameterizedTest
	@ValueSource(strings = {"U8", "U16", "U32", "U64", "I8", "I16", "I32", "I64"})
	void typedNumber(String value) {
		assertRun("100" + value, "100");
	}

	@ParameterizedTest
	@ValueSource(strings = {"name", "otherName", "anotherName"})
	void let(String name) {
		assertRun("let " + name + " = 100; " + name, "100");
	}

	@ParameterizedTest
	@ValueSource(strings = {"F32", "F64"})
	void typedFloat(String value) {
		assertRun("100.0" + value, "100.0");
	}

	@ParameterizedTest
	@ValueSource(strings = {"F32", "F64"})
	void typedFloatWithoutDecimal(String value) {
		assertRun("100" + value, "100.0");
	}

	@ParameterizedTest
	@ValueSource(strings = {"0.5", "-0.5", "1.0"})
	void floating(String value) {
		assertRun(value, value);
	}

	@ParameterizedTest
	@ValueSource(strings = {"a", "b", "c"})
	void character(String value) {
		assertRun("'" + value + "'", value);
	}

	@ParameterizedTest
	@ValueSource(strings = {"first", "second", "third"})
	void strings(String value) {
		assertRun("\"" + value + "\"", value);
	}

	private void assertRun(String input, String output) {
		try {
			// Test with empty input
			// Verify that the output contains the expected C program output
			assertEquals(output, Main.processCProgram(input));
		} catch (IOException | InterruptedException e) {
			fail(e);
		}
	}

	@Test
	void testProcessCProgramWithNonEmptyInput() {
		// Test with non-empty input
		String nonEmptyInput = "Some content";

		// Verify that an exception is thrown for non-empty input
		Exception exception = assertThrows(IOException.class, () -> {
			Main.processCProgram(nonEmptyInput);
		}, "An IOException should be thrown when input is not empty");

		// Verify the exception message
		assertEquals("Input file is not empty. Cannot proceed.", exception.getMessage(),
								 "Exception message should indicate that input file is not empty");
	}
}