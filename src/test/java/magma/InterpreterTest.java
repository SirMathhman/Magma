package magma;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class InterpreterTest {
	@Test
	void valid() {
		assertValid("5", "5");
	}

	@Test
	void integerSuffix() {
		assertValid("5I32", "5");
	}

	private void assertValid(String input, String output) {
		Interpreter.interpret(input)
				.consume(result -> Assertions.assertEquals(output, result), error -> Assertions.fail(error.display()));
	}

	@Test
	void invalid() {
		assertInvalid("test");
	}

	private void assertInvalid(String input) {
		assertTrue(Interpreter.interpret(input).isErr());
	}
}