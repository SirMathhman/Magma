package magma;

import magma.interpret.InterpretError;
import magma.interpret.Interpreter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InterpreterTest {
	@Test
	void empty() {
		assertValid("", "");
	}

	@Test
	void undefined() {
		assertInvalid("test");
	}

	@Test
	void numberLiteral() {
		assertValid("5", "5");
	}

	@Test
	void numberLiteralWithTrailing() {
		assertValid("5I32", "5");
	}

	@Test
	void additionSimple() {
		assertValid("2 + 3", "5");
	}

	@Test
	void additionWithTrailingType() {
		assertValid("2 + 3I32", "5");
	}

	@Test
	void subtractionSimple() {
		assertValid("5 - 3", "2");
	}

	@Test
	void multiplicationSimple() {
		assertValid("5 * 3", "15");
	}

	@Test
	void chainedAddition() {
		assertValid("1 + 2 + 3", "6");
	}

	@Test
	void additionMixedUnsignedAndSigned() {
		assertInvalid("2U8 + 3I32");
	}

	private static void assertValid(String input, String expected) {
		switch (new Interpreter().interpret(input)) {
			case Ok<String, InterpretError>(String value) -> assertEquals(expected, value);
			case Err<String, InterpretError>(InterpretError error) -> fail(error.display());
		}
	}

	private static void assertInvalid(String input) {
		switch (new Interpreter().interpret(input)) {
			case Err<String, InterpretError>(InterpretError error) -> assertNotNull(error);
			case Ok<String, InterpretError>(String value) -> fail("Expected error but got: " + value);
		}
	}
}
