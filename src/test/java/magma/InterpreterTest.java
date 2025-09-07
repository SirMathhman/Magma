package magma;

import magma.interpret.InterpretError;
import magma.interpret.Interpreter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InterpreterTest {
	@Test
	void empty() {
		assertInterpretsTo("", "");
	}

	@Test
	void undefined() {
		assertInstanceOf(Err.class, new Interpreter().interpret("test"));
	}

	@Test
	void numberLiteral() {
		assertInterpretsTo("5", "5");
	}

	@Test
	void numberLiteralWithTrailing() {
		assertInterpretsTo("5I32", "5");
	}

	@Test
	void additionSimple() {
		assertInterpretsTo("2 + 3", "5");
	}

	@Test
	void additionWithTrailingType() {
		assertInterpretsTo("2 + 3I32", "5");
	}

	@Test
	void additionMixedUnsignedAndSigned() {
		final var interpreter = new Interpreter();
		assertInstanceOf(Err.class, interpreter.interpret("2U8 + 3I32"));
	}

	private static void assertInterpretsTo(String input, String expected) {
		switch (new Interpreter().interpret(input)) {
			case Ok<String, InterpretError>(String value) -> assertEquals(expected, value);
			case Err<String, InterpretError>(InterpretError error) -> fail(error.display());
		}
	}
}
