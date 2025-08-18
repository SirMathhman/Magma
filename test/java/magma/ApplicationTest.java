package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApplicationTest {
	@Test
	void noBehavior() {
		assertValid("", 0);
	}

	@Test
	void integer() {
		assertValid("5", 5);
	}

	@Test
	void add() {
		assertValid("2 + 3", 5);
	}

	@Test
	void subtract() {
		assertValid("5 - 3", 2);
	}

	@Test
	void multiply() {
		assertValid("2 * 3", 6);
	}

	@Test
	void copy() {
		assertValid("external fn readInt() : I32; readInt()", "5", 5);
	}

	private void assertValid(String input, int exitCode) {
		assertValid(input, "", exitCode);
	}

	private void assertValid(String input, String stdIn, int exitCode) {
		assertEquals(exitCode, Application.run(input, stdIn));
	}
}
