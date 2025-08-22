package magma;// ...imports moved to TestUtils when needed

import org.junit.jupiter.api.Test;

import static magma.TestUtils.*;

public class BasicTest {
	@Test
	public void undefined() {
		assertInvalid("readInt");
	}

	@Test
	void integer() {
		assertValid("0", "", 0);
	}

	@Test
	void pass() {
		assertValidWithPrelude("readInt()", "100", 100);
	}

	@Test
	void add() {
		assertValidWithPrelude("readInt() + readInt()", "100\r\n200", 300);
	}

	@Test
	void subtract() {
		assertValidWithPrelude("readInt() - readInt()", "n200\r\n100", 100);
	}

	@Test
	void multiply() {
		assertValidWithPrelude("readInt() * readInt()", "100\r\n200", 20000);
	}

	@Test
	void let() {
		assertValidWithPrelude("let x = readInt(); x", "100", 100);
	}
}
