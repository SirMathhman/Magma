package magma;

// ...existing imports...
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

public class InterpreterTest {
	@ParameterizedTest
	@CsvSource({
			"'', ''",
			"0, 0",
			"'3 + 5', 8",
			"'let x : I32 = 10; x', 10",
			"'let mut x : I32 = 0; x = 10; x', 10",
			"true, true",
			"'if (true) 3 else 5', 3"
	})
	void interpretsCases(String input, String expected) {
		assertInterpretsTo(input, expected);
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
