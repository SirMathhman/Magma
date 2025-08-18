package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApplicationTest {
	public static final String PRELUDE = "external fn readInt() : I32; ";

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
		assertValidWithPrelude("readInt() + 3", "5", 8);
	}

	@Test
	void subtract() {
		assertValidWithPrelude("readInt() - 3", "5", 2);
	}

	private void assertValidWithPrelude(String input, String stdIn, int exitCode) {
		assertValid(PRELUDE + input, stdIn, exitCode);
	}

	@Test
	void multiply() {
		assertValidWithPrelude("readInt() * 3", "2", 6);
	}

	@Test
	void callTwice() {
		assertValidWithPrelude("readInt() + readInt()", "3\r\n4", 7);
	}

	@Test
	void braces() {
		assertValid("{}", 0);
	}

	@Test
	void bracesWithInt() {
		assertValidWithPrelude("{readInt()}", "5", 5);
	}

	@Test
	void let() {
		assertValidWithPrelude("let x = readInt(); x + 3", "5", 8);
	}

	private void assertValid(String input, int exitCode) {
		assertValid(input, "", exitCode);
	}

	private void assertValid(String input, String stdIn, int exitCode) {
		assertEquals(exitCode, Application.run(input, stdIn));
	}
}
