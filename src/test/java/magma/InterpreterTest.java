package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InterpreterTest {
	@Test
	void empty() {
		switch (new Interpreter().interpret("")) {
			case Ok<String, InterpretError>(String value) -> assertEquals("", value);
			case Err<String, InterpretError>(InterpretError error) -> fail(error.display());
		}
	}

	@Test
	void undefined() {
		final var interpreter = new Interpreter();
		assertInstanceOf(Err.class, interpreter.interpret("test"));
	}
}
