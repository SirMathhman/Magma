package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InterpreterTest {
	@Test
	void valid() {
		Interpreter.interpret("5").consume(result -> assertEquals("5", result), error -> fail(error.display()));
	}

	@Test
	void invalid() {
		assertTrue(Interpreter.interpret("test").isErr());
	}
}