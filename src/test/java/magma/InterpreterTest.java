package magma;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InterpreterTest {
	@Test
	public void testInterpretEmptyInput() {
		Interpreter interpreter = new Interpreter();
		String result = interpreter.interpret("");
		assertEquals("", result, "Should return empty string for empty input");
	}

	@Test
	public void testInterpretNonEmptyInputThrows() {
		Interpreter interpreter = new Interpreter();
		assertThrows(InterpretException.class, () -> interpreter.interpret("something"),
				"Should throw InterpretException for non-empty input");
	}
}
