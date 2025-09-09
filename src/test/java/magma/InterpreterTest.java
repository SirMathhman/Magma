package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InterpreterTest {
	@Test
	public void undefined() {
		Interpreter interp = new Interpreter();
		String source = "bad source code";
		String input = "";

		Result<String, InterpretError> res = interp.interpret(source, input);

		assertTrue(res instanceof Result.Err);
		Result.Err<String, InterpretError> err = (Result.Err<String, InterpretError>) res;
		// errorReason should contain the offending source
		assertEquals(source, err.error().errorReason());
	}
}
