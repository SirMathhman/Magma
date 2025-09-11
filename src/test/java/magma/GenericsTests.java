package magma;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class GenericsTests {
	@Test
	public void genericFnPassesValue() {
		Result<String, InterpretError> r = new Interpreter().interpret(
				"fn pass<T>(value : T) => value; pass(100)");
		if (r instanceof Result.Err) {
			fail("Interpreter returned error: " + ((Result.Err<String, InterpretError>) r).error());
		}
		assertEquals("100", ((Result.Ok<String, InterpretError>) r).value());
	}

	@Test
	public void genericFnWithTwoTypeParamsReturnsFirst() {
		Result<String, InterpretError> r = new Interpreter().interpret(
				"fn pass2<T, U>(a : T, b : U) => a; pass2(100, true)");
		if (r instanceof Result.Err) {
			fail("Interpreter returned error: " + ((Result.Err<String, InterpretError>) r).error());
		}
		assertEquals("100", ((Result.Ok<String, InterpretError>) r).value());
	}
}
