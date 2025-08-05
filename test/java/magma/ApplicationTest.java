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

	private void assertValid(String type) {
		try {
			var value = (int) (Math.random() * 0x1000);
			final var input = String.valueOf(value);
			final var output = Application.run(input + type);
			assertEquals(output, input);
		} catch (ApplicationException e) {
			fail(e);
		}
	}
}
