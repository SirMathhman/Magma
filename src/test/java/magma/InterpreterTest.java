package magma;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class InterpreterTest {

	@ParameterizedTest
	@ValueSource(strings = { "", "0" })
	void testInterpret(String input) {
		Interpreter interpreter = new Interpreter();
		Result<String, InterpretError> result = interpreter.interpret(input);
		assertTrue(result instanceof Ok);
		if (result instanceof Ok ok) {
			assertEquals(input, ok.getValue());
		} else {
			fail("expected Ok result");
		}
	}

}
