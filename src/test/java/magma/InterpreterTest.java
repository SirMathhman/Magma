package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InterpreterTest {

	@Test
	public void undefined() {
		Interpreter interp = new Interpreter();
		String source = "bad source code";
		String input = "";

		Result<String, String> res = interp.interpret(source, input);

		assertTrue(res instanceof Result.Err);
		Result.Err<String, String> err = (Result.Err<String, String>) res;
		assertEquals(source, err.error());
	}
}
