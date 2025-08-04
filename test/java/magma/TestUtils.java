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
	 * @param source The input to process
	 * @param output The expected output
	 */
	static void assertRun(String source, String output) {
		assertRunWithArguments(source, Collections.emptyList(), output);
	}

	/**
	 * Assert that running the given input with the specified process arguments produces the expected output.
	 *
	 * @param source The input to process
	 * @param input  The input to process
	 * @param output The expected output
	 */
	static void assertRunWithArguments(String source, List<String> input, String output) {
		try {
			// Test with the given input and arguments
			// Verify that the output contains the expected C program output
			assertEquals(output, Main.processCProgram(source, input));
		} catch (IOException | InterruptedException | CompileException e) {
			fail(e);
		}
	}

	static void assertRunFail(String input) {
		assertThrows(CompileException.class, () -> Main.processCProgram(input));
	}
}