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

	@Test
	void let() {
		assertValid("let x = 10; x", "10");
	}

	@Test
	void assign() {
		assertValid("let mut x = 0; x = 20; x", "20");
	}

	@Test
	void braces() {
		assertValid("{5}", "5");
	}

	@Test
	void trueTest() {
		assertValid("true", "true");
	}

	@Test
	void falseTest() {
		assertValid("false", "false");
	}

	@Test
	void ifTrue() {
		assertValid("if (true) 5 else 3", "5");
	}

	@Test
	void ifFalse() {
		assertValid("if (false) 5 else 3", "3");
	}

	@Test
	void postIncrement() {
		assertValid("let mut x = 0; x++; x", "1");
	}
}