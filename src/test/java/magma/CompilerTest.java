package magma;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test class for StringProcessor.
 */
public class CompilerTest {

	/**
	 * Test that the process method throws an UnsupportedOperationException.
	 */
	@Test
	@DisplayName("process() should throw UnsupportedOperationException")
	public void testProcessThrowsException() {
		Compiler processor = new Compiler();

		assertThrows(UnsupportedOperationException.class, () -> {
			processor.process("test input");
		});
	}

	/**
	 * Parameterized test to verify that the process method throws an
	 * UnsupportedOperationException for non-empty inputs.
	 *
	 * @param input The input string to test
	 */
	@ParameterizedTest
	@ValueSource(strings = {"hello", "123", "special!@#"})
	@DisplayName("process() should throw UnsupportedOperationException for non-empty inputs")
	public void testProcessThrowsExceptionForNonEmptyInputs(String input) {
		Compiler processor = new Compiler();

		assertThrows(UnsupportedOperationException.class, () -> {
			processor.process(input);
		});
	}

	/**
	 * Test that the process method returns an empty string when the input is empty.
	 */
	@Test
	@DisplayName("process() should return empty string for empty input")
	public void testProcessReturnsEmptyStringForEmptyInput() {
		Compiler processor = new Compiler();
		String result = processor.process("");
		assertEquals("", result, "Should return empty string for empty input");
	}


	/**
	 * Test that the process method transforms "let x = 100;" to "int32_t x = 100;".
	 */
	@Test
	@DisplayName("process() should transform 'let x = 100;' to 'int32_t x = 100;'")
	public void testProcessTransformsLetToInt32t() {
		Compiler processor = new Compiler();
		String input = "let x = 100;";
		String expected = "int32_t x = 100;";

		String result = processor.process(input);

		assertEquals(expected, result, "Should transform 'let x = 100;' to 'int32_t x = 100;'");
	}
}