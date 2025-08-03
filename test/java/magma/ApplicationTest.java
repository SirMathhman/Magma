package magma;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ApplicationTest {
	@ParameterizedTest
	@ValueSource(strings = {"5", "10"})
	void number(String value) throws ApplicationException {
		assertEquals(value, Application.run(value));
	}

	@ParameterizedTest
	@ValueSource(strings = {"first", "second"})
	void test(String value) {
		assertThrows(ApplicationException.class, () -> Application.run(value));
	}
}
