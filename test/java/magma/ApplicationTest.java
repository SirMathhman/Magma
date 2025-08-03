package magma;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ApplicationTest {
	@Test
	void invalid() {
		assertThrows(ApplicationException.class, () -> Application.run("?"));
	}

	@ParameterizedTest
	@ValueSource(strings = {"100", "200"})
	void digit(String value) throws ApplicationException {
		assertEquals(value, Application.run(value));
	}
}