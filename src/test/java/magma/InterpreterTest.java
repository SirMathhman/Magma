package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InterpreterTest {
	private Result<String, InterpreterError> run(String input) {
		Interpreter interp = new Interpreter();
		return interp.interpret(input);
	}

	@Test
	public void interpret_emptyInput_returnsEmptyString() {
		Result<String, InterpreterError> result = run("");
		assertEquals(true, result.isSuccess());
		assertEquals("", result.getValue().orElse("<missing>"));
	}

	@Test
	public void interpret_nonEmpty_returnsErrorResult() {
		Result<String, InterpreterError> result = run("hello");
		assertEquals(true, result.isError());
		// error should be present
		assertEquals(true, result.getError().isPresent());
	}

	@Test
	public void interpret_numericInput_returnsSameString() {
		Result<String, InterpreterError> result = run("100");
		assertEquals(true, result.isSuccess());
		assertEquals("100", result.getValue().orElse("<missing>"));
	}
}
