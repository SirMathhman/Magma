package magma.math;

import static magma.infra.TestUtils.assertValidWithPrelude;

import org.junit.jupiter.api.Test;

public class ArithmeticTest {
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