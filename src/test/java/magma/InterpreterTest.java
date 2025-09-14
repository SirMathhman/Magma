package magma;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InterpreterTest {
	@Test
	void interpretReturnsThreeForIfTrueElseFive() throws InterpretException {
		Interpreter interpreter = new Interpreter();
		assertEquals("3", interpreter.interpret("if (true) 3 else 5"));
	}

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

	@Test
	void interpretReturnsFiveForFiveInput() throws InterpretException {
		Interpreter interpreter = new Interpreter();
		assertEquals("5", interpreter.interpret("5"));
	}

	@Test
	void interpretReturnsFiveForLetXEqualsFiveX() throws InterpretException {
		Interpreter interpreter = new Interpreter();
		assertEquals("5", interpreter.interpret("let x = 5; x"));
	}

	@Test
	void interpretReturnsTenForLetMutXAssignment() throws InterpretException {
		Interpreter interpreter = new Interpreter();
		assertEquals("10", interpreter.interpret("let mut x = 0; x = 10; x"));
	}

	@Test
	void interpretReturnsTrueForTrueInput() throws InterpretException {
		Interpreter interpreter = new Interpreter();
		assertEquals("true", interpreter.interpret("true"));
	}
}
