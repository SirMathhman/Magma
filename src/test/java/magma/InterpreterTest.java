package magma;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class InterpreterTest {
	@Test
	void valid() {
		Interpreter.interpret("5")
							 .consume(result -> Assertions.assertEquals("5", result), error -> Assertions.fail(error.display()));
	}

	@Test
	void invalid() {
		assertTrue(Interpreter.interpret("test").isErr());
	}
}