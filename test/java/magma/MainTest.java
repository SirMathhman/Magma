package magma;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static magma.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

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

	@Test
	void testProcessCProgramWithNonEmptyInput() {
		assertRunFail("Some content");
	}

	@Test
	void invalidType() {
		assertRunFail("100.0U8");
	}

	@ParameterizedTest
	@ValueSource(strings = {"first", "second"})
	void argumentName(String name) {
		assertRunWithArguments("require(" + name + " : **char); name", "100", List.of("100"));
	}

	@ParameterizedTest
	@ValueSource(strings = {"200", "300"})
	void argumentValue(String value) {
		assertRunWithArguments("require(test : **char); test", value, List.of(value));
	}

	@Test
	void argumentsLength() {
		assertRunWithArguments("require(args : **char); args.length", "2", List.of("Hello", "World"));
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