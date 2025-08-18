package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApplicationTest {
	@Test
	void classTest() {
		assertValid("class fn Empty() => {}", 0);
	}

	private void assertValid(String input, int exitCode) {
		assertValid(input, "", exitCode);
	}

	private void assertValid(String input, String stdIn, int exitCode) {
		assertEquals(exitCode, Application.run(input, stdIn));
	}
}
