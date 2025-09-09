package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InterpreterTest {

	@Test
	public void interpret_alwaysThrowsInterpretException() {
		Interpreter interp = new Interpreter();
		String source = "bad source code";
		String input = "";

		InterpretException ex = assertThrows(InterpretException.class, () -> {
			interp.interpret(source, input);
		});

		// The exception message should be formatted as: message + ": " + source
		assertTrue(ex.getMessage().contains("Invalid source"));
		assertTrue(ex.getMessage().endsWith(source));
		// getSource() should return the original source string
		assertEquals(source, ex.getSource());
	}
}
