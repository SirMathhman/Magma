package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CoreTest {
	@Test
	public void undefined() {
		Interpreter interp = new Interpreter();
		String source = "bad source code";

		Result<String, InterpretError> res = interp.interpret(source);

		assertInstanceOf(Result.Err.class, res);
		Result.Err<String, InterpretError> err = (Result.Err<String, InterpretError>) res;
		assertNotNull(err.error().errorReason());
		assertFalse(err.error().errorReason().isEmpty());
		assertEquals(source, err.error().sourceCode());
	}
}
