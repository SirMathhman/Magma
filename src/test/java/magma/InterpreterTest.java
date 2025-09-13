package magma;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import magma.Result.Err;
import magma.Result.Ok;

public class InterpreterTest {
	@Test
	public void emptyInputReturns() {
		assertSuccess(new Interpreter().interpret(""), "");
	}

	@Test
	public void test() {
		// non-empty input should produce an error in the current interpreter stub
		assertError(new Interpreter().interpret("invalid"));
	}

	@Test
	public void one() {
		// simple numeric input "1" should return the same string as success
		assertSuccess(new Interpreter().interpret("1"), "1");
	}

	@Test
	public void two() {
		// simple numeric input "2" should return the same string as success
		assertSuccess(new Interpreter().interpret("2"), "2");
	}

	@Test
	public void addition() {
		// test that a simple addition expression evaluates correctly
		assertSuccess(new Interpreter().interpret("3 + 5"), "8");
	}

	@Test
	public void letAssignment() {
		// test simple let assignment and lookup
		assertSuccess(new Interpreter().interpret("let x = 10; x"), "10");
	}

	@Test
	public void letMutAssignment() {
		// test mutable let and reassignment
		assertSuccess(new Interpreter().interpret("let mut x = 0; x = 10; x"), "10");
	}

	@Test
	public void booleanLiteralTrue() {
		assertSuccess(new Interpreter().interpret("true"), "true");
	}

	// Helper to assert a successful interpretation with expected value
	private void assertSuccess(Result<String, InterpreterError> result, String expected) {
		switch (result) {
			case Err<String, InterpreterError>(InterpreterError error) -> fail(error.display());
			case Ok<String, InterpreterError>(String value) -> assertEquals(expected, value);
		}
	}

	// Helper to assert an error result
	private void assertError(Result<String, InterpreterError> result) {
		switch (result) {
			case Ok<String, InterpreterError>(String value) -> fail("expected error but got success: " + value);
			case Err<String, InterpreterError>(InterpreterError error) -> assertNotNull(error);
		}
	}
}
