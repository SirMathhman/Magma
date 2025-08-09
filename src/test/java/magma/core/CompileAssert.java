package magma.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CompileAssert {
	// Create a new compiler instance for each test to avoid state issues
	public static void assertInvalid(String input) {
		Compiler compiler = new Compiler();
		assertThrows(CompileException.class, () -> compiler.compile(input));
	}

	/**
	 * Helper method to validate that the input is correctly transformed to the expected output.
	 *
	 * @param input  the input string to transform
	 * @param output the expected output after transformation
	 */
	public static void assertValid(String input, String output) {
		Compiler compiler = new Compiler();
		System.out.println("[DEBUG_LOG] Testing input: " + input);
		String actual = compiler.compile(input);
		System.out.println("[DEBUG_LOG] Actual output: " + actual);
		System.out.println("[DEBUG_LOG] Expected output: " + output);
		assertEquals(output, actual);
	}
}
