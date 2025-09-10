package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InterpreterTest {
	@Test
	public void undefined() {
		Interpreter interp = new Interpreter();
		String source = "bad source code";
		String input = "";

		Result<String, InterpretError> res = interp.interpret(source);

		assertInstanceOf(Result.Err.class, res);
		Result.Err<String, InterpretError> err = (Result.Err<String, InterpretError>) res;
		// InterpretError should provide a human-readable reason and preserve the original source
		assertNotNull(err.error().errorReason());
		assertFalse(err.error().errorReason().isEmpty());
		assertEquals(source, err.error().sourceCode());
	}
}
