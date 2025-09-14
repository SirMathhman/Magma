package magma;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InterpreterTest {
	@Test
	void interpretReturnsInput() {
		Interpreter interpreter = new Interpreter();
		String input = "Hello, Magma!";
		String result = interpreter.interpret(input);
		assertEquals(input, result);
	}
}
