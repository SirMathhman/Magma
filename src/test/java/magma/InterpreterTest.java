package magma;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InterpreterTest {
	@Test
	public void empty() {
		assertValid("", "");
	}

	@Test
	public void interpretFive() {
		assertValid("5", "5");
	}

	@Test
	public void interpretI32() {
		assertValid("5I32", "5");
	}

	@Test
	public void interpretTestThrows() {
		Interpreter interpreter = new Interpreter();
		Result<String, InvalidInputException> result = interpreter.interpret("test");
		if (result instanceof Err rawErr) {
			var e = rawErr.error();
			if (e instanceof InvalidInputException iie) {
				assertEquals("'test' is not a valid input", iie.getMessage());
			} else {
				fail("Expected InvalidInputException inside Err, got: " + e.getClass().getSimpleName());
			}
		} else {
			fail("Expected Err but got: " + result.getClass().getSimpleName());
		}
	}

	private void assertValid(String input, String expected) {
		Interpreter interpreter = new Interpreter();
		Result<String, InvalidInputException> result = interpreter.interpret(input);
		if (result instanceof Ok ok) {
			assertEquals(expected, ok.value());
		} else {
			fail("Expected Ok but got: " + result.getClass().getSimpleName());
		}
	}
}
