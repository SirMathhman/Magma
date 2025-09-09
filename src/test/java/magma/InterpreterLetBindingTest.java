package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InterpreterLetBindingTest {
	@Test
	public void unannotatedLetBinding_returnsValue() {
		Interpreter interp = new Interpreter();
		Result<String, InterpretError> res = interp.interpret("let x = 3; x", "");
		assertTrue(res instanceof Result.Ok);
		Result.Ok<String, InterpretError> ok = (Result.Ok<String, InterpretError>) res;
		assertEquals("3", ok.value());
	}

	@Test
	public void annotatedLetBinding_withTypedLiteral_returnsValue() {
		TestUtils.assertValid("let x : U8 = 3U8; x", "3");
	}
}
