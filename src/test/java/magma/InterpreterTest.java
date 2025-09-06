package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InterpreterTest {

	@Test
	void emptyString() {
		assertInterpretsTo("", "");
	}

	@Test
	void zeroString() {
		assertInterpretsTo("0", "0");
	}

	@Test
	void addExpression() {
		assertInterpretsTo("3 + 5", "8");
	}

	private static void assertInterpretsTo(String input, String expected) {
		Interpreter interpreter = new Interpreter();
		Result<String, InterpretError> result = interpreter.interpret(input);
		assertTrue(result instanceof Ok);
		if (result instanceof Ok ok) {
			assertEquals(expected, ok.getValue());
		} else {
			fail("expected Ok result");
		}
	}

}
