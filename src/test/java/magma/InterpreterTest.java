package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InterpreterTest {
	@Test
	void empty() {
		assertInterpretsTo("", "");
	}

	@Test
	void undefined() {
		final var interpreter = new Interpreter();
		assertInstanceOf(Err.class, interpreter.interpret("test"));
	}

	@Test
	void numberLiteral() {
		assertInterpretsTo("5", "5");
	}

	private static void assertInterpretsTo(String input, String expected) {
		switch (new Interpreter().interpret(input)) {
			case Ok<String, InterpretError>(String value) -> assertEquals(expected, value);
			case Err<String, InterpretError>(InterpretError error) -> fail(error.display());
		}
	}
}
