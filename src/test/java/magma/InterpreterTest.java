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
		assertEquals("", result.getValue().orElse("<missing>"));
	}
}
