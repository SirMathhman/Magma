package magma;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InterpreterTest {
	@Test
	void interpretReturnsEmptyStringForEmptyInput() throws InterpretException {
		Interpreter interpreter = new Interpreter();
		assertEquals("", interpreter.interpret(""));
	}

	@Test
	void interpretThrowsExceptionForNonEmptyInput() {
		Interpreter interpreter = new Interpreter();
		assertThrows(InterpretException.class, () -> interpreter.interpret("Hello"));
	}
}
