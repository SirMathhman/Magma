package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InterpreterTest {
	@Test
	public void interpret_emptyInput_returnsEmptyString() {
		Interpreter interp = new Interpreter();
		Result<String, InterpreterError> result = interp.interpret("");
		assertEquals(true, result.isSuccess());
		assertEquals("", result.getValue().orElse("<missing>"));
	}

	@Test
	public void interpret_nonEmpty_returnsErrorResult() {
		Interpreter interp = new Interpreter();
		Result<String, InterpreterError> result = interp.interpret("hello");
		assertEquals(true, result.isError());
		// error should be present
		assertEquals(true, result.getError().isPresent());
	}

	@Test
	public void interpret_numericInput_returnsSameString() {
		Interpreter interp = new Interpreter();
		Result<String, InterpreterError> result = interp.interpret("100");
		assertEquals(true, result.isSuccess());
		assertEquals("100", result.getValue().orElse("<missing>"));
	}
}
