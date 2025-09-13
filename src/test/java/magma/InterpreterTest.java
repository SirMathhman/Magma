package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class InterpreterTest {
	@Test
	public void interpret_emptyInput_returnsEmptyString() {
		Interpreter interp = new Interpreter();
		String result = interp.interpret("");
		assertEquals("", result);
	}

	@Test
	public void interpret_nonEmpty_throwsInterpreterError() {
		Interpreter interp = new Interpreter();
		assertThrows(InterpreterError.class, () -> interp.interpret("hello"));
	}
}
