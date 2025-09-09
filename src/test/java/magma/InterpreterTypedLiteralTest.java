package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InterpreterTypedLiteralTest {
	@Test
	public void typedIntegerSuffix_returnsBaseValue() {
		Interpreter interp = new Interpreter();
		Result<String, String> res = interp.interpret("5I32", "");

		assertTrue(res instanceof Result.Ok, "Expected Ok result for typed integer literal");
		Result.Ok<String, String> ok = (Result.Ok<String, String>) res;
		assertEquals("5", ok.value());
	}
}
