package magma;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class ApplicationTest {
	@ParameterizedTest
	@ValueSource(strings = {"5", "10"})
	void number(String value) {
		assertValid(value);
	}

	@Test
	void invalid() {
		assertThrows(ApplicationException.class, () -> Application.run("test"));
	}

	private void assertValid(String value) {
		try {
			assertEquals(value, Application.run(value));
		} catch (ApplicationException e) {
			fail(e);
		}
	}
}
