import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.lang.InterruptedException;

public class MainTest {
	@Test
	void testProcessCProgramWithEmptyInput() throws IOException, InterruptedException {
		// Test with empty input
		String emptyInput = "";
		String output = Main.processCProgram(emptyInput);
		
		// Verify that the output contains the expected C program output
		assertTrue(output.contains("Hello from C program! Current execution successful."), 
			"C program should output the expected message when input is empty");
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