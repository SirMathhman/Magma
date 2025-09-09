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

	@Test
	public void unsigned8_maxValue_ok() {
		Interpreter interp = new Interpreter();
		Result<String, String> res = interp.interpret("255U8", "");
		assertTrue(res instanceof Result.Ok);
		Result.Ok<String, String> ok = (Result.Ok<String, String>) res;
		assertEquals("255", ok.value());
	}

	@Test
	public void unsigned8_overflow_err() {
		Interpreter interp = new Interpreter();
		Result<String, String> res = interp.interpret("256U8", "");
		assertTrue(res instanceof Result.Err);
	}

	@Test
	public void signed8_minValue_ok() {
		Interpreter interp = new Interpreter();
		Result<String, String> res = interp.interpret("-128I8", "");
		assertTrue(res instanceof Result.Ok);
		Result.Ok<String, String> ok = (Result.Ok<String, String>) res;
		assertEquals("-128", ok.value());
	}

	@Test
	public void unsignedNegative_err() {
		Interpreter interp = new Interpreter();
		Result<String, String> res = interp.interpret("-1U8", "");
		assertTrue(res instanceof Result.Err);
	}

	@Test
	public void unsigned64_large_ok() {
		Interpreter interp = new Interpreter();
		// max U64 is 18446744073709551615
		Result<String, String> res = interp.interpret("18446744073709551615U64", "");
		assertTrue(res instanceof Result.Ok);
		Result.Ok<String, String> ok = (Result.Ok<String, String>) res;
		assertEquals("18446744073709551615", ok.value());
	}

	@Test
	public void signed64_overflow_err() {
		Interpreter interp = new Interpreter();
		// one more than I64 max (9223372036854775807)
		Result<String, String> res = interp.interpret("9223372036854775808I64", "");
		assertTrue(res instanceof Result.Err);
	}
}
