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
}
