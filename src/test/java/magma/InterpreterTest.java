package magma;

import magma.Result.Err;
import magma.Result.Ok;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InterpreterTest {
	@Test
	public void emptyInputReturns() {
		assertSuccess(new Interpreter().interpret(""), "");
	}

	@Test
	public void nonEmptyInputIsError() {
		assertError(new Interpreter().interpret("test"));
	}

	@Test
	public void numericReturns() {
		assertSuccess(new Interpreter().interpret("5"), "5");
	}

	@Test
	public void numWithSuffReturns() {
		// Expect that an input with trailing letters keeps the numeric prefix only
		assertSuccess(new Interpreter().interpret("5U8"), "5");
	}

	@Test
	public void addNumbersReturnsSum() {
		// Simple addition expression with spaces should evaluate to the numeric sum
		assertSuccess(new Interpreter().interpret("1 + 2"), "3");
	}

	// Helper to assert a successful interpretation with expected value
	private void assertSuccess(Result<String, InterpreterError> result, String expected) {
		switch (result) {
			case Err<String, InterpreterError>(InterpreterError error) -> fail("expected success but got error: " + error);
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
