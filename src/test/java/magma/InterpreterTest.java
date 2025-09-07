package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class InterpreterTest {
	@Test
	public void empty() {
		final var interpreter = new Interpreter();
		final var result = interpreter.interpret("");
		switch (result) {
			case Ok<String, InterpretError>(String value) -> assertEquals("", value);
			case Err<String, InterpretError>(InterpretError error) -> fail(error.display());
		}
	}
}
