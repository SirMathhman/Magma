package magma;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Make the tests pass. Do not add more tests.
public class MainTest {
	@ParameterizedTest
	@ValueSource(strings = {"-10", "-1", "0", "1", "10"})
	void digit(String input) {
		assertRun(input, input);
	}

	@Test
	void add() {
		assertRun("1 + 2", "3");
	}

	@Test
	void subtract() {
		assertRun("2 - 1", "1");
	}

	private void assertRun(String input, String output) {
		assertEquals(output, Main.run(input));
	}
}
