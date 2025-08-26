package magma;

import static org.junit.jupiter.api.Assertions.*;

public class TestUtils {
	static void assertValid(String source, String output) {
		Interpreter.interpret(source).consume(result -> assertEquals(output, result), error -> fail(error.display()));
	}

	static void assertInvalid(String input) {
		assertTrue(Interpreter.interpret(input).isErr());
	}
}
