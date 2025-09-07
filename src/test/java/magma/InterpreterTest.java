package magma;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InterpreterTest {
	@Test
	public void empty() {
		Interpreter interpreter = new Interpreter();
		String input = "";
		String result = interpreter.interpret(input);
		assertEquals(input, result);
	}

	@Test
	public void interpretFive() {
		Interpreter interpreter = new Interpreter();
		String input = "5";
		String result = interpreter.interpret(input);
		assertEquals(input, result);
	}
}
