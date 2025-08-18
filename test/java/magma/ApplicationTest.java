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
	void copy() {
		assertValidWithPrelude("readInt()", "2", 2);
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

	@Test
	void testTrue() {
		assertValidWithPrelude("readInt() == 100", "100", 1);
	}

	@Test
	void testFalse() {
		assertValidWithPrelude("readInt() == 100", "99", 0);
	}

	@Test
	void reference() {
		assertValidWithPrelude("let x = readInt(); &x != 0", "100", 1);
	}

	@Test
	void deference() {
		assertValidWithPrelude("let x = readInt(); let y = &x; *y", "100", 100);
	}

	@Test
	void multipleLet() {
		assertValidWithPrelude("let x = readInt(); let y = readInt(); x + y", "5\r\n7", 12);
	}
	
	@Test
	void testIfElseTrue() {
		assertValidWithPrelude("if(readInt() == 10){5}else{7}", "10", 5);
	}

	@Test
	void testIfElseFalse() {
		assertValidWithPrelude("if(readInt() == 10){5}else{7}", "9", 7);
	}

	private void assertValid(String input, int exitCode) {
		assertValid(input, "", exitCode);
	}

	private void assertValid(String input, String stdIn, int exitCode) {
		assertEquals(exitCode, Application.run(input, stdIn));
	}
}
