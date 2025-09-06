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

	@Test
	void letBinding() {
		assertInterpretsTo("let x : I32 = 3; x", "3");
	}

	@Test
	void letMutableBinding() {
		assertInterpretsTo("let mut x : I32 = 0; x = 3; x", "3");
	}

	@Test
	void trueLiteral() {
		assertInterpretsTo("true", "true");
	}

	@Test
	void ifExpressionTrue() {
		assertInterpretsTo("if (true) 3 else 5", "3");
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
