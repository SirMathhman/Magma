package magma;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MainTest {
	@ParameterizedTest
	@ValueSource(strings = {"5", "10"})
	void number(String value) {
		assertValid(value);
	}

	private void assertValid(String value) {
		assertEquals(value, Main.run(value));
	}
}
