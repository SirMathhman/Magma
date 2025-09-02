package magma.feature;

import org.junit.jupiter.api.Test;

import static magma.TestUtils.assertAllValidWithPrelude;

public class ArithmeticTest {
	@Test
	void add() {
		assertAllValidWithPrelude("readInt() + readInt()", "10\r\n20", "30");
	}

	@Test
	void subtract() {
		assertAllValidWithPrelude("readInt() - readInt()", "10\r\n20", "-10");
	}

	@Test
	void multiply() {
		assertAllValidWithPrelude("readInt() * readInt()", "10\r\n20", "200");
	}

	@Test
	void divide() {
		assertAllValidWithPrelude("readInt() / readInt()", "20\r\n10", "2");
	}
}
