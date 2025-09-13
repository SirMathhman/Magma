package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InterpreterTest {
	@Test
	void empty() {
		assertEquals("", Interpreter.interpret(""));
	}

	@Test
	void integer() {
		assertEquals("1", Interpreter.interpret("1"));
	}
}