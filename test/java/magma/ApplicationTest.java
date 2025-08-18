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

	private void assertValid(String input, int exitCode) {
		assertEquals(exitCode, Application.run(input));
	}
}
