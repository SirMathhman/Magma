package magma;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MainTest {
	@ParameterizedTest
	@ValueSource(strings = {"-10", "-1", "0", "1", "10"})
	void digit(String input) {
		assertEquals(input, Main.run(input));
	}
}
