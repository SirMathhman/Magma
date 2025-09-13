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
