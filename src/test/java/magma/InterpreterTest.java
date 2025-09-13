package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InterpreterTest {
	@Test
	void empty() {
		Result<String, InterpreterError> r = Interpreter.interpret("");
		assertEquals(new Ok<String, InterpreterError>(""), r);
	}

	@Test
	void integer() {
		Result<String, InterpreterError> r = Interpreter.interpret("1");
		assertEquals(new Ok<String, InterpreterError>("1"), r);
	}
}