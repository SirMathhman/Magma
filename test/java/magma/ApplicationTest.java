package magma;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationTest {
	@Test
	void invalid() {
		assertInvalid("?");
	}

	private void assertInvalid(String input) {
		assertThrows(ApplicationException.class, () -> Application.run(input));
	}

	@ParameterizedTest
	@ValueSource(strings = {"100", "200"})
	void digit(String value) {
		assertValid(value, value);
	}

	@ParameterizedTest
	@ValueSource(strings = {"U8", "U16", "U32", "U64", "I8", "I16", "I32", "I64"})
	void digitSuffixValid(String type) {
		String value = "100";
		assertValid(value + type, value);
	}

	@Test
	void digitSuffixInvalid() {
		assertInvalid("100?");
	}

	private void assertValid(String input, String output) {
		try {
			assertEquals(output, Application.run(input));
		} catch (ApplicationException e) {
			fail(e);
		}
	}
}