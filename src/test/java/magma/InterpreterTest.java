package magma;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InterpreterTest {
	@Test
	public void empty() {
		Interpreter interpreter = new Interpreter();
		String input = "";
		Result<String, InvalidInputException> result = interpreter.interpret(input);
		if (result instanceof Ok ok) {
			assertEquals(input, ok.value());
		} else {
			fail("Expected Ok but got: " + result.getClass().getSimpleName());
		}
	}

	@Test
	public void interpretFive() {
		Interpreter interpreter = new Interpreter();
		String input = "5";
		Result<String, InvalidInputException> result = interpreter.interpret(input);
		if (result instanceof Ok ok) {
			assertEquals(input, ok.value());
		} else {
			fail("Expected Ok but got: " + result.getClass().getSimpleName());
		}
	}

	@Test
	public void interpretI32() {
		Interpreter interpreter = new Interpreter();
		String input = "5I32";
		String expected = "5";
		Result<String, InvalidInputException> result = interpreter.interpret(input);
		if (result instanceof Ok ok) {
			assertEquals(expected, ok.value());
		} else {
			fail("Expected Ok but got: " + result.getClass().getSimpleName());
		}
	}

	@Test
	public void interpretTestThrows() {
		Interpreter interpreter = new Interpreter();
		Result<String, InvalidInputException> result = interpreter.interpret("test");
		if (result instanceof Err rawErr) {
			Object e = rawErr.error();
			if (e instanceof InvalidInputException iie) {
				assertEquals("'test' is not a valid input", iie.getMessage());
			} else {
				fail("Expected InvalidInputException inside Err, got: " + e.getClass().getSimpleName());
			}
		} else {
			fail("Expected Err but got: " + result.getClass().getSimpleName());
		}
	}
}
