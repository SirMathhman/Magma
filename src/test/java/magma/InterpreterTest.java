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

	@Test
	void lessThan() {
		assertValid("1 < 2", "true");
	}

	@Test
	void whileTest() {
		assertValid("let mut i = 0; while (i < 10) i++; i", "10");
	}

	@Test
	void addAssign() {
		assertValid("let mut x = 0; x += 5; x", "5");
	}

	@Test
	void forTest() {
		assertValid("let mut sum = 0; for(let mut i = 0; i < 10; i++) sum += i; sum", "45");
	}

	@Test
	void function() {
		assertValid("fn get() => 100; get()", "100");
	}

	@Test
	void struct() {
		assertValid("struct Container { value : I32 } let container = Container { 100 }; container.value", "100");
	}

	@Test
	void equalsTest() {
		assertValid("5 == 5", "true");
	}
}