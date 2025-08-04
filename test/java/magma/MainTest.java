package magma;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MainTest {
	@Test
	void empty() {
		TestUtils.assertRun("", "");
	}

	@ParameterizedTest
	@ValueSource(strings = {"100", "200", "300"})
	void number(String value) {
		TestUtils.assertRun(value, value);
	}

	@ParameterizedTest
	@ValueSource(strings = {"U8", "U16", "U32", "U64", "I8", "I16", "I32", "I64"})
	void typedNumber(String value) {
		TestUtils.assertRun("100" + value, "100");
	}

	@ParameterizedTest
	@ValueSource(strings = {"name", "otherName", "anotherName"})
	void let(String name) {
		TestUtils.assertRun("let " + name + " = 100; " + name, "100");
	}

	@ParameterizedTest
	@ValueSource(strings = {"F32", "F64"})
	void typedFloat(String value) {
		TestUtils.assertRun("100.0" + value, "100.0");
	}

	@ParameterizedTest
	@ValueSource(strings = {"F32", "F64"})
	void typedFloatWithoutDecimal(String value) {
		TestUtils.assertRun("100" + value, "100.0");
	}

	@ParameterizedTest
	@ValueSource(strings = {"0.5", "-0.5", "1.0"})
	void floating(String value) {
		TestUtils.assertRun(value, value);
	}

	@ParameterizedTest
	@ValueSource(strings = {"a", "b", "c"})
	void character(String value) {
		TestUtils.assertRun("'" + value + "'", value);
	}

	@ParameterizedTest
	@ValueSource(strings = {"first", "second", "third"})
	void strings(String value) {
		TestUtils.assertRun("\"" + value + "\"", value);
	}

	@Test
	void testProcessCProgramWithNonEmptyInput() {
		// Test with non-empty input
		String nonEmptyInput = "Some content";

		// Verify that an exception is thrown for non-empty input
		Exception exception = assertThrows(CompileException.class, () -> Main.processCProgram(nonEmptyInput),
																			 "A CompileException should be thrown when input cannot be processed");

		// Verify the exception message
		assertTrue(exception.getMessage().contains("Failed to compile"),
							 "Exception message should indicate a compilation failure");
	}

	@Test
	void testProcessCProgramWithArguments() {
		// Test with empty input and some arguments
		try {
			// Create a list of arguments to pass to the C program
			List<String> args = Arrays.asList("arg1", "arg2", "arg3");

			// Call processCProgram with arguments
			// This should not throw an exception even though the C program doesn't use the arguments
			String output = Main.processCProgram("", args);

			// The output should be empty since the C program doesn't use the arguments
			assertEquals("", output, "Output should be empty for empty input, regardless of arguments");
		} catch (IOException | InterruptedException | CompileException e) {
			fail("Should not throw an exception when passing arguments to the C program: " + e.getMessage());
		}
	}
}