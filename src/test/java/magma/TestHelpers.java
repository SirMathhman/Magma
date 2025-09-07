package magma;

import magma.interpret.InterpretError;
import magma.interpret.Interpreter;

import static org.junit.jupiter.api.Assertions.*;

public final class TestHelpers {
	private TestHelpers() {
	}

	public static void assertValid(String input, String expected) {
		switch (new Interpreter().interpret(input)) {
			case Ok<String, InterpretError>(String value) -> assertEquals(expected, value);
			case Err<String, InterpretError>(InterpretError error) -> fail(error.display());
		}
	}

	public static void assertInvalid(String input) {
		switch (new Interpreter().interpret(input)) {
			case Err<String, InterpretError>(InterpretError error) -> assertNotNull(error);
			case Ok<String, InterpretError>(String value) -> fail("Expected error but got: " + value);
		}
	}
}
