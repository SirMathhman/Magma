package magma;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

	/**
	 * Assert that running the given input produces the expected output.
	 * This is an overloaded method that calls assertRun with an empty list of arguments.
	 *
	 * @param input  The input to process
	 * @param output The expected output
	 */
	private void assertRun(String input, String output) {
		assertRun(input, output, Collections.emptyList());
	}

	/**
	 * Assert that running the given input with the specified process arguments produces the expected output.
	 *
	 * @param input  The input to process
	 * @param output The expected output
	 * @param args   The arguments to pass to the C program
	 */
	private void assertRun(String input, String output, List<String> args) {
		try {
			// Test with the given input and arguments
			// Verify that the output contains the expected C program output
			assertEquals(output, Main.processCProgram(input, args));
		} catch (IOException | InterruptedException e) {
			fail(e);
		}
	}

	@Test
	void testProcessCProgramWithNonEmptyInput() {
		// Test with non-empty input
		String nonEmptyInput = "Some content";

		// Verify that an exception is thrown for non-empty input
		Exception exception = assertThrows(IOException.class, () -> Main.processCProgram(nonEmptyInput),
																			 "An IOException should be thrown when input is not empty");

		// Verify the exception message
		assertEquals("Input file is not empty. Cannot proceed.", exception.getMessage(),
								 "Exception message should indicate that input file is not empty");
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
		} catch (IOException | InterruptedException e) {
			fail("Should not throw an exception when passing arguments to the C program: " + e.getMessage());
		}
	}
}