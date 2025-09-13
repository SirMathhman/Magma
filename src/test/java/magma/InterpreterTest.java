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

	@Test
	void mixedDigits() {
		Result<String, InterpreterError> r = Interpreter.interpret("1U8");
		assertEquals(new Ok<String, InterpreterError>("1"), r);
	}
}