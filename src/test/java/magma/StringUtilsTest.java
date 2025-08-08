package magma;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for the StringUtils class.
 * Verifies the conversion of JavaScript and TypeScript syntax to C syntax.
 */
public class StringUtilsTest {

	private StringUtils stringUtils;
	
	@BeforeEach
	public void setUp() {
		// Create a new instance of StringUtils before each test
		stringUtils = new StringUtils();
	}

	@Test
	public void shouldReturnSameString() {
		// Arrange
		String input = "hello";
		assertValid(input, input);
	}

	@Test
	public void shouldConvertJavaScriptLetToC() {
		assertValid("let x = 0;", "int32_t x = 0;");
	}
	
	@Test
	public void shouldConvertJavaScriptLetWithTestVariableToC() {
		assertValid("let test = 0;", "int32_t test = 0;");
	}
	
	@Test
	public void shouldConvertTypeScriptTypedVariableToC() {
		assertValid("let x : I32 = 0;", "int32_t x = 0;");
	}

	/**
	 * Helper method to validate that the input is correctly transformed to the expected output.
	 * 
	 * @param input the input string to transform
	 * @param output the expected output after transformation
	 */
	private void assertValid(String input, String output) {
		String actual = stringUtils.echo(input);
		assertEquals(output, actual);
	}
}