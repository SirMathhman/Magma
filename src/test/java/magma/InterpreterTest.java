package magma;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InterpreterTest {
	@Test
	public void empty() {
		Interpreter interpreter = new Interpreter();
		String input = "";
		Result<String, InvalidInputException> result = interpreter.interpret(input);
		assertTrue(result instanceof Ok);
		assertEquals(input, ((Ok<String, InvalidInputException>) result).value());
	}

	@Test
	public void interpretFive() {
		Interpreter interpreter = new Interpreter();
		String input = "5";
		Result<String, InvalidInputException> result = interpreter.interpret(input);
		assertTrue(result instanceof Ok);
		assertEquals(input, ((Ok<String, InvalidInputException>) result).value());
	}

	@Test
	public void interpretI32() {
		Interpreter interpreter = new Interpreter();
		String input = "5I32";
		String expected = "5";
		Result<String, InvalidInputException> result = interpreter.interpret(input);
		assertTrue(result instanceof Ok);
		assertEquals(expected, ((Ok<String, InvalidInputException>) result).value());
	}

	@Test
	public void interpretTestThrows() {
		Interpreter interpreter = new Interpreter();
		Result<String, InvalidInputException> result = interpreter.interpret("test");
		assertTrue(result instanceof Err);
		InvalidInputException err = ((Err<String, InvalidInputException>) result).error();
		assertEquals("'test' is not a valid input", err.getMessage());
	}
}
