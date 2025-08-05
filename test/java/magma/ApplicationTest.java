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
		var value = String.valueOf((int) (Math.random() * 0x1000));
		assertValid(value, value);
	}

	private void assertValid(String input, String output) {
		try {
			assertEquals(output, Application.run(input));
		} catch (ApplicationException e) {
			fail(e);
		}
	}

	@ParameterizedTest
	@ValueSource(strings = {"U8", "U16", "U32", "U64", "I8", "I16", "I32", "I64"})
	void integerTyped(String type) {
		var value = String.valueOf((int) (Math.random() * 0x1000));
		assertValid(value + type, value);
	}

	@Test
	void testFalse() {
		assertValid("false", "0");
	}

	@Test
	void testTrue() {
		assertValid("true", "1");
	}
}
