package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InterpreterFeatureTest {
	@Test
	public void literalInteger_returnsSameValue() {
		Interpreter interp = new Interpreter();
		Result<String, String> res = interp.interpret("5", "");

		assertTrue(res instanceof Result.Ok, "Expected Ok result for literal integer");
		Result.Ok<String, String> ok = (Result.Ok<String, String>) res;
		assertEquals("5", ok.value());
	}
}
