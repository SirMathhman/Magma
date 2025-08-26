package magma;

import org.junit.jupiter.api.Test;

import static magma.TestUtils.assertInvalid;
import static magma.TestUtils.assertValid;

class InterpreterTest {
	@Test
	void valid() {
		assertValid("5", "5");
	}

	@Test
	void invalid() {
		assertInvalid("test");
	}
}