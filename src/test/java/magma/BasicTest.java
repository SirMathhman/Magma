package magma;

import org.junit.jupiter.api.Test;

import static magma.TestUtils.*;

public class BasicTest {
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
		assertValidWithPrelude("readInt() - readInt()", "100\r\n200", -100);
	}

	@Test
	void multiply() {
		assertValidWithPrelude("readInt() * readInt()", "100\r\n200", 20000);
	}
}