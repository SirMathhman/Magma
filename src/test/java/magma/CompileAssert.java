package magma;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CompileAssert {
	private static final Compiler compiler = new Compiler();

	static void assertInvalid(String input) {
		assertThrows(CompileException.class, () -> compiler.compile(input));
	}

	/**
	 * Helper method to validate that the input is correctly transformed to the expected output.
	 *
	 * @param input  the input string to transform
	 * @param output the expected output after transformation
	 */
	static void assertValid(String input, String output) {
		String actual = compiler.compile(input);
		assertEquals(output, actual);
	}
}
