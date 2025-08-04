package magma;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestUtils {
	/**
	 * Assert that running the given input produces the expected output.
	 * This is an overloaded method that calls assertRun with an empty list of arguments.
	 *
	 * @param input  The input to process
	 * @param output The expected output
	 */
	static void assertRun(String input, String output) {
		assertRunWithArguments(input, output, Collections.emptyList());
	}

	/**
	 * Assert that running the given input with the specified process arguments produces the expected output.
	 *
	 * @param input  The input to process
	 * @param output The expected output
	 * @param args   The arguments to pass to the C program
	 */
	static void assertRunWithArguments(String input, String output, List<String> args) {
		try {
			// Test with the given input and arguments
			// Verify that the output contains the expected C program output
			assertEquals(output, Main.processCProgram(input, args));
		} catch (IOException | InterruptedException | CompileException e) {
			fail(e);
		}
	}

	static void assertRunFail(String input) {
		assertThrows(CompileException.class, () -> Main.processCProgram(input));
	}
}