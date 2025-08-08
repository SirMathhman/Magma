package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StringUtilsTest {

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

	private void assertValid(String input, String output) {
		String actual = StringUtils.echo(input);
		assertEquals(output, actual);
	}
}