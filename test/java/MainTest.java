import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class MainTest {
	@Test
	void empty() {
		assertRun("", "");
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