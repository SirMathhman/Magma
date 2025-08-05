package magma;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class ApplicationTest {
	@Test
	void invalid() {
		assertThrows(ApplicationException.class, () -> Application.run("test"));
	}

	@Test
	void integer() {
		assertValid("");
	}

	@ParameterizedTest
	@ValueSource(strings = {"U8", "U16", "U32", "U64", "I8", "I16", "I32", "I64"})
	void integerTyped(String type) {
		assertValid(type);
	}

	@Test
	void testFloat() {
		final var value = String.valueOf(createNumber());
		assertValid(value, value);
	}

	private void assertValid(String type) {
		var value = (int) createNumber();
		final var input = String.valueOf(value);
		assertValid(input, input + type);
	}

	private double createNumber() {
		return Math.random() * 0x1000;
	}

	private void assertValid(String output, String input) {
		try {
			final var expected = Application.run(input);
			assertEquals(expected, output);
		} catch (ApplicationException e) {
			fail(e);
		}
	}
}
