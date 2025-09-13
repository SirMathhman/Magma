package magma;

import org.junit.jupiter.api.Test;

import magma.Result.Err;
import magma.Result.Ok;

import static org.junit.jupiter.api.Assertions.*;

public class InterpreterTest {
	private Result<String, InterpreterError> run(String input) {
		Interpreter interp = new Interpreter();
		return interp.interpret(input);
	}

	@Test
	public void emptyInputReturns() {
		Result<String, InterpreterError> result = run("");
		assertEquals("", result.getValue().orElse("<missing>"));
	}

	@Test
	public void nonEmptyInputIsError() {
		Result<String, InterpreterError> result = run("test");
		// Expect an error for non-empty input (interpreter not implemented for this
		// input)
		assertTrue(result instanceof Err, "expected error but got success: " + result.getValue().orElse("<missing>"));
		InterpreterError err = result.getError().orElseThrow();
		// The current Interpreter returns a generic 'not implemented' error with the
		// source code
		assertEquals("not implemented", err.getMessage());
		assertEquals("test", err.getSourceCode());
	}

	@Test
	public void numericReturns() {
		Result<String, InterpreterError> result = run("5");
		assertTrue(result instanceof Ok, "expected success for numeric literal");
		assertEquals("5", result.getValue().orElse("<missing>"));
	}
}
